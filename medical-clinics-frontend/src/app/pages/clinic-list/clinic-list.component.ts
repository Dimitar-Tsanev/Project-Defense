import {Component, OnInit} from '@angular/core';
import {ClinicShortInfo} from '../../services/models/clinic-short-info';
import {LoaderComponent} from '../../common/loader/loader.component';
import {ClinicControllerService} from '../../services/services/clinic-controller.service';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-clinic-list',
  imports: [LoaderComponent, RouterLink],
  templateUrl: './clinic-list.component.html',
  styleUrl: './clinic-list.component.css'
})
export class ClinicListComponent implements OnInit {
  clinics: Array<ClinicShortInfo> = [];
  isLoading = true;
  clinicCount: number = this.clinics.length;

  constructor(private clinicService: ClinicControllerService) {
  }

  ngOnInit() {
    this.getAllClinics()
  }

  private getAllClinics() {
    this.clinicService.getAllClinics().subscribe({
      next: (res) => {
        this.clinics = res;
        this.clinicCount = this.clinics.length;
        this.isLoading = false;
      }
    })
  }
}
