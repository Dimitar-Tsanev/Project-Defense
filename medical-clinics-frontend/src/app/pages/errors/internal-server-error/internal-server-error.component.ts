import { Component } from '@angular/core';
import {NgOptimizedImage} from "@angular/common";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-internal-server-error',
    imports: [
        NgOptimizedImage,
        RouterLink
    ],
  templateUrl: './internal-server-error.component.html',
  styleUrl: './internal-server-error.component.css'
})
export class InternalServerErrorComponent {

}
