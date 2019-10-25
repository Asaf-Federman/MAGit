import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { IRepositoryDetails } from '../models/IRepositoryDetails';
import { Subject } from 'rxjs';
import { environment } from "../../environments/environment";


@Injectable({
  providedIn: 'root'
})
export class ImageUploadService {
  message: string;
  messageSub: Subject<string> = new Subject();
  backEndUrl = environment.path + '/XMLFile';
  repositoryInformation: IRepositoryDetails[] = [];
  repositoryInformationSub: Subject<IRepositoryDetails[]> = new Subject();

  constructor(private http: HttpClient) { }

  getInformation() {
    return this.http.get<IRepositoryDetails[]>(this.backEndUrl).subscribe(res => {
      this.update(res);
    })
  }

  uploadFile(fileToUpload: File) {
    let fileReader = new FileReader();
    fileReader.onload = (e) => {
      var data: string = fileReader.result.toString();
      return this.http.post<any>(this.backEndUrl, data).subscribe(res => {
        if (res instanceof Object) {
          this.update(res);
        }
        else {
          this.message = res;
          this.messageSub.next(this.message);
        }
      });
    }
    fileReader.readAsText(fileToUpload);
  }

  update(repositoryDetails) {
    this.repositoryInformation = repositoryDetails;
    this.repositoryInformationSub.next(this.repositoryInformation);
  }

}

