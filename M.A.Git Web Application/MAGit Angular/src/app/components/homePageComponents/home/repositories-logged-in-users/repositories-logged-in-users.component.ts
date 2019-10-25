import { Component, OnInit, ViewChild, ElementRef, ChangeDetectionStrategy, OnDestroy, Output, EventEmitter } from '@angular/core';
import { AuthenticationService } from 'src/app/Services/authentication.service';
import { IUserAuth } from 'src/app/models/IUserAuth';
import { Subscription } from 'rxjs';
import { Router, ActivatedRoute, Params, UrlSegment } from '@angular/router';
import { createElementCssSelector } from '@angular/compiler';


@Component({
  selector: 'app-repositories-logged-in-users',
  templateUrl: './repositories-logged-in-users.component.html',
  styleUrls: ['./repositories-logged-in-users.component.css'],
  changeDetection: ChangeDetectionStrategy.Default
})
export class RepositoriesLoggedInUsersComponent implements OnInit, OnDestroy {
  LoggedInSet = [];
  LoggedInUserName: string;
  id;
  clickedUser: string;
  LoggedInSub: Subscription;
  clickedUserIndex: Number;
  activeRouteSub: Subscription;
  childSub: Subscription;

  constructor(private auth: AuthenticationService, private route: Router, private activeRoute: ActivatedRoute) { }

  ngOnInit() {
    this.LoggedInUserName = this.auth.userAuth.userName;
    this.setInter();
    this.userNamesSubscription();
    this.auth.updateUserList();

    this.activeRouteSub = this.activeRoute.url
      .subscribe(
        (url: UrlSegment[]) => {
          if (this.activeRoute.children[0]) {
            this.childSub = this.activeRoute.children[0].params.subscribe(res => {
              this.clickedUser = res["id"];
            });
          }

          this.chosenUser();
        });
  }

  userNamesSubscription() {
    this.LoggedInSub = this.auth.loggedInSub.subscribe(res => {
      this.LoggedInSet = res;
      this.chosenUser();
    })
  }

  setInter() {
    this.id = setInterval(() => {
      this.auth.updateUserList();
    }, 2000);
  }

  onClick(index: number) {
    this.clickedUser = this.LoggedInSet[index]
    if (this.LoggedInUserName === this.clickedUser) {
      this.route.navigate(['home'])
    }
    else {
      this.route.navigate(['home', 'view', this.clickedUser, 'repositories'])
    }
  }

  chosenUser() {
    const index=this.LoggedInSet.findIndex(user => this.clickedUser === user);
    this.clickedUserIndex=index;
  }

  ngOnDestroy() {
    clearInterval(this.id);
    this.LoggedInSub.unsubscribe();
    this.activeRouteSub.unsubscribe();
    this.childSub.unsubscribe();
  }
}
