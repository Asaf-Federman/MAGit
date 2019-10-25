import { PRBasicMessage } from './PRBasicMessage';

export class PRStatus extends PRBasicMessage{
    ID:number;
    status:ePRStatus
}

export enum ePRStatus{
    Open="Open",
    Accepted="Accepted",
    Declined="Declined"
}