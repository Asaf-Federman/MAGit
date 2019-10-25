import { Component, OnInit, Inject } from '@angular/core';
import { ImageUploadService } from 'src/app/Services/upload.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-alert',
  templateUrl: './alert.component.html',
  styleUrls: ['./alert.component.css']
})
export class AlertComponent implements OnInit {
  message:string="";
  
  constructor(public dialogRef: MatDialogRef<AlertComponent>) { }

  ngOnInit() {
  }

  close(){
    this.dialogRef.close("Finished");
  }

}
