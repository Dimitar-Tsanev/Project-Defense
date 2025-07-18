import { ValidatorFn } from '@angular/forms';

export function urlValidator(): ValidatorFn {

  return (control)=> {
    const isInvalid = isValidUrl(control.value)
    return isInvalid ? null : { urlValidator: true };
  };
}

function isValidUrl(control:string) {
  if (control === ''){
    return true;
  }
  try {
    new URL(control);
    return true;
  } catch (err) {
    return false;
  }
}
