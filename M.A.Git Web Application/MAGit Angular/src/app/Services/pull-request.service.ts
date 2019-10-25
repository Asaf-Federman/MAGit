import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Subject } from 'rxjs';
import { BranchNames } from '../models/BranchNames';
import { PRBasicMessage } from '../models/PRBasicMessage';

@Injectable({
  providedIn: 'root'
})
export class PullRequestService {

  backEndUrl = environment.path + "/branchesNames";
  branchesNames:BranchNames;
  branchesNamesSub:Subject<BranchNames>= new Subject();
  constructor(private http:HttpClient) { }

  getBranchesNames(repositoryName:string){
    return this.http.get<BranchNames>(this.backEndUrl,{params:{'repository-name':repositoryName}})
    .subscribe(res=>{
        this.branchesNames=res;
        this.branchesNamesSub.next(res);
      });
  }

  postMessage(repositoryName:string,prMessage:PRBasicMessage){
    return this.http.post(this.backEndUrl,prMessage,{params:{'repository-name':repositoryName}})
    .subscribe();
  }
}
