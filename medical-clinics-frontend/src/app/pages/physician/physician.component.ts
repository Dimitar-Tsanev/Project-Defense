import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {PhysicianControllerService} from '../../services/services/physician-controller.service';
import {PhysicianInfo} from '../../services/models/physician-info';

@Component({
  selector: 'app-physician',
  imports: [],
  templateUrl: './physician.component.html',
  styleUrl: './physician.component.css'
})
export class PhysicianComponent implements OnInit {
  physician: PhysicianInfo | undefined = undefined
  isLoading: boolean = true;

  constructor(private route: ActivatedRoute,
              private physicianService: PhysicianControllerService ) {
  }
  ngOnInit(): void {
    const id = this.route.snapshot.params['physicianId'];
    this.physicianService.getPhysicianInfo({ 'physicianId':id as string}).subscribe({
      next: (res) => {
        this.physician = res;
        this.isLoading = false;
      }
    });
  }
}
