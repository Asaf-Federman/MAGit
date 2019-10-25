import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from './Services/authentication.service';
import { IUserAuth } from './models/IUserAuth';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'MAGit-Ex03';
  userName;
  password;

  constructor(private auth:AuthenticationService)
  {

  }

  ngOnInit() {
    this.userName=localStorage.getItem('userName');
    this.password=localStorage.getItem('password');
    if(this.userName!='null' && this.userName!=null){
      const user:IUserAuth={userName:this.userName,password:this.password};
      this.auth.login(user);
    }
  }
}
