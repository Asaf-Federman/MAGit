import { Injectable } from '@angular/core';
import { StatusInformation } from '../models/StatusInformation';
import { environment } from 'src/environments/environment';
import { Subject } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PrFilesService {

  statusInformationCollection:StatusInformation[]=[];
  statusInformationCollectionSub:Subject<StatusInformation[]>=new Subject();
  backEndUrl=environment.path+"/pr-files";

  constructor(private http:HttpClient) { }

  getStatusInformation(repositoryName:string,id:string){
    const params = new HttpParams()
    .set('repository-name',repositoryName)
    .set('id',id);
    this.http.get<StatusInformation[]>(this.backEndUrl,{params}).subscribe(res=>{
      this.statusInformationCollection=res;
      for(var i=0;i<this.statusInformationCollection.length;++i){
        this.statusInformationCollection[i].id=i+1;
      }
      this.statusInformationCollectionSub.next(this.statusInformationCollection);
    })
  }
}
