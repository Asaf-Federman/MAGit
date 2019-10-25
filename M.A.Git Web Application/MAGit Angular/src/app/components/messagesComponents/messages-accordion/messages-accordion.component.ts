import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { MessagesService } from 'src/app/Services/messages.service';
import { IMessage } from 'src/app/models/IMessage';

@Component({
  selector: 'app-messages-accordion',
  templateUrl: './messages-accordion.component.html',
  styleUrls: ['./messages-accordion.component.css']
})
export class MessagesAccordionComponent implements OnInit {

  messages:IMessage[];
  messageSubs:Subscription=new Subscription;

  constructor(private messageService:MessagesService) { }

  ngOnInit() {
    this.messageSubs=this.messageService.messagesSub.subscribe(res=>{
      this.messages=res;
    });
    
    this.messageService.getMessages();
  }

  isFork(message:IMessage){
    return 'name' in message;
  }

  isPRMessage(message:IMessage){
    return 'fromUserName' in message && !this.isPRStatus(message);
  }

  isPRStatus(message:IMessage){
    return 'status' in message;
  }

  isDeletedMessage(message){
    return 'deletedBranchName' in message;
  }

  ngOnDestroy(): void {
    this.messageSubs.unsubscribe();
  }
}
