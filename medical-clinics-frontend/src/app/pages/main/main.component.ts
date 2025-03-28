import {Component, OnInit} from '@angular/core';
import {ClinicControllerService} from '../../services/services/clinic-controller.service';
import {ClinicShortInfo} from '../../services/models/clinic-short-info';
import {ClinicListComponent} from '../../common/clinic-list/clinic-list.component';

@Component({
  selector: 'app-main',
  imports: [ClinicListComponent],
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent implements OnInit {

  clinics: Array<ClinicShortInfo> = [];

  constructor(private clinicService: ClinicControllerService) {

  }

  ngOnInit() {
    this.getAllClinics()
  }

  private getAllClinics() {
    this.clinicService.getAllClinics().subscribe({
      next: (res) => {
        this.clinics = res;
      }
    })
  }
}
