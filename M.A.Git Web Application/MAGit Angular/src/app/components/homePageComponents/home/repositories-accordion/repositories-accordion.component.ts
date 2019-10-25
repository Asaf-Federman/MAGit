import { Component, OnInit, ViewChild, Inject, OnDestroy } from '@angular/core';
import { IRepositoryDetails } from 'src/app/models/IRepositoryDetails';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/Services/authentication.service';
import { ImageUploadService } from 'src/app/Services/upload.service';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { AlertComponent } from '../../../alert/alert.component';
import { Subscription } from 'rxjs';


@Component({
  selector: 'app-repositories-accordion',
  templateUrl: './repositories-accordion.component.html',
  styleUrls: ['./repositories-accordion.component.css']
})
export class RepositoriesAccordionComponent implements OnInit,OnDestroy {

  repositoryInformation:IRepositoryDetails[]=[]

  @ViewChild("file") private file;
  public path:string=" ";
  private messageSub:Subscription;
  private repositoryInfoSub:Subscription;
  constructor(private route:Router,private uploadService:ImageUploadService, private dialog:MatDialog) { }

  ngOnInit() {
    this.repositoryInfoSub=this.uploadService.repositoryInformationSub.subscribe(res=>{
      this.repositoryInformation=res;
    });
    this.messageSub=this.uploadService.messageSub.subscribe(res=>{
      this.openDialog(res);
    })
    this.uploadService.getInformation();
  }

  onClick(repositoryName:string){
    this.route.navigate(['repository',repositoryName,'branches'])
  }

  onFileBrowse(){
    this.file.nativeElement.value="";
    this.file.nativeElement.click();
  }

  onFilesChoose(files:FileList){
    var path:string=" ";
    for(var i=0;i<files.length;++i){
      if(i>0){
        path+=", ";
      }

      this.uploadService.uploadFile(files[i]);
      path+=files[i].name
    }
    this.path=path;
  }

  isEmpty(){
    return this.repositoryInformation.length==0;
  }

  openDialog(message:string): void {
   let dialogRef=this.dialog.open(AlertComponent, {});
   let instance=dialogRef.componentInstance;
   instance.message=message;
  }

  ngOnDestroy(): void {
    this.messageSub.unsubscribe();
    this.repositoryInfoSub.unsubscribe();
  }
}

