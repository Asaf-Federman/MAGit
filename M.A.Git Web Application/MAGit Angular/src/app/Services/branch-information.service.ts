import { Injectable } from '@angular/core';
import { BranchInformation } from '../models/BranchInformation';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Subject } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { AlertComponent } from '../components/alert/alert.component';


@Injectable({
  providedIn: 'root'
})
export class BranchInformationService {
  branchInformationCollection:BranchInformation[]=[];
  branchInformationCollectionSub:Subject<BranchInformation[]>=new Subject();
  backEndUrl=environment.path+"/branchInformation";

  constructor(private http:HttpClient,private dialog:MatDialog) { }

  getBranchInformation(repositoryName:string){
    this.http.get<BranchInformation[]>(this.backEndUrl,{params:{'repository-name':repositoryName}}).subscribe(res=>
      this.updateBranchCollection(res))
  }

  newHead(repositoryName: string, branchName: string) {
    this.http.post<BranchInformation[]>(this.backEndUrl,branchName,{params:{'repository-name':repositoryName}})
    .subscribe(res=>this.updateBranchCollection(res),
      error=>{
        this.alert(error.error.text);
      })
  }

  newBranch(repositoryName: string, newBranchName: string,branchName:string) {
    const params=new HttpParams()
    .set('repository-name',repositoryName)
    .set('branch-name',branchName);
    this.http.put<BranchInformation[]>(this.backEndUrl,newBranchName,{params})
    .subscribe(
      res=>this.updateBranchCollection(res),
      error=>this.alert(error.error.text)
    );
  }

  push(repositoryName:string){
    const url=environment.path+"/push";
    this.http.post<BranchInformation[]>(url,repositoryName).subscribe(
      res=>this.updateBranchCollection(res),
      error=>this.alert(error.error.text)
      );
  }

  pull(repositoryName:string){
    const url=environment.path+"/pull";
    this.http.post<BranchInformation[]>(url,repositoryName).subscribe(
      res=>this.updateBranchCollection(res),
      error=>this.alert(error.error.text)
      );
  }

  deleteBranch(repositoryName: string, deleteBranchName: string) {
    const params = new HttpParams()
    .set('repository-name', repositoryName)
    .set('delete-branch-name',deleteBranchName);
    this.http.delete<Array<{branchName:string,commitKey:string,commitMessage:string,isHead:boolean}>>(this.backEndUrl,{params})
    .subscribe(res=>this.updateBranchCollection(res));
  }

  updateBranchCollection(branchInformationCollection){
    this.branchInformationCollection=[];
    for(var i=0;i<branchInformationCollection.length;++i){
      const branchInfo:BranchInformation= {id:i+1,branchName:branchInformationCollection[i].branchName,commitKey:branchInformationCollection[i].commitKey,
        commitMessage:branchInformationCollection[i].commitMessage,isHead:branchInformationCollection[i].isHead,
        type:branchInformationCollection[i].type};
      this.branchInformationCollection.push(branchInfo);
    }
    this.branchInformationCollectionSub.next(this.branchInformationCollection);
  }


  alert(message){
    let dialogRef=this.dialog.open(AlertComponent, {});
    let instance=dialogRef.componentInstance;
    instance.message=message;
  }
}
