/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { PhysicianEditRequest } from '../../models/physician-edit-request';

export interface UpdatePhysician$Params {
  physicianId: string;
      body: PhysicianEditRequest
}

export function updatePhysician(http: HttpClient, rootUrl: string, params: UpdatePhysician$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
  const rb = new RequestBuilder(rootUrl, updatePhysician.PATH, 'put');
  if (params) {
    rb.path('physicianId', params.physicianId, {});
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

updatePhysician.PATH = '/physicians/physician/{physicianId}';
