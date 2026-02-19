import { Component } from '@angular/core';
import { FileData } from '../../models/FileData';
import { FileManagerService } from '../../services/file-manager.service';
import { CommonModule } from '@angular/common';
import { lastValueFrom } from 'rxjs';

@Component({
  selector: 'app-file-manager',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './file-manager.component.html',
  styleUrl: './file-manager.component.css'
})
export class FileManagerComponent {
  
  files: FileData[] = [];
  filteredFiles: FileData[] = [];
  allCameraNr = new Set();
  totalSize: string | undefined;
  selectedDate = '';
  showModal = false;
  videoUrl = '';
  
  sortColumn = '';
  sortDirection: 'asc' | 'desc' = 'asc';

  constructor(private fileManager: FileManagerService) {}

  async ngOnInit() {
    if (this.selectedDate == '') {
      await lastValueFrom(this.fileManager.getFiles()).then(
        files => this.files = files,
        error => {
          if (error.name === 'HttpErrorResponse') 
            window.location.href = this.fileManager.getLoginUrl()
        }
      );
    } else {
      this.getFilesByDate(this.selectedDate);
    }

    this.filteredFiles = this.files;
    this.totalSize = Intl.NumberFormat('en-us', {minimumFractionDigits: 2}).format(this.files.reduce((sum, file) => sum + file.size, 0));
    this.allCameraNr = new Set(this.files.map(file => file.cameraNr));
  }

  sortBy(column: any) {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }

    this.files.sort((a: any, b: any) => {
      let valA = a[column];
      let valB = b[column];

      if (column === 'created') {
        valA = new Date(a[column]).getTime();
        valB = new Date(b[column]).getTime();
      }

      if (valA < valB) return this.sortDirection === 'asc' ? -1 : 1;
      if (valA > valB) return this.sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
  }

  selectedValues: number[] = [];

  onCameraSelect(event: any) {
    const value = event.target.value;
    const isChecked = event.target.checked;
    
    if (isChecked) {
      this.selectedValues.push(value);
    } else {
      this.selectedValues = this.selectedValues.filter(v => v !== value);
    }

    this.filteredFiles = this.files;
    if (this.selectedValues.length > 0)
      this.filteredFiles = this.files.filter(file => this.selectedValues.find(v => v == file.cameraNr))

    this.totalSize = Intl.NumberFormat('en-us', {minimumFractionDigits: 2}).format(this.filteredFiles.reduce((sum, file) => sum + file.size, 0));
  }

  async getFilesByDate(selectedDate: string) {
    await lastValueFrom(this.fileManager.getFilesByDate(selectedDate)).then(
      files => this.files = files      
    );

    this.filteredFiles = this.files;
    this.totalSize = Intl.NumberFormat('en-us', {minimumFractionDigits: 2}).format(this.files.reduce((sum, file) => sum + file.size, 0));
    this.allCameraNr = new Set(this.files.map(file => file.cameraNr));
  }

  downloadFile(file: FileData) {
    this.fileManager.downloadFile(file);
  }

  deleteFile(file: FileData) {
    this.fileManager.deleteFile(file).subscribe(
      () => window.location.reload()
    );
  }

  getVideoUrl(fileName: string) {
    this.showModal = true;
    this.videoUrl = this.fileManager.getVideoUrl(fileName);;
  }

  getVideoThumbnailUrl(fileName: string) {
    return this.fileManager.getVideoUrl(fileName);
  }
}
