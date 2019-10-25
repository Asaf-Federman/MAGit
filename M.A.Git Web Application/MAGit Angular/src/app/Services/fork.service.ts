import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { IRepositoryDetails } from '../models/IRepositoryDetails';
import { environment } from "../../environments/environment";
import { MatDialog } from '@angular/material/dialog';
import { AlertComponent } from '../components/alert/alert.component';


@Injectable({
  providedIn: 'root'
})
export class ForkServiceService {
  user: string;
  repositoryInformation: IRepositoryDetails[] = [];
  repositoryInformationSub: Subject<IRepositoryDetails[]> = new Subject();

  backEndUrl = environment.path + "/fork";
  constructor(private http: HttpClient, private route: Router, private dialog:MatDialog) { }

  public fetchRepositories(user: string) {
    this.user = user;
    return this.http.get<IRepositoryDetails[]>
      (this.backEndUrl,
        { params: { "userName": this.user } }).subscribe(
          res => {
            this.repositoryInformation = res;
            this.repositoryInformationSub.next(this.repositoryInformation)
          });
  }

  public fetchForks(RepositoryName:string,user: string) {
    this.user = user;
    return this.http.post<string>
      (this.backEndUrl,RepositoryName,{
        params:{"userName":this.user}
      }).subscribe(
        message=>this.alert(message),
        error=>this.alert(error.error.text));
  }

  alert(message:string){
    let dialogRef=this.dialog.open(AlertComponent, {});
    let instance=dialogRef.componentInstance;
    instance.message=message;
  }
}
