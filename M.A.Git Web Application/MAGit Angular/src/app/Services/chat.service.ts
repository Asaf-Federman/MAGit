import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Subject } from 'rxjs';
import { ChatMessage } from '../models/ChatMessage';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  messagesCollection:ChatMessage[]=[];
  messagesCollectionSub:Subject<ChatMessage[]>=new Subject();
  usersCollection:string[]=[];
  usersCollectionSub:Subject<string[]>=new Subject();
  backEndChatMessageUrl=environment.path+"/chatMessages";
  backEndChatUsersUrl=environment.path+"/chatUsers";
  lastVersion=0;

  constructor(private http:HttpClient) { }

  getMessages(){
    this.http.get<ChatMessage[]>(this.backEndChatMessageUrl,{params:{'last-version':<any>this.lastVersion},observe: 'response'})
    .subscribe(res=>
      {
        this.lastVersion=+res.headers.get('version');
        this.populateMessagesCollection(res.body)
      });
  }

  populateMessagesCollection(messages: ChatMessage[]): void {
    this.messagesCollection=this.messagesCollection.concat(messages);
    this.messagesCollectionSub.next(this.messagesCollection);
  }

  addMessage(message:ChatMessage){
    this.http.post<ChatMessage[]>(this.backEndChatMessageUrl,message,{params:{'last-version':<any>this.lastVersion},observe: 'response'})
    .subscribe(res=>
      {
        this.lastVersion=+res.headers.get('version');
        this.populateMessagesCollection(res.body)
      });
  }

  getUsers(){
    this.http.get<string[]>(this.backEndChatUsersUrl)
    .subscribe(res=>this.populateUsersCollection(res))
  }

  populateUsersCollection(res: string[]): void {
    this.usersCollection=res;
    this.usersCollectionSub.next(this.usersCollection);
  }

  addUser(userName:string){
    this.http.post<any>(this.backEndChatUsersUrl,userName,{observe: 'response'})
    .subscribe(res=>{
      this.lastVersion=+res.headers.get('version');
      this.populateUsersCollection(res.body);
      this.getMessages();
    });
  }

  deleteUser(userName:string){
    this.http.delete<string[]>(this.backEndChatUsersUrl,{params:{'userName':userName}})
    .subscribe(res=>{
      this.messagesCollection=[];
      this.messagesCollectionSub.next(this.messagesCollection);
    });
  }

}
