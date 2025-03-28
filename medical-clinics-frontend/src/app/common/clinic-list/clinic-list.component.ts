import {Component, input, InputSignal} from '@angular/core';
import {ClinicShortInfo} from '../../services/models/clinic-short-info';

@Component({
  selector: 'app-clinic-list',
  imports: [],
  templateUrl: './clinic-list.component.html',
  styleUrl: './clinic-list.component.css'
})
export class ClinicListComponent {
  clinics: InputSignal<ClinicShortInfo[]> = input<ClinicShortInfo[]>([]);
}
