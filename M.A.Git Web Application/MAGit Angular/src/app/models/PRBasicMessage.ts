import { IMessage } from './IMessage';

export class PRBasicMessage implements IMessage{
    fromUserName:string;
    baseBranchName:string;
    targetBranchName:string;
    dateOfRequestCreation:string;
    message:string;
    repositoryName:string;
}