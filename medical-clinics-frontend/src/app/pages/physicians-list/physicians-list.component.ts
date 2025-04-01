import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {PhysicianInfo} from '../../services/models/physician-info';
import {PhysicianControllerService} from '../../services/services/physician-controller.service';
import {LoaderComponent} from '../../common/loader/loader.component';

@Component({
  selector: 'app-physicians-list',
  imports: [
    LoaderComponent,
    RouterLink
  ],
  templateUrl: './physicians-list.component.html',
  styleUrl: './physicians-list.component.css'
})
export class PhysiciansListComponent implements OnInit {
  isLoading = true;
  physicians: Array<PhysicianInfo> = [];

  constructor(
    private route: ActivatedRoute,
    private physicianService: PhysicianControllerService
  ) {
  }

  ngOnInit(): void {
    let clinicId: string = this.route.snapshot.params['clinicId'];
    let specialityId: string = this.route.snapshot.params['specialityId'];

    this.route.queryParams.subscribe((paramMap) => {
      clinicId = paramMap['clinic'];
      specialityId = paramMap['speciality'];
    });

    this.physicianService.getPhysiciansByClinicAndSpeciality({
      'clinicId': clinicId as string,
      'specialityId': specialityId as string,
    }).subscribe({
      next: (res) => {
        this.physicians = res;
        this.isLoading = false;
      }
    });
  }
}
