/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { WorkDayDto } from '../models/work-day-dto';
export interface CreateEditClinicRequest {
  address: string;
  city: string;
  description: string;
  identificationNumber: string;
  phoneNumber: string;
  pictureUrl: string;
  workingDays: Array<WorkDayDto>;
}
