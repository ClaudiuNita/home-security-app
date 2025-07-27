import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FileData } from '../models/FileData';

@Injectable({
  providedIn: 'root'
})
export class FileManagerService {

  private baseUrl = 'http://localhost:8080';
  private fileUrl = `${this.baseUrl}/api/files`;

  constructor(private http: HttpClient) { }

  getFiles(): Observable<FileData[]> {
    return this.http.get<FileData[]>(this.fileUrl, { withCredentials: true });
  }

  getFilesByDate(selectedDate: string): Observable<FileData[]> {
    return this.http.get<FileData[]>(this.fileUrl, { params: new HttpParams().set("selectedDate", selectedDate), withCredentials: true });
  }
  
  deleteFile(file: FileData): Observable<FileData> {
    return this.http.delete<FileData>(this.fileUrl, { body: file, withCredentials: true });
  }

  getVideoUrl(fileName: string) {
    return `${this.fileUrl}/video/${fileName}`;
  }

  getLoginUrl() {
    return `${this.baseUrl}/login`;
  }

  getLogoutUrl() {
    return `${this.baseUrl}/logout`;
  }

  downloadFile(file: FileData): void {
    this.http.post(`${this.fileUrl}/download`, file, {
      responseType: 'blob',
      withCredentials: true
    }).subscribe(
      blob => {
        const fileName = file.name || 'download';
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        a.click();
        window.URL.revokeObjectURL(url);
    }, error => {
      console.error('Download failed', error);
    });
  }
}
