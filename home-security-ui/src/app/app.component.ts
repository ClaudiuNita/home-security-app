import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { FileManagerService } from './services/file-manager.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  logoutUrl = '';

  constructor(private fileManager: FileManagerService) {}

  ngOnInit() {
    this.logoutUrl = this.fileManager.getLogoutUrl();
  }
}
