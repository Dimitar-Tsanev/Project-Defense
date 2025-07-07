import { Directive } from '@angular/core';
import {
  AbstractControl,
  NG_VALIDATORS,
  ValidationErrors,
  Validator,
} from '@angular/forms';
import {urlValidator} from './url.validator';


@Directive({
  selector: '[appUrl]',
  standalone: true,
  providers: [
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: UrlDirective,
    },
  ],
})
export class UrlDirective implements Validator {

  constructor() {}

  validate(control: AbstractControl): ValidationErrors | null {
    const validatorFn = urlValidator();
    return validatorFn(control);
  }
}
