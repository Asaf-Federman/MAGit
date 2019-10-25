import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthenticationService} from '../../Services/authentication.service';
import {Subscription} from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit, OnDestroy {
  userName: string = null;
  userSub: Subscription;
  constructor(private auth: AuthenticationService,private route:Router) { }

  ngOnInit() {
    if(this.auth.userAuth!=null){
      this.userName=this.auth.userAuth.userName;
    }
    this.userSub = this.auth.logInSub.subscribe(res=>{
    this.userName=res;
    })
  }

  onLogout()
  {
    this.auth.Logout();
  }

  ngOnDestroy(): void {
    this.userSub.unsubscribe();
  }

}
