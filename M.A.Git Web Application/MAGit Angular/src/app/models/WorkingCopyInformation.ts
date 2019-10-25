export class WorkingCopyInformation{
    fileName:string;
    path:string
    childrenNames:WorkingCopyInformation[]
    content:string;

    constructor(fileName:string,path:string,childrenNames:WorkingCopyInformation[],content:string){
        this.fileName=fileName;
        this.path=path;
        this.childrenNames=childrenNames;
        this.content=content;
    }
}