import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { WorkingCopyInformation } from '../models/WorkingCopyInformation';
import { Subject } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class WorkingCopyInformationService {

  workingCopyInformation:WorkingCopyInformation;
  workingCopyInformationSub:Subject<WorkingCopyInformation>=new Subject();
  constructor(private http:HttpClient) { }
  backEndUrl=environment.path+"/workingCopyInformation";

  getWorkingCopyInformation(repositoryName:string){
    this.http.get<WorkingCopyInformation>(this.backEndUrl,{params:{'repository-name':repositoryName}}).subscribe(res=>{
      this.workingCopyInformation=res;
      this.workingCopyInformationSub.next(this.workingCopyInformation);
    })
  }

  saveChanges(node:WorkingCopyInformation,repositoryName:string,action:string){
    const params = new HttpParams()
    .set('repository-name', repositoryName)
    .set('action', action);
    this.http.post<WorkingCopyInformation>(this.backEndUrl,node,{params}).subscribe(res=>{
      this.workingCopyInformation=res;
      this.workingCopyInformationSub.next(this.workingCopyInformation);
    })
  }
}
