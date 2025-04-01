import {Component} from '@angular/core';
import {ClinicListComponent} from '../clinic-list/clinic-list.component';

@Component({
  selector: 'app-main',
    imports: [
        ClinicListComponent
    ],
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent {

}
