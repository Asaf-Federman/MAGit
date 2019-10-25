import { IMessage } from './IMessage';

export class ChatMessage implements IMessage{
    userName:string;
	messageContent:string;
    date:string;
    state:eMessageState
}

export enum eMessageState{
    Message="Message",
    Alert="Alert"
}