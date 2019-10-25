import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { Subscription } from 'rxjs';
import { CommitInformationService } from 'src/app/Services/commit-information.service';
import { AlertComponent } from '../../alert/alert.component';
import { MatDialog } from '@angular/material/dialog';
import { RepositoryInformationService } from 'src/app/Services/repository-information.service';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.css']
})
export class SidenavComponent implements OnInit, OnDestroy {

  paramsSubscription: Subscription;
  repositoryName: string;
  branchName:string;
  branchNameSub:Subscription
  clickBranchPage:boolean;
  clickCommitsPage:boolean;
  clickWorkingCopyPage:boolean;
  clickCommitPage:boolean;
  clickPullRequestPage:boolean;
  isRemote:boolean
  isRemoteSub:Subscription;
  clickPRStatusPage: boolean;

  constructor(private activeRoute:ActivatedRoute, private commitInfoService:CommitInformationService,
    private repositoryInformationService:RepositoryInformationService) { }

  ngOnInit() {
    this.resetFlags();
    this.clickBranchPage=true;
    this.branchNameSub=this.commitInfoService.headBrachNameSub.subscribe(res=>{
      this.branchName=res;
    });

    this.isRemoteSub=this.repositoryInformationService.isRemoteSub.subscribe(res=>{
      this.isRemote=res;
    })

    this.paramsSubscription=this.activeRoute.params
    .subscribe(
      (params:Params) =>{
        this.repositoryName = params['name'];
        this.branchName=this.commitInfoService.headBranchName;
      });
  }

  BranchFlag(){
    this.resetFlags();
    this.clickBranchPage=true;
  }

  CommitsFlag(){
    this.resetFlags()
    this.clickCommitsPage=true;
  }

  CommitFlag(){
    this.resetFlags();
    this.clickCommitPage=true;
  }

  WorkingCopyFlag(){
    this.resetFlags();
    this.clickWorkingCopyPage=true;
  }

  PullRequestFlag(){
    this.resetFlags();
    this.clickPullRequestPage=true;
  }

  PRStatusFlag(){
    this.resetFlags();
    this.clickPRStatusPage=true;
  }

  resetFlags(){
    this.clickBranchPage=false;
    this.clickCommitPage=false;
    this.clickCommitsPage=false;
    this.clickWorkingCopyPage=false;
    this.clickPullRequestPage=false;
    this.clickPRStatusPage=false;
  }

  ngOnDestroy(): void {
    this.paramsSubscription.unsubscribe();
    this.branchNameSub.unsubscribe();
    this.isRemoteSub.unsubscribe();
  }

}
