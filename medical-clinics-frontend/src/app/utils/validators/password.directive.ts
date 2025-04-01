import {Directive} from '@angular/core';
import {AbstractControl, NG_VALIDATORS, ValidationErrors, Validator,} from '@angular/forms';
import {passwordValidator} from './password.validator';


@Directive({
  selector: '[appPassword]',
  standalone: true,
  providers: [
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: PasswordDirective,
    },
  ],
})
export class PasswordDirective implements Validator {

  constructor() {}

  validate(control: AbstractControl): ValidationErrors | null {
    const validatorFn = passwordValidator();
    return validatorFn(control);
  }
}
