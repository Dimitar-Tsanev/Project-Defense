import {Component, OnInit} from '@angular/core';
import {ErrorMessagesService} from '../../services/my-services/error-messages-service';

@Component({
  selector: 'app-error-messages',
  imports: [],
  templateUrl: './error-messages.component.html',
  styleUrl: './error-messages.component.css'
})
export class ErrorMessagesComponent implements OnInit {
  errorMessages: Array<String> = [];

  constructor(private errorService: ErrorMessagesService) {
  }

  ngOnInit(): void {
    this.errorService.error$.subscribe((err: any) => {
      let {error}: any = err;

      if (error.messages) {
        for (let message of error.messages) {
          console.log(this.errorMessages.push(message));
        }
      }
    });
  }
}
