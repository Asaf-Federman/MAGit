import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { PRStatus } from '../models/PRStatus';
import { Subject } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PrStatusService {
  prStatusArray: PRStatus[] = [];
  prStatusSub: Subject<PRStatus[]> = new Subject();
  backEndUrl = environment.path + "/prStatusInformation";
  
  constructor(private http:HttpClient) { }

  getStatusInformation(repositoryName:string){
    this.http.get<PRStatus[]>(this.backEndUrl,{params:{'repository-name':repositoryName}})
    .subscribe(res=>{
      this.prStatusArray=res;
      this.prStatusSub.next(this.prStatusArray);
    });
  }

  changeState(repositoryName: string, selectedStatus: PRStatus) {
    const params=new HttpParams()
    .set('repository-name',repositoryName)
    .set('state',selectedStatus.status);
    return this.http.post<PRStatus[]>(this.backEndUrl,selectedStatus,{params})
    .subscribe(res=>{
      this.prStatusArray=res;
      this.prStatusSub.next(this.prStatusArray);
    })
  }
}
