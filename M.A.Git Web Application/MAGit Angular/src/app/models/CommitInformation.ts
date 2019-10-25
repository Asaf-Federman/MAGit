export class CommitInformation{
    id:number
    encryptionKey:string;
    message:string;
    creationDate:string;
    author:string;
    relatedBranches:coloredListItem[]

    constructor(id:number,encryptionKey:string, message:string, creationDate:string, author:string, relatedBranches:string[]){
        this.id=id
        this.encryptionKey=encryptionKey;
        this.message=message;
        this.creationDate=creationDate;
        this.author=author;
        this.relatedBranches=relatedBranches.map(branchName=>new coloredListItem(branchName));
    }
}

export class coloredListItem{
    name:string;
    color:string;

    constructor(name:string){
        this.name=name;
        this.color=this.getRandomColor();
    }

    getRandomColor() {
        var colorArray:string[]=["primary","warn","accent"]
        var colorIndex = Math.floor(Math.random() * 3);
        return colorArray[colorIndex];
        // var color = Math.floor(0x1000000 * Math.random()).toString(16);
        // return '#' + ('000000' + color).slice(-6);
      }
}