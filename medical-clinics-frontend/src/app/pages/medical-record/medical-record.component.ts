import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/my-services/auth-service';
import {MedicalRecordControllerService} from '../../services/services/medical-record-controller.service';
import {NoteResponse} from '../../services/models/note-response';
import {Router, RouterLink} from '@angular/router';
import {LoaderComponent} from '../../common/loader/loader.component';

@Component({
  selector: 'app-medical-record',
  imports: [
    LoaderComponent,
    RouterLink
  ],
  templateUrl: './medical-record.component.html',
  styleUrl: './medical-record.component.css'
})
export class MedicalRecordComponent implements OnInit {

  patientId: string = '';
  record: Array<NoteResponse> = [];
  isLoading: boolean = true;

  constructor(
    private authService: AuthService,
    private recordsService: MedicalRecordControllerService,
    private router: Router
  ) {
  }

  ngOnInit() {
    const user = this.authService.getUser();
    if (user != null) {
      this.patientId = user.patientInfo.patientId;
      this.getMyRecord()
    } else {
      this.router.navigate(['/']);
    }
  }

  private getMyRecord() {
    this.recordsService.getPatientRecord({'patientId': this.patientId}).subscribe({
      next: (res) => {
        this.record = res;
        this.isLoading = false;
      }
    })
  }
}
