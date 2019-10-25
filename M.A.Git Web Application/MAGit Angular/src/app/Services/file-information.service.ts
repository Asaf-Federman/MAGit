import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Subject } from 'rxjs';
import { FileInformation } from '../models/FileInformation';

@Injectable({
  providedIn: 'root'
})
export class FileInformationService {
  fileInformation:FileInformation;
  fileInformationSub:Subject<FileInformation>=new Subject();
  constructor(private http:HttpClient) { }
  backEndUrl=environment.path+"/fileInformation";


  getFileInformation(repositoryName:string,encryptionKey:string){
    const params = new HttpParams()
    .set('repository-name', repositoryName)
    .set('commit-key', encryptionKey);
    this.http.get<FileInformation>(this.backEndUrl,{params}).subscribe(res=>{
      this.fileInformation=res;
      this.fileInformationSub.next(this.fileInformation);
    })
  }
}
