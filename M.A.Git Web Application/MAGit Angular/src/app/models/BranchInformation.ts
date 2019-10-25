export class BranchInformation{

    id:Number;
    branchName:string;
	commitKey:string;
	commitMessage:string;
    isHead:boolean;
    type:eBranchType
    
    constructor(id:Number,branchName:string, commitKey:string, 
        commitMessage:string, isHead:boolean, type:eBranchType){
        this.id=id;
        this.branchName=branchName;
        this.commitKey=commitKey;
        this.commitMessage=commitMessage;
        this.isHead=isHead;
        this.type=type;
    }
}

export enum eBranchType{
        localBranch="localBranch",
        remoteBranch="remoteBranch",
        remoteTrackingBranch="remoteTrackingBranch"
}