import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {PhysicianControllerService} from '../../services/services/physician-controller.service';
import {FormsModule, NgForm} from '@angular/forms';
import {EmailDirective} from '../../utils/validators/email.directive';
import {UrlDirective} from '../../utils/validators/url.directive';
import {CreatePhysician} from '../../services/models/create-physician';

@Component({
  selector: 'app-add-physician',
  imports: [
    EmailDirective,
    FormsModule,
    UrlDirective
  ],
  templateUrl: './add-physician.component.html',
  styleUrl: './add-physician.component.css'
})
export class AddPhysicianComponent implements OnInit {
  workplaceCity: string = '';
  workplaceAddress: string = '';
  pictureUrl: string = '';

  specialties: Map<string, string> = new Map([
    ['Dermatology','DERMATOLOGY'],
    ['Family medicine','FAMILY_MEDICINE'],
    ['Internal medicine', 'INTERNAL_MEDICINE'],
    ['Gynaecology', 'GYNAECOLOGY'],
    ['Neurology', 'NEUROLOGY'],
    ['Allergist', 'ALLERGIST'],
    ['Cardiology', 'CARDIOLOGY'],
    ['Psychiatry', 'PSYCHIATRY'],
    ['Gastroenterology', 'GASTROENTEROLOGY'],
    ['Pediatrics', 'PEDIATRICS'],
    ['Endocrinology', 'ENDOCRINOLOGY'],
    ['Ophthalmology', 'OPHTHALMOLOGY'],
    ['Orthopedics', 'ORTHOPEDICS'],
  ]);

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private physicianService: PhysicianControllerService
  ) {
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.workplaceCity = params['workplaceCity'];
      this.workplaceAddress = params['workplaceAddress']
    })
  }

  addPhysician(form: NgForm) {
    if (form.invalid) {
      return;
    }

    const physician: CreatePhysician = {
      abbreviation: form.value.abbreviation ,
      description: form.value.description ,
      email: form.value.email,
      firstName: form.value.firstName,
      identificationNumber: form.value.identificationNumber,
      lastName: form.value.lastName,
      pictureUrl: form.value.pictureUrl,
      specialty: form.value.specialty,
      workplaceAddress: this.workplaceAddress,
      workplaceCity: this.workplaceCity,
    }

    this.physicianService.addPhysician$Response({body: physician}).subscribe({
      next: (res) => {
        const location: string | null = res.headers.get('location');
        if (location) {
          const physicianId = location.split('/').pop();
          this.router.navigate(['/physician', physicianId]);
        }
      }
    });
  }
}
