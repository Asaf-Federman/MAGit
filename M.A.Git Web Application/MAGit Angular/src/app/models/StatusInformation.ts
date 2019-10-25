import { Status } from './Status';

export class StatusInformation{
    id:number;
    currentStatus:Status;
	lastStatus:Status;
	state:string;
	path:string;
}