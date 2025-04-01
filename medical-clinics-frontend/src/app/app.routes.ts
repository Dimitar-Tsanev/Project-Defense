import {Routes} from '@angular/router';
import {MainComponent} from './pages/main/main.component';
import {ClinicDetailsComponent} from './pages/clinic-detailes/clinic-details.component';
import {ClinicListComponent} from './pages/clinic-list/clinic-list.component';
import {PhysiciansListComponent} from './pages/physicians-list/physicians-list.component';
import {PhysicianComponent} from './pages/physician/physician.component';
import {LoginComponent} from './pages/login/login.component';
import {RegisterComponent} from './pages/register/register.component';
import {ProfileComponent} from './pages/profile/profile.component';
import {EditProfileComponent} from './pages/edit-profile/edit-profile.component';
import {AuthGuard} from './guard/auth.guard';

export const routes: Routes = [
  {path: '', redirectTo: '/clinics', pathMatch: 'full'},
  {path: 'clinics', component: MainComponent},
  {
    path: 'clinic',
    children: [
      {path: 'clinic', component: ClinicListComponent},
      {
        path: ':clinicId',
        component: ClinicDetailsComponent
      }
    ]
  },
  { path: 'physicians', component: PhysiciansListComponent},
  {
    path: 'physician',
    children:[
      {path: 'physician', component: PhysiciansListComponent},
      {
        path: ':physicianId',
        component: PhysicianComponent
      }
    ]
  },
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  {path: 'profile', component: ProfileComponent, canActivate: [AuthGuard]},
  {
    path: 'profile/edit',
    children: [
      {path: 'profile/edit', component: ProfileComponent},
      {
        path: ':userAccountId',
        component: EditProfileComponent,
        canActivate: [AuthGuard]
      }
    ]
  },
];
