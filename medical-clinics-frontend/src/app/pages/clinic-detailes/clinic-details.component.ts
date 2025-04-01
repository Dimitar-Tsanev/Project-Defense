import {Component, OnInit} from '@angular/core';
import {ClinicControllerService} from '../../services/services/clinic-controller.service';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {ClinicDetails} from '../../services/models/clinic-details';
import {LoaderComponent} from '../../common/loader/loader.component';


@Component({
  selector: 'app-clinic-details',
  imports: [
    RouterLink,
    LoaderComponent
  ],
  templateUrl: './clinic-details.component.html',
  styleUrl: './clinic-details.component.css'
})
export class ClinicDetailsComponent implements OnInit {
  isLoading = true;
  clinic: ClinicDetails | null = null;
  clinicId: String | undefined = undefined;

  constructor(private clinicService: ClinicControllerService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    const id = this.route.snapshot.params['clinicId'];
    this.clinicService.getClinicInfo({ 'clinicId':id as string}).subscribe({
      next: (res) => {
        this.clinic = res;
        this.isLoading = false;
      }
    });
  }
}
