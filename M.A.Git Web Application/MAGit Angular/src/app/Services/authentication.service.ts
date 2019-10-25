import { Injectable, OnInit } from '@angular/core';
import { ISignUp } from '../models/ISignUp';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ILogin } from '../models/ILogin';
import { Subject } from 'rxjs';
import { Router } from '@angular/router';
import { IUserAuth } from '../models/IUserAuth';
import {environment} from "../../environments/environment";
import { MessagesService } from './messages.service';
import { MatDialog } from '@angular/material/dialog';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService{
  logInSub: Subject<string> = new Subject<string>();
  userAuth:IUserAuth = null;
  error:string=null;
  usersSet:String[]=[];
  loggedInSub:Subject<[]> = new Subject();
  errorSub:Subject<string>=new Subject();
  backEndUrl = environment.path+'/getUsers';
  userName: string;
  
  constructor(private http: HttpClient, private route: Router,private messageService:MessagesService, private dialog:MatDialog) { }

  public signUp(user: IUserAuth) {
    this.userAuth=user;
    this.userName=user.userName;
    return this.http.post<IUserAuth>(this.backEndUrl, user).subscribe(
      res => {
          this.updateUserName(res);
          if(this.route.url==="/registration"){
            this.route.navigate(['home']);
          }
          this.error="";
          this.errorSub.next(this.error);
        },
        error=>{
          this.userAuth=null;
          this.error=error.error.text;
          this.Logout();
          this.errorSub.next(this.error);
        });
  }

  public updateUserList()
  {
    return this.http.get<[]>(this.backEndUrl).subscribe(res=>{
      this.usersSet=res;
      this.loggedInSub.next(res);
    })
  }

  public updateUserName(user:IUserAuth){
    this.userAuth=user;
    this.userName=user.userName;
    this.logInSub.next(user.userName)
    localStorage.setItem("userName",user.userName);
    localStorage.setItem("password",user.password);
  }

  public login(user:IUserAuth) {
    this.userAuth=user;
    this.userName=user.userName;
    return this.http.get<IUserAuth>(environment.path+"/login",{params:{'userName':JSON.stringify(user)}}).subscribe(     
      res=>{
      this.updateUserName(res);
      if(this.route.url==="/registration"){
        this.route.navigate(['home']);
      }},
      error=>{
        this.userAuth=null;
        this.Logout();
      });
  }

  public Logout() {
    localStorage.removeItem('userName');
    localStorage.removeItem("password");
    this.userAuth=null;
    this.logInSub.next(null);
    this.route.navigate(['registration']);
  }
}
