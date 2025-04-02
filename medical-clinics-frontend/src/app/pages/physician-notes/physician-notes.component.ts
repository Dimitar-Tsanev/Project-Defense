import {Component, OnInit} from '@angular/core';
import {LoaderComponent} from "../../common/loader/loader.component";
import {Router} from "@angular/router";
import {NoteResponse} from '../../services/models/note-response';
import {AuthService} from '../../services/my-services/auth-service';
import {MedicalRecordControllerService} from '../../services/services/medical-record-controller.service';

@Component({
  selector: 'app-physician-notes',
  imports: [
    LoaderComponent
  ],
  templateUrl: './physician-notes.component.html',
  styleUrl: './physician-notes.component.css'
})
export class PhysicianNotesComponent implements OnInit {
  accountId: string = '';
  outpatientSheets: Array<NoteResponse> = [];
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
      this.accountId = user.accountId;
      this.getMyOutpatientSheets()
    } else {
      this.router.navigate(['/']);
    }
  }

  private getMyOutpatientSheets() {
    this.recordsService.getPhysicianNotes({'accountId': this.accountId}).subscribe({
      next: (res) => {
        this.outpatientSheets = res;
        this.isLoading = false;
      }
    })
  }
}
