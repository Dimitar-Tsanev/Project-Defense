/* tslint:disable */
/* eslint-disable */
/* Code generated by ng-openapi-gen DO NOT EDIT. */

import { PatientInfo } from '../models/patient-info';
export interface DayAppointmentPrivate {
  patientInfo?: PatientInfo;
  startTime?: string;
  status?: 'RESERVED' | 'FREE' | 'PASSED' | 'INACTIVE';
  timeslotId?: string;
}
