import { ValidatorFn } from '@angular/forms';

export function passwordValidator(): ValidatorFn {

  const regExp = new RegExp(`(?=\\S+$)(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[.!@#$%^&*()_+<>?])[A-Za-z\\d!@#$%^&*()_+<>?.]*`);

  return (control)=> {
    const isInvalid = control.value === '' || regExp.test(control.value);
    return isInvalid ? null : { passwordValidator: true };
  };
}
