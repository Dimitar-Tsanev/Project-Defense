import { Routes } from '@angular/router';
import {MainComponent} from './pages/main/main.component';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: MainComponent },
];
