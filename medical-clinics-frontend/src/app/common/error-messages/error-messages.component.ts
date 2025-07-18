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
      if (err == null) {
        return;
      }

      let {error}: any = err;
      this.parseMessages(error);
      this.setTimeout()
    });
  }

  parseMessages(error: any) {
    if (error.messages) {
      for (let message of error.messages) {
        this.errorMessages.push(message);
      }

    } else {
      let {messages} = JSON.parse(error)
      this.errorMessages = messages;
    }
  }

  setTimeout() {
    setTimeout(() => {
      this.errorMessages = []
    }, 1000 * 12);
  }
}
