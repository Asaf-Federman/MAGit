import { Component, OnInit, ViewChild, ElementRef, OnDestroy, Output, EventEmitter } from '@angular/core';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { BranchInformation } from 'src/app/models/BranchInformation';
import { BranchInformationService } from 'src/app/Services/branch-information.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Params } from '@angular/router';
import { CommitInformationService } from 'src/app/Services/commit-information.service';
import {eBranchType} from '../../../models/BranchInformation'
import { RepositoryInformationComponent } from '../repository-information/repository-information.component';
import { RepositoryInformationService } from 'src/app/Services/repository-information.service';

@Component({
  selector: 'app-branch-table',
  templateUrl: './branch-table.component.html',
  styleUrls: ['./branch-table.component.css']
})
export class BranchTableComponent implements OnInit , OnDestroy{
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('input') private input:ElementRef;
  initialSelection = [];
  branchInformationCollection:BranchInformation[];
  displayedColumns: string[] = ['select','id','branchName', 'commitKey', 'commitMessage','isHead'];
  allowMultiSelect = false;
  dataSource;
  selection = new SelectionModel<BranchInformation>(this.allowMultiSelect, this.initialSelection);
  paramsSubscription: Subscription;
  branchInfoSubscription: Subscription;
  repositoryName: string;
  headBranchName:string;
  newBranchName:string="";
  isRemote:boolean;
  isRemoteSub:Subscription;
  interval;

  constructor(private branchInfoService:BranchInformationService, private activeRoute:ActivatedRoute, private commitInfoService:CommitInformationService,
    private repositoryInformation:RepositoryInformationService) { }

  ngOnInit() {  
    this.isRemote=this.repositoryInformation.isRemote;
      this.branchInfoSubscription=this.branchInfoService.branchInformationCollectionSub.subscribe(res=>{
        this.branchInformationCollection=res;
        this.headBranchName=this.branchInformationCollection.filter(branch=>branch.isHead===true)[0].branchName;
        this.dataSource = new MatTableDataSource<BranchInformation>(this.branchInformationCollection);
        this.dataSource.paginator = this.paginator;
        this.commitInfoService.setBranchName(this.headBranchName);
      });

      this.paramsSubscription=this.activeRoute.parent.params
      .subscribe(
        (params:Params) =>{
          this.repositoryName = params['name'];
          this.branchInfoService.getBranchInformation(this.repositoryName);
        });

        this.isRemoteSub=this.repositoryInformation.isRemoteSub.subscribe(res=>{
          this.isRemote=res;
        })
  }

  // setInter(){
  //   this.interval = setInterval(() => {
  //     this.branchInfoService.getBranchInformation(this.repositoryName);
  //   }, 2000);
  // }

  newHead(){
    const select:BranchInformation=this.selection.selected[0];
    this.selection.clear();
    this.branchInfoService.newHead(this.repositoryName,select.branchName);
  }

  deleteBranch(){
    const select:BranchInformation=this.selection.selected[0];
    this.selection.clear();
    this.branchInfoService.deleteBranch(this.repositoryName,select.branchName);
  }

  newBranch(){
    const select:BranchInformation=this.selection.selected[0];
    this.selection.clear();
    this.branchInfoService.newBranch(this.repositoryName,this.newBranchName,select.branchName);
    this.newBranchName="";
  }

  push(){
    this.selection.clear();
    this.branchInfoService.push(this.repositoryName);
  }

  pull(){
    this.selection.clear();
    this.branchInfoService.pull(this.repositoryName);
  }

  isNewHeadDisable(){
    var isHead:boolean=false;
    const isEmpty:boolean=this.selection.isEmpty();
    var isRemote:boolean=false;
    if(!isEmpty){
      isHead=this.selection.selected[0].isHead===true;
      isRemote=this.selection.selected[0].type===eBranchType.remoteBranch;
    }

    return isHead || isEmpty || isRemote;
  }

  isNewBranchDisable(){
    return this.newBranchName.length===0 || this.selection.isEmpty();
  }

  isDeleteBranchDisable(){
    var isHead:boolean=false;
    const isEmpty:boolean=this.selection.isEmpty();
    var isRemote:boolean=false;
    if(!isEmpty){
      isHead=this.selection.selected[0].isHead;
      isRemote=this.selection.selected[0].type===eBranchType.remoteBranch;
    }

    return isHead || isEmpty || isRemote;
  }

  ngOnDestroy(): void {
    this.paramsSubscription.unsubscribe();
    this.branchInfoSubscription.unsubscribe();
    this.isRemoteSub;
  }
}
