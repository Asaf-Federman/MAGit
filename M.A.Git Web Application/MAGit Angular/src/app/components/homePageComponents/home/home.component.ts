import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, Params, ActivatedRoute } from "@angular/router";
import { AuthenticationService } from "../../../Services/authentication.service";
import { IUserAuth } from 'src/app/models/IUserAuth';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit , OnDestroy{

  clickedUser:string;
  paramsSubscription:Subscription= new Subscription();

  constructor(private authService: AuthenticationService, private route:ActivatedRoute, private router:Router) { }

  ngOnInit() {
  }

  ngOnDestroy(): void {
    this.paramsSubscription.unsubscribe();
  }
}
