import { Injectable } from '@angular/core';
import { StatusInformation } from '../models/StatusInformation';
import { environment } from 'src/environments/environment';
import { Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CommitService {

  statusInformationCollection:StatusInformation[]=[];
  statusInformationCollectionSub:Subject<StatusInformation[]>=new Subject();
  backEndUrl=environment.path+"/commit";

  constructor(private http:HttpClient) { }


  getStatusInformation(repositoryName:string){
    this.http.get<StatusInformation[]>(this.backEndUrl,{params:{'repository-name':repositoryName}}).subscribe(res=>{
      this.statusInformationCollection=res;
      for(var i=0;i<this.statusInformationCollection.length;++i){
        this.statusInformationCollection[i].id=i+1;
      }
      this.statusInformationCollectionSub.next(this.statusInformationCollection);
    })
  }

  commit(repositoryName:string,commitMessage:string){
    this.http.post<StatusInformation[]>(this.backEndUrl,commitMessage,{params:{'repository-name':repositoryName}}).subscribe(res=>{
      this.statusInformationCollection=[];
      this.statusInformationCollectionSub.next(this.statusInformationCollection);
    })
  }
}
