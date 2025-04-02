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
import {UsersComponent} from './pages/users/users.component';
import {AdminGuard} from './guard/admin.guard';
import {MedicalRecordComponent} from './pages/medical-record/medical-record.component';
import {CredentialsGuard} from './guard/Credentials.guard';
import {PhysicianNotesComponent} from './pages/physician-notes/physician-notes.component';
import {PageNotFoundComponent} from './pages/errors/page-not-found/page-not-found.component';
import {ErrorMessagesComponent} from './common/error-messages/error-messages.component';
import {ForbiddenComponent} from './pages/errors/forbidden/forbidden.component';
import {InternalServerErrorComponent} from './pages/errors/internal-server-error/internal-server-error.component';

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
  {path: 'physicians', component: PhysiciansListComponent},
  {
    path: 'physician',
    children: [
      {path: 'physician', component: PhysiciansListComponent},
      {
        path: ':physicianId',
        component: PhysicianComponent
      }
    ]
  },
  {path: 'register', component: RegisterComponent},
  {path: 'login', component: LoginComponent},
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
  {path: 'users', component: UsersComponent, canActivate: [AdminGuard]},
  {path: 'my-medical-record', component: MedicalRecordComponent, canActivate: [AuthGuard]},
  {path: 'physician-notes', component: PhysicianNotesComponent, canActivate: [CredentialsGuard]},

  { path: 'errors', component: ErrorMessagesComponent },
  { path: 'not-found', component: PageNotFoundComponent },
  { path: 'forbidden', component: ForbiddenComponent },
  { path: 'forbidden', component: InternalServerErrorComponent },
  { path: '**', redirectTo: '/not-found'},
];
