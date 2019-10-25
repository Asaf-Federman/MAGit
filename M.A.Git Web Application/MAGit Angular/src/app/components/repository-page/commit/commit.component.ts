import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { CommitInformation } from 'src/app/models/CommitInformation';
import { SelectionModel } from '@angular/cdk/collections';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { CommitInformationService } from 'src/app/Services/commit-information.service';
import { StatusInformation } from 'src/app/models/StatusInformation';
import { CommitService } from 'src/app/Services/commit.service';

@Component({
  selector: 'app-commit',
  templateUrl: './commit.component.html',
  styleUrls: ['./commit.component.css']
})
export class CommitComponent implements OnInit {
  initialSelection = [];
  statusInformationCollection:StatusInformation[]=[];
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = ['select','id','path','state'];
  allowMultiSelect = false;
  dataSource;
  selection = new SelectionModel<StatusInformation>(this.allowMultiSelect, this.initialSelection);
  paramsSubscription: Subscription;
  statusInfoSub: Subscription;
  repositoryName: any;
  commitMessage:string="";

  constructor(private activeRoute:ActivatedRoute, private commitService:CommitService, private route:Router) { }

  ngOnInit() {
      this.statusInfoSub=this.commitService.statusInformationCollectionSub.subscribe(res=>{
        this.statusInformationCollection=res;
        this.dataSource = new MatTableDataSource<StatusInformation>(this.statusInformationCollection);
        this.dataSource.paginator = this.paginator;
      })

      this.paramsSubscription=this.activeRoute.parent.params
      .subscribe(
        (params:Params) =>{
          this.repositoryName=params['name'];
          this.commitService.getStatusInformation(this.repositoryName);
        })
  }

  Commit(){
    this.commitService.commit(this.repositoryName,this.commitMessage);
    this.commitMessage=""
  }

  isDisable(){
    return this.commitMessage.length===0 || this.statusInformationCollection.length===0;
  }

  ngOnDestroy(): void {
    this.paramsSubscription.unsubscribe();
    this.statusInfoSub.unsubscribe();
  }

}
