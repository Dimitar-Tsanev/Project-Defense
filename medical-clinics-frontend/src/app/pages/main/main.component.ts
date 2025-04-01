import {Component} from '@angular/core';
import {ClinicListComponent} from '../clinic-list/clinic-list.component';
import {AuthenticationComponent} from "../../common/authentication/authentication.component";
import {EditProfileComponent} from "../edit-profile/edit-profile.component";
import {ProfileComponent} from "../profile/profile.component";

@Component({
  selector: 'app-main',
    imports: [
        ClinicListComponent,
        AuthenticationComponent,
        EditProfileComponent,
        ProfileComponent
    ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent {

}
