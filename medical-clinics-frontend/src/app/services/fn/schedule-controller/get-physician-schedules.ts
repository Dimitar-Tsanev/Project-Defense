/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { PhysicianDaySchedulePrivate } from '../../models/physician-day-schedule-private';

export interface GetPhysicianSchedules$Params {
  accountId: string;
}

export function getPhysicianSchedules(http: HttpClient, rootUrl: string, params: GetPhysicianSchedules$Params, context?: HttpContext): Observable<StrictHttpResponse<Array<PhysicianDaySchedulePrivate>>> {
  const rb = new RequestBuilder(rootUrl, getPhysicianSchedules.PATH, 'get');
  if (params) {
    rb.path('accountId', params.accountId, {});
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<Array<PhysicianDaySchedulePrivate>>;
    })
  );
}

getPhysicianSchedules.PATH = '/schedules/physician/{accountId}';
