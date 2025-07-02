import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {NavComponent} from './common/nav/nav.component';
import {ErrorMessagesComponent} from './common/error-messages/error-messages.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavComponent, ErrorMessagesComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'medical-clinics-frontend';
}
