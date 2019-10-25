import {NestedTreeControl} from '@angular/cdk/tree';
import {Component, OnInit, OnDestroy} from '@angular/core';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import { FileInformationService } from 'src/app/Services/file-information.service';
import { Subscription } from 'rxjs';
import { FileInformation } from 'src/app/models/FileInformation';
import { ActivatedRoute, Params } from '@angular/router';


@Component({
  selector: 'app-file-table',
  templateUrl: './file-table.component.html',
  styleUrls: ['./file-table.component.css'],
})
export class FileTableComponent implements OnInit ,OnDestroy {
  treeControl = new NestedTreeControl<FileInformation>(node => node.children);
  dataSource = new MatTreeNestedDataSource<FileInformation>();
  fileInformationSub:Subscription;
  fileInformation:FileInformation[]
  paramsSubscription: Subscription;
  repositoryName: string;
  commitKey: string;
  clickedNode: FileInformation;
  parentParamSub:Subscription;

  constructor(private fileInfoService:FileInformationService, private activeRoute:ActivatedRoute) {
  }

  ngOnInit(): void {
    
    this.paramsSubscription=this.activeRoute.params
    .subscribe(
      (params:Params) =>{
        this.commitKey=params['key'];
      });

      this.fileInformationSub=this.fileInfoService.fileInformationSub.subscribe(res=>{
        this.fileInformation=[];
        this.fileInformation=this.fileInformation.concat(res);
        this.dataSource.data = this.fileInformation;
      });

      this.parentParamSub=this.activeRoute.parent.params.subscribe((params:Params)=>{
        this.repositoryName=params['name'];
        this.fileInfoService.getFileInformation(this.repositoryName,this.commitKey);
      })
  } 

 
  printContent(node:FileInformation)
  {
    this.clickedNode=node;
  }

  hasChild = (num: number, node: FileInformation) => !!node.children && node.children.length > 0;

  ngOnDestroy(): void {
    this.paramsSubscription.unsubscribe();
    this.parentParamSub.unsubscribe();
    this.fileInformationSub.unsubscribe();
  }
}


