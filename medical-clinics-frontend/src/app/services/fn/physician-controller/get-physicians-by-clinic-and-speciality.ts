/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { PhysicianInfo } from '../../models/physician-info';

export interface GetPhysiciansByClinicAndSpeciality$Params {
  clinicId: string;
  specialityId: string;
}

export function getPhysiciansByClinicAndSpeciality(http: HttpClient, rootUrl: string, params: GetPhysiciansByClinicAndSpeciality$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<PhysicianInfo>>> {
  const rb = new RequestBuilder(rootUrl, getPhysiciansByClinicAndSpeciality.PATH, 'get');
  if (params) {
    rb.path('clinicId', params.clinicId, {});
    rb.path('specialityId', params.specialityId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<Array<PhysicianInfo>>;
    })
  );
}

getPhysiciansByClinicAndSpeciality.PATH = '/physicians/{clinicId}/{specialityId}';
