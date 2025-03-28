/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { NewNoteRequest } from '../../models/new-note-request';

export interface AddNewNote$Params {
  accountId: string;
  patientId: string;
      body: NewNoteRequest
}

export function addNewNote(http: HttpClient, rootUrl: string, params: AddNewNote$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
  const rb = new RequestBuilder(rootUrl, addNewNote.PATH, 'post');
  if (params) {
    rb.path('accountId', params.accountId, {});
    rb.query('patientId', params.patientId, {});
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'text', accept: '*/*', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return (r as HttpResponse<any>).clone({ body: undefined }) as StrictHttpResponse<void>;
    })
  );
}

addNewNote.PATH = '/medical-records/note/new/physician/{accountId}';
