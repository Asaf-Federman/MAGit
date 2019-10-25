import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { CommitInformation } from 'src/app/models/CommitInformation';
import { SelectionModel } from '@angular/cdk/collections';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { CommitInformationService } from 'src/app/Services/commit-information.service';

@Component({
  selector: 'app-commit-table',
  templateUrl: './commit-table.component.html',
  styleUrls: ['./commit-table.component.css']
})
export class CommitTableComponent implements OnInit, OnDestroy{

  initialSelection = [];
  commitInformationCollection:CommitInformation[];
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('input') private input:ElementRef;

  displayedColumns: string[] = ['select','id','encryptionKey','message', 'creationDate', 'author','relatedBranches'];
  allowMultiSelect = false;
  dataSource;
  selection = new SelectionModel<CommitInformation>(this.allowMultiSelect, this.initialSelection);
  paramsSubscription: Subscription;
  commitInfoSubscription: Subscription;
  headBranchNameSub:Subscription;
  branchName: string;
  repositoryName: any;

  constructor(private activeRoute:ActivatedRoute, private commitInfoService:CommitInformationService, private route:Router) { }

  ngOnInit() {
    this.branchName=this.commitInfoService.headBranchName;
    this.headBranchNameSub=this.commitInfoService.headBrachNameSub.subscribe(res=>{
      this.branchName=res;
    });

      this.commitInfoSubscription=this.commitInfoService.branchInformationCollectionSub.subscribe(res=>{
        this.commitInformationCollection=res.reverse();
        this.dataSource = new MatTableDataSource<CommitInformation>(this.commitInformationCollection);
        this.dataSource.paginator = this.paginator;
      })

      this.paramsSubscription=this.activeRoute.parent.params
      .subscribe(
        (params:Params) =>{
          this.repositoryName=params['name'];
          this.commitInfoService.getCommitInformation(this.repositoryName);
        })  
  }

  openFiles(selectedCommit:CommitInformation[]){
    var selectedCommitKey=selectedCommit[0].encryptionKey;
    this.route.navigate(['repository',this.repositoryName,'branch',this.branchName,'commit',selectedCommitKey,'files'])
  }

  ngOnDestroy(): void {
    this.paramsSubscription.unsubscribe();
    this.commitInfoSubscription.unsubscribe();
    this.headBranchNameSub.unsubscribe();
  }

}
