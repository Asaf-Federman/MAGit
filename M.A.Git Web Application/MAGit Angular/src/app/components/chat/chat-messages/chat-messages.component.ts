import { Component, OnInit, Input, ElementRef, AfterViewChecked, ViewChild, OnDestroy } from '@angular/core';
import { ChatMessage, eMessageState } from 'src/app/models/ChatMessage';
import { AuthenticationService } from 'src/app/Services/authentication.service';
import { formatDate } from '@angular/common';
import { ChatService } from 'src/app/Services/chat.service';
import { Subscription } from 'rxjs';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-chat-messages',
  templateUrl: './chat-messages.component.html',
  styleUrls: ['./chat-messages.component.css']
})
export class ChatMessagesComponent implements OnInit, AfterViewChecked,OnDestroy {
  @ViewChild('scrollMe') private myScrollContainer: ElementRef;
  userName: string;
  now = new Date();
  scrollToBottomCounter:boolean=true;
  chatMessages: ChatMessage[] = [];
  chatMessagesSub:Subscription;
  interval;
  lastScrollHeight: number;

  constructor(private authService: AuthenticationService, private chatService:ChatService) { }

  ngOnInit() {
    this.userName = this.authService.userAuth.userName;
    this.chatMessagesSub=this.chatService.messagesCollectionSub.subscribe(res=>{
      this.chatMessages=res;
    });

    this.setInter();
  }

  ngAfterViewChecked(): void {
    this.scrollBottom();
  }

  isUser(message: ChatMessage) {
    return message.userName === this.userName || message.state === eMessageState.Alert;
  }

  isAdmin(message: ChatMessage) {
    return message.state === eMessageState.Alert;
  }

  scrollBottom() {
    this.checkIfHeightChanged();
    if (this.scrollToBottomCounter) {
      this.scrollToBottomLogic();
      this.scrollToBottomCounter=false;
    }
  }

  checkIfHeightChanged() {
    const height:number=this.myScrollContainer.nativeElement.scrollHeight;
    if(height!==this.lastScrollHeight){
      this.scrollToBottomCounter=true;
    }
  }

  scrollToBottomLogic(){
    try {
      this.lastScrollHeight=this.myScrollContainer.nativeElement.scrollHeight;
      this.myScrollContainer.nativeElement.scrollTop = this.myScrollContainer.nativeElement.scrollHeight;
    } catch (err) {
    }
  }

  onSubmit(form:NgForm){
    const message:ChatMessage={messageContent:form.value.message,userName:this.userName,date:null,state:eMessageState.Message};
    this.chatService.addMessage(message);
    form.reset();
  }

  setInter() {
    this.interval = setInterval(() => {
      this.chatService.getMessages();
    }, 2000);
  }

  ngOnDestroy(): void {
    clearInterval(this.interval);
    this.chatMessagesSub.unsubscribe();
  }

}
