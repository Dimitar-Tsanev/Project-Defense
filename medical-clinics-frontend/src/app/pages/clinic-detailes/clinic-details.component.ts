import {Component, OnInit} from '@angular/core';
import {ClinicControllerService} from '../../services/services/clinic-controller.service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {ClinicDetails} from '../../services/models/clinic-details';
import {LoaderComponent} from '../../common/loader/loader.component';
import {AuthService} from '../../services/my-services/auth-service';
import {ObjectTransferService} from '../../services/my-services/object-transfer-service';


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
  clinic: ClinicDetails | undefined = undefined;
  clinicId: string = '';
  isAdmin: boolean = false;

  constructor(
    private clinicService: ClinicControllerService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private objectTransfer: ObjectTransferService
  ) {
  }

  ngOnInit(): void {
    this.clinicId = this.activatedRoute.snapshot.params['clinicId'];
    this.isAdmin = this.authService.isAdmin()

    this.clinicService.getClinicInfo({'clinicId': this.clinicId}).subscribe({
      next: (res) => {
        this.clinic = res;
        this.isLoading = false;
      }
    });
  }

  deleteClinic() {
    this.clinicService.deleteClinic({'clinicId': this.clinicId}).subscribe({
      next: () => this.router.navigate(['clinics'])
    })
  }

  editClinic() {
    this.objectTransfer.setObject(this.clinic);
    this.router.navigate(['add-edit-clinic', this.clinicId]);
  }
}

