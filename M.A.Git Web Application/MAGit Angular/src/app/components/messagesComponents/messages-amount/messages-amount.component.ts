import { Component, OnInit, OnDestroy } from '@angular/core';
import { MessagesService } from 'src/app/Services/messages.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-messages-amount',
  templateUrl: './messages-amount.component.html',
  styleUrls: ['./messages-amount.component.css']
})
export class MessagesAmountComponent implements OnInit,OnDestroy {
  messagesAmount:Number;
  messagesAmountSub:Subscription;
  interval;

  constructor(private messageService:MessagesService) { }

  ngOnInit() {
    this.messagesAmountSub=this.messageService.amountOfMessagesSub
    .subscribe(res=>{
      this.messagesAmount=res;
    })

    this.messageService.getAmountOfMessages(); 
    this.setInter();
  }

  setInter(){
    this.interval = setInterval(() => {
      this.messageService.getAmountOfMessages(); 
    }, 2000);
  }

  ngOnDestroy(): void {
    this.messagesAmountSub.unsubscribe();
    clearInterval(this.interval);
  }

}
