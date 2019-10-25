import { Injectable } from '@angular/core';
import { RepositoryInformation } from '../models/repositoryInformation';
import { Subject } from 'rxjs';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';
import { SourceListMap } from 'source-list-map';

@Injectable({
  providedIn: 'root'
})
export class RepositoryInformationService {

  repositoryInformation: RepositoryInformation;
  repositoryInformationSub: Subject<RepositoryInformation> = new Subject();
  isRemote:boolean=false;
  isRemoteSub:Subject<boolean>=new Subject();

  backEndUrl = environment.path + "/repositoryInformation";
  constructor(private http: HttpClient) { }

  public getRepositoryInformation(repositoryName: string) {
    return this.http.get<RepositoryInformation>
      (this.backEndUrl,
        { params: {'repository-name':repositoryName} }).subscribe(
          res => {
            this.repositoryInformation = res;
            this.repositoryInformationSub.next(this.repositoryInformation)
            this.isRemote=this.repositoryInformation.remoteRepositoryName!==undefined;
            this.isRemoteSub.next(this.isRemote);
          });
  }
}
