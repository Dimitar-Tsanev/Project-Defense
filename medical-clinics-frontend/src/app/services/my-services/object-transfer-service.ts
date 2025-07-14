import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ObjectTransferService {
  private object: any = {};

  setObject(object: any) {
    this.object = object;
  }

  getObject() {
    return this.object;
  }

  hasObject() {
    return Object.keys(this.object).length !== 0;
  }

  clearObject() {
    this.object = {};
  }
}
