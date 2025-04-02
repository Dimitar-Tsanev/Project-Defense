import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ErrorMessagesService {
  private error$$ = new BehaviorSubject(null);
  public error$ = this.error$$.asObservable();

  solve(err: any) {
    this.error$$.next(err)
  }
}
