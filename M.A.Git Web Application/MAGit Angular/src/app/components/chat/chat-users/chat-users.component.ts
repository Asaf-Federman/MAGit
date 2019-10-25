import { Component, OnInit, OnDestroy } from '@angular/core';
import { ChatService } from 'src/app/Services/chat.service';
import { AuthenticationService } from 'src/app/Services/authentication.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-chat-users',
  templateUrl: './chat-users.component.html',
  styleUrls: ['./chat-users.component.css']
})
export class ChatUsersComponent implements OnInit,OnDestroy {
  userName: string;
  userCollection:string[];
  userSub:Subscription;
  interval;

  constructor(private chatService:ChatService,private authService:AuthenticationService) { }

  ngOnInit() {
    this.userName=this.authService.userAuth.userName;
    this.chatService.addUser(this.userName);
    this.userSub=this.chatService.usersCollectionSub.subscribe(res=>{
      this.userCollection=res;
    })

    this.setInter();
  }

  setInter() {
    this.interval = setInterval(() => {
      this.chatService.getUsers();
    }, 2000);
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
    clearInterval(this.interval);
    this.chatService.deleteUser(this.userName);
  }

}
