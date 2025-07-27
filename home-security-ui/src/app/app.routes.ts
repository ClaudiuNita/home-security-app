import { Routes } from '@angular/router';
import { FileManagerComponent } from './components/file-manager/file-manager.component';
import { NvrStreamComponent } from './components/nvr-stream/nvr-stream.component';

export const routes: Routes = [
    { path: 'files', component: FileManagerComponent },
    { path: 'stream', component: NvrStreamComponent }
];
