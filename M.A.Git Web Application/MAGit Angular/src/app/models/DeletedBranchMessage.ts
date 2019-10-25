import { IMessage } from './IMessage';

export class DeletedBranchMessage implements IMessage{
    repositoryName:string;
    deletedBranchName:string;
    deletedDate:string;
    deletedByUser:string;
}