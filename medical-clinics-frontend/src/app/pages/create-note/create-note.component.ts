import {Component, OnInit} from '@angular/core';
import {FormsModule, NgForm} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../../services/my-services/auth-service';
import {MedicalRecordControllerService} from '../../services/services/medical-record-controller.service';
import {NewNoteRequest} from '../../services/models/new-note-request';
import {PatientControllerService} from '../../services/services/patient-controller.service';
import {PatientInfo} from '../../services/models/patient-info';
import {ErrorMessagesComponent} from '../../common/error-messages/error-messages.component';
import {ErrorMessagesService} from '../../services/my-services/error-messages-service';
import {HttpClient, HttpParams} from '@angular/common/http';

@Component({
  selector: 'app-create-note',
  imports: [
    FormsModule,
    RouterLink
  ],
  templateUrl: './create-note.component.html',
  styleUrl: './create-note.component.css'
})
export class CreateNoteComponent implements OnInit {
  accountId: string = '';
  patient: PatientInfo | undefined = undefined;
  patients: PatientInfo[] = [];
  found: boolean = true;

  constructor(
    private router: Router,
    private noteService: MedicalRecordControllerService,
    private authService: AuthService,
    private http: HttpClient,
    private patientService: PatientControllerService,
  ) {
  }

  ngOnInit(): void {
    const user = this.authService.getUser();
    if (user) {
      this.accountId = user.accountId;
    }
  }

  createNote(form: NgForm) {
    if (form.invalid) {
      return;
    }

    const note: NewNoteRequest = {
      chiefComplaint: form.value.chiefComplaint.length > 0 ? form.value.chiefComplaint : null,
      diagnosis: form.value.diagnosis,
      diagnosisCode: form.value.diagnosisCode.length > 0 ? form.value.diagnosisCode : null,
      examination: form.value.examination,
      medicalHistory: form.value.medicalHistory,
      medicationAndRecommendations:
        form.value.medicationAndRecommendations.length > 0 ?
          form.value.medicationAndRecommendations :
          null,
      testResults: form.value.testResults.length > 0 ? form.value.testResults : null
    }

    this.noteService.addNewNote({
      'accountId': this.accountId as string,
      'patientId': this.patient?.patientId as string,
      body: note
    }).subscribe({
      next: () => {
        this.router.navigate(['/physician-notes']);
      }
    });
  }

  loadPatient(findForm: NgForm) {
    const url = 'http://localhost:8080/api/v0/patients/filter'

    let options = {
      params: new HttpParams().set('phoneNumber', findForm.value.phone)
        .append('email', findForm.value.email)
    }

    return this.http.get<PatientInfo[]>(url, options)
      .subscribe({
        next: (res) => {
          if (res.length === 0) {
            this.found = false;

          } else if (res.length === 1) {
            this.patient = res[0];

          } else {
            this.patients = res;
          }
        }
      })
  }

  selectPatient(position: number) {
    this.patient = this.patients[position];
    this.patients = [];
  }

  editPatient(editForm: NgForm) {
    if (this.patient && editForm.valid) {
      this.patientService.setPatientCountryAndIdentificationCode({
        'identificationCode': editForm.value.identificationCode as string,
        'country': editForm.value.country as string,
        'patientId': this.patient.patientId as string
      }).subscribe({
        next: () => {
          this.router.navigate(['/create-note']);
        }
      });
    }
    return;
  }
}
