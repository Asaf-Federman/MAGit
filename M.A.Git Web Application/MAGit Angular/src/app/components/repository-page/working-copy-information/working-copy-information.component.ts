import { NestedTreeControl } from '@angular/cdk/tree';
import { Component, OnInit, ViewChild, ElementRef, OnDestroy, Output, EventEmitter } from '@angular/core';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Params } from '@angular/router';
import { WorkingCopyInformation } from 'src/app/models/WorkingCopyInformation';
import { WorkingCopyInformationService } from 'src/app/Services/working-copy-information.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-working-copy-information',
  templateUrl: './working-copy-information.component.html',
  styleUrls: ['./working-copy-information.component.css']
})
export class WorkingCopyInformationComponent implements OnInit, OnDestroy {

  @ViewChild('contentTextArea') private contentTextArea: ElementRef;
  treeControl = new NestedTreeControl<WorkingCopyInformation>(node => node===null ? null:node.childrenNames);
  dataSource = new MatTreeNestedDataSource<WorkingCopyInformation>();
  workingCopyInformationSub: Subscription;
  workingCopyInformation: WorkingCopyInformation[]
  paramsSubscription: Subscription;
  repositoryName: string;
  commitKey: string;
  clickedNode: WorkingCopyInformation;
  clickedNewFile: boolean;
  form: FormGroup;
  changes: boolean = false;
  extensionArray:string[]=[".txt"];
  extensionChoice:string;
  @Output() changesEmitter: EventEmitter<boolean> = new EventEmitter();

  constructor(private workingCopyInfoService: WorkingCopyInformationService, private activeRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.initializeForm();

    this.workingCopyInformationSub = this.workingCopyInfoService.workingCopyInformationSub.subscribe(res => {
      this.updateDataSource(res);
    });

    this.paramsSubscription = this.activeRoute.parent.params
    .subscribe(
      (params: Params) => {
        this.repositoryName = params['name'];
        this.workingCopyInfoService.getWorkingCopyInformation(this.repositoryName);
      });
  }

  updateDataSource(res: WorkingCopyInformation) {
    if(res!==null){
      this.workingCopyInformation = [];
      this.workingCopyInformation = this.workingCopyInformation.concat(res);
      this.dataSource.data = this.workingCopyInformation;
    }else{
      this.dataSource.data=null;
    }

  }

  printContent(node: WorkingCopyInformation) {
    this.clickedNode = node;
    this.clickedNewFile = false;
  }

  hasChild = (num: number, node: WorkingCopyInformation) => node===null ? null:!!node.childrenNames && node.childrenNames.length > 0;

  saveChanges() {
    const content: string = this.contentTextArea.nativeElement.value;
    if (content != this.clickedNode.content) {
      const newNode: WorkingCopyInformation = { fileName: this.clickedNode.fileName, path: this.clickedNode.path, childrenNames: null, content: content };
      this.workingCopyInfoService.saveChanges(newNode, this.repositoryName, "save");
      this.EmitChanges();
    }

    this.clickedNode = null;
  }

  EmitChanges() {
    this.changes = true;
    this.changesEmitter.emit(this.changes);
  }

  removeFile() {
    const content: string = this.contentTextArea.nativeElement.value;
    const newNode: WorkingCopyInformation = { fileName: this.clickedNode.fileName, path: this.clickedNode.path, childrenNames: null, content: content };
    this.workingCopyInfoService.saveChanges(newNode, this.repositoryName, "remove");
    this.EmitChanges();
    this.clickedNode = null;
  }

  newFileClick() {
    this.clickedNewFile = true;
    this.clickedNode = null;
  }

  initializeForm() {
    this.form = new FormGroup({
      fileName: new FormControl(null, { validators: [Validators.required] }),
      extension: new FormControl(null, { validators: [Validators.required] }),
      route: new FormControl(null, { validators: [Validators.required] }),
      newFileContent: new FormControl(null),
    });
  }

  clickFolder() {
    this.clickedNewFile = false;
  }

  onSubmitNewFile() {
    const isValid = this.validateFile()
    if (isValid) {
      const fileName=this.form.value.fileName+this.form.value.extension;
      const file: WorkingCopyInformation = new WorkingCopyInformation(fileName, this.form.value.route, null, this.form.value.newFileContent);
      this.workingCopyInfoService.saveChanges(file, this.repositoryName, "newFile");
      this.clickedNewFile = false;
      this.form.reset();
      this.EmitChanges();
    }
  }

  validateFile(): boolean {
    // return this.validatePath() && this.validateName()
    return true;
  }

  // validatePath():boolean{

  // }
  // validateName():boolean{

  // }

  ngOnDestroy(): void {
    this.paramsSubscription.unsubscribe();
    this.workingCopyInformationSub.unsubscribe();
  }
}

