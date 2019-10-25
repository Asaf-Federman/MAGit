import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { IForkMessage } from '../models/IForkMessage';
import { Subject } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { PRBasicMessage } from '../models/PRBasicMessage';
import { PRStatus } from '../models/PRStatus';
import { PRDeclineStatus } from '../models/PRDeclineStatus';
import { IMessage } from '../models/IMessage';

@Injectable({
  providedIn: 'root'
})
export class MessagesService {
  messages:IMessage[]=[];
  messagesSub:Subject<IMessage[]>=new Subject();
  user: string;
  forkMessages: IForkMessage[] = [];
  forkMessageSub: Subject<IForkMessage[]> = new Subject();
  backEndUrl = environment.path + "/messages";
  prAlertMessages: PRBasicMessage[]=[];
  prAlertMessageSub: Subject<PRBasicMessage[]>= new Subject();
  amountOfMessages:number;
  amountOfMessagesSub:Subject<number>=new Subject();
  prStatusMessages: PRStatus[];
  prStatusMessageSub: Subject<PRStatus[]>=new Subject();;

  constructor(private http:HttpClient) { }

  getMessages(){
    return this.http.get<(IMessage[])[]>(this.backEndUrl).subscribe(res=>{
      this.messages=res;
      this.messagesSub.next(this.messages);
    }); 
  }

  // getMessages(type:eMessageManager){
  //     if(type===eMessageManager.ForkManager){
  //       this.handleForkMessages();
  //     }else if(type===eMessageManager.PRAlertManager){
  //       this.handlePRAlertMessages();
  //     }else if(type===eMessageManager.PRStatus){
  //       this.handlePRStatusMessages();
  //     }
  // }
  handlePRStatusMessages() {
    const params = new HttpParams()
    .set('type', <any>eMessageManager.PRStatus);
    return this.http.get<(PRStatus | PRDeclineStatus)[]>(this.backEndUrl,{params}).subscribe(res=>{
      this.prStatusMessages=res;
      this.prStatusMessageSub.next(this.prStatusMessages);
    }); 
   }

  handlePRAlertMessages() {
    const params = new HttpParams()
    .set('type', <any>eMessageManager.PRAlertManager);
    return this.http.get<PRBasicMessage[]>(this.backEndUrl,{params}).subscribe(res=>{
      this.prAlertMessages=res;
      this.prAlertMessageSub.next(this.prAlertMessages);
    });
  }

  handleForkMessages(){
    const params = new HttpParams()
    .set('type', <any>eMessageManager.ForkManager);
    return this.http.get<IForkMessage[]>(this.backEndUrl,{params}).subscribe(res=>{
      this.forkMessages=res;
      this.forkMessageSub.next(this.forkMessages);
    });
  }

  getAmountOfMessages(){
    const url=environment.path+"/messageAmount";
    return this.http.get<number>(url)
    .subscribe(res=>{
      this.amountOfMessages=res;
      this.amountOfMessagesSub.next(this.amountOfMessages);
    });
  }

  resetForkMessages(){
    this.forkMessages=[];
    this.forkMessageSub.next(this.forkMessages);
    this.prAlertMessages=[];
    this.prAlertMessageSub.next(this.prAlertMessages);
  }
}

export enum eMessageManager{
  ForkManager=0,
  PRAlertManager=1,
  PRStatus=2,
}
