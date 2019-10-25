import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { StatusInformation } from 'src/app/models/StatusInformation';
import { SelectionModel } from '@angular/cdk/collections';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Params } from '@angular/router';
import { MatTableDataSource } from '@angular/material/table';
import { PrFilesService } from 'src/app/Services/pr-files.service';

@Component({
  selector: 'app-pull-request-files',
  templateUrl: './pull-request-files.component.html',
  styleUrls: ['./pull-request-files.component.css']
})
export class PullRequestFilesComponent implements OnInit {
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
  parentParamsSubscription: Subscription;
  id: string;

  constructor(private activeRoute:ActivatedRoute, private prStatusService:PrFilesService) { }

  ngOnInit() {
      this.statusInfoSub=this.prStatusService.statusInformationCollectionSub.subscribe(res=>{
        this.statusInformationCollection=res;
        this.dataSource = new MatTableDataSource<StatusInformation>(this.statusInformationCollection);
        this.dataSource.paginator = this.paginator;
      })

      this.paramsSubscription=this.activeRoute.params.subscribe((params:Params)=>{
        this.id=params['statusId'];
      })

      this.parentParamsSubscription=this.activeRoute.parent.params
      .subscribe(
        (params:Params) =>{
          this.repositoryName=params['name'];
          this.prStatusService.getStatusInformation(this.repositoryName,this.id);
        })
  }

  ngOnDestroy(): void {
    this.paramsSubscription.unsubscribe();
    this.statusInfoSub.unsubscribe();
  }
}
