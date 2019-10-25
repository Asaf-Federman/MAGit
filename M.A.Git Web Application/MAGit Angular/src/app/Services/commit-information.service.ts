import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Subject } from 'rxjs';
import { CommitInformation } from '../models/CommitInformation';

@Injectable({
  providedIn: 'root'
})
export class CommitInformationService {
  commitInformationCollection:CommitInformation[]=[];
  headBranchName:string="";
  headBrachNameSub:Subject<string>=new Subject();
  branchInformationCollectionSub:Subject<CommitInformation[]>=new Subject();
  backEndUrl=environment.path+"/commitInformation";

  constructor(private http:HttpClient) { }


  getCommitInformation(repositoryName:string){
    this.http.get<Array<{encryptionKey:string,message:string,creationDate:string,author:string,relatedBranches:string[]}>>(this.backEndUrl,{params:{'repository-name':repositoryName}}).subscribe(res=>{
      this.commitInformationCollection=[];
      for(var i=0;i<res.length;++i){
        const commitInfo:CommitInformation= new CommitInformation(i+1,res[i].encryptionKey,res[i].message,res[i].creationDate,res[i].author,res[i].relatedBranches);
        this.commitInformationCollection.push(commitInfo);
      }

      this.branchInformationCollectionSub.next(this.commitInformationCollection);
    })
  }

  setBranchName(headBranchName:string){
    this.headBranchName=headBranchName;
    this.headBrachNameSub.next(this.headBranchName);
  }
}
