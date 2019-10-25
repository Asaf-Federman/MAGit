import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { PRStatus, ePRStatus } from 'src/app/models/PRStatus';
import { SelectionModel } from '@angular/cdk/collections';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { PrStatusService } from 'src/app/Services/pr-status.service';
import { PRDeclineStatus } from 'src/app/models/PRDeclineStatus';

@Component({
  selector: 'app-pr-status-information',
  templateUrl: './pr-status-information.component.html',
  styleUrls: ['./pr-status-information.component.css']
})
export class PrStatusInformationComponent implements OnInit, OnDestroy {
  initialSelection = [];
  prStatusArray:PRStatus[];
  @ViewChild(MatPaginator) paginator: MatPaginator;
  declineMessage:string="";

  displayedColumns: string[] = ['select','id','fromUser','baseBranchName', 'targetBranchName', 'dateOfCreation','message','status'];
  allowMultiSelect = false;
  dataSource;
  selection = new SelectionModel<PRStatus>(this.allowMultiSelect, this.initialSelection);
  paramsSubscription: Subscription;
  prStatusSubscription: Subscription;
  repositoryName: string;
  constructor(private activeRoute:ActivatedRoute, private prStatusService:PrStatusService,private route:Router) { }

  ngOnInit() {

      this.prStatusSubscription=this.prStatusService.prStatusSub.subscribe(res=>{
        this.prStatusArray=res.reverse();
        this.dataSource = new MatTableDataSource<PRStatus>(this.prStatusArray);
        this.dataSource.paginator = this.paginator;
      });

      this.paramsSubscription=this.activeRoute.parent.params
      .subscribe(
        (params:Params) =>{
          this.repositoryName=params['name'];
          this.prStatusService.getStatusInformation(this.repositoryName);
        });
  }

  seeFiles(){
    this.route.navigate(['repository',this.repositoryName,'pull-request-status-files',this.selection.selected[0].ID])
  }

  isOpenForChanges(){
    const isEmpty:boolean=this.selection.isEmpty();
    var isClose:boolean=true;
    if(!isEmpty){
      isClose=this.selection.selected[0].status!==ePRStatus.Open
    }

    return isClose || isEmpty;
  }

  isDeclineInvalid(){
    return this.isOpenForChanges() || this.declineMessage.length===0
  }

  changeState(status:string){
    const selectedStatus:PRStatus=this.selection.selected[0];
    const id=selectedStatus.ID;
    const baseBranchName=selectedStatus.baseBranchName;
    const targetBranchName=selectedStatus.targetBranchName;
    const message=selectedStatus.message;
    const user=selectedStatus.fromUserName;
    const date=selectedStatus.dateOfRequestCreation;
    const repositoryName=selectedStatus.repositoryName;

    if(status==='accept'){
      const state=ePRStatus.Accepted;
      const newStatus:PRStatus={ID:id,baseBranchName:baseBranchName,targetBranchName:targetBranchName,
        message:message,fromUserName:user,dateOfRequestCreation:date,status:state,repositoryName:repositoryName};
      this.prStatusService.changeState(this.repositoryName,newStatus);
    }
    else{
      const state=ePRStatus.Declined;
      const declineMessage=this.declineMessage;
      const declineStatus:PRDeclineStatus={ID:id,baseBranchName:baseBranchName,targetBranchName:targetBranchName,
      message:message,fromUserName:user,dateOfRequestCreation:date,status:state,declineMessage:declineMessage,repositoryName:repositoryName};
      this.prStatusService.changeState(this.repositoryName,declineStatus);
    }

    this.selection.clear();
    this.declineMessage="";
  }

  ngOnDestroy(): void {
    this.paramsSubscription.unsubscribe();
    this.prStatusSubscription.unsubscribe();
  }
}
