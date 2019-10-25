import { IMessage } from './IMessage';

export class IForkMessage implements IMessage{
    repositoryName:string;
    name:string;
}