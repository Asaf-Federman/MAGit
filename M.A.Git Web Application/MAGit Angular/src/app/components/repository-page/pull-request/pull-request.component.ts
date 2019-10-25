import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute, Params } from '@angular/router';
import { PullRequestService } from 'src/app/Services/pull-request.service';
import { Subscription } from 'rxjs';
import { BranchNames } from 'src/app/models/BranchNames';
import { AuthenticationService } from 'src/app/Services/authentication.service';
import { PRBasicMessage } from 'src/app/models/PRBasicMessage';

@Component({
  selector: 'app-pull-request',
  templateUrl: './pull-request.component.html',
  styleUrls: ['./pull-request.component.css']
})
export class PullRequestComponent implements OnInit {
  form: FormGroup
  parentParamSub: Subscription;
  repositoryName: string;
  branchesNamesSub: Subscription;
  branchesNames: BranchNames;

  constructor(private pullRequestService: PullRequestService, private activeRoute: ActivatedRoute,
    private authService: AuthenticationService) { }

  ngOnInit() {
    this.initializeForm();

    this.branchesNamesSub = this.pullRequestService.branchesNamesSub.subscribe(res => {
      this.branchesNames = res;
    });

    this.parentParamSub = this.activeRoute.parent.params.subscribe((params: Params) => {
      this.repositoryName = params['name'];
      this.pullRequestService.getBranchesNames(this.repositoryName);
    })
  }

  initializeForm() {
    this.form = new FormGroup({
      targetBranch: new FormControl(null, { validators: [Validators.required] }),
      baseBranch: new FormControl(null, { validators: [Validators.required] }),
      message: new FormControl(null, { validators: [Validators.required] }),
    });
  }

  submit() {
    const targetBranch: string = this.form.value.targetBranch;
    const baseBranch: string = this.form.value.baseBranch;
    const message: string = this.form.value.message;
    const userName: string = this.authService.userAuth.userName;
    const prMessage: PRBasicMessage = {
      targetBranchName: targetBranch, baseBranchName: baseBranch,
      message: message, fromUserName: userName, dateOfRequestCreation: null, repositoryName: this.repositoryName
    };
    
    if (baseBranch !== targetBranch) {
      this.pullRequestService.postMessage(this.repositoryName, prMessage);
    }

    this.form.reset();
  }
}
