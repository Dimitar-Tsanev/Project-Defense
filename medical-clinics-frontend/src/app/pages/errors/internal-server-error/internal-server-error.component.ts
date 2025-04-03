import { Component } from '@angular/core';
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-internal-server-error',
    imports: [
        RouterLink
    ],
  templateUrl: './internal-server-error.component.html',
  styleUrl: './internal-server-error.component.css'
})
export class InternalServerErrorComponent {

}
