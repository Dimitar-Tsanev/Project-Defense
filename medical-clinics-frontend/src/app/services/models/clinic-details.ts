/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { SpecialityDto } from '../models/speciality-dto';
import { WorkDayDto } from '../models/work-day-dto';
export interface ClinicDetails {
  address?: string;
  city?: string;
  description?: string;
  id?: string;
  phoneNumber?: string;
  pictureUrl?: string;
  specialties?: Array<SpecialityDto>;
  workingDays?: Array<WorkDayDto>;
}
