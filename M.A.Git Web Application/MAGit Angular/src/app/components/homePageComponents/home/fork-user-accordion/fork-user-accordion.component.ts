import { Component, OnInit, Input, OnChanges, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { ForkServiceService } from 'src/app/Services/fork.service';
import { IRepositoryDetails } from 'src/app/models/IRepositoryDetails';
import { ActivatedRoute, Params } from '@angular/router';

@Component({
  selector: 'app-fork-user-accordion',
  templateUrl: './fork-user-accordion.component.html',
  styleUrls: ['./fork-user-accordion.component.css']
})
export class ForkUserAccordionComponent implements OnInit,OnChanges, OnDestroy {
  repositoryInformation:IRepositoryDetails[]=[];
  repositoryInformationSub:Subscription;
  clickedUser:string;
  paramSub: Subscription;

  constructor(private forkService:ForkServiceService, private activeRoute:ActivatedRoute) { }

  ngOnInit() {
    this.repositoryInformationSub=this.forkService.repositoryInformationSub.subscribe(res=>{
      this.repositoryInformation=res;
    });

    this.paramSub=this.activeRoute.params.subscribe((params:Params)=>{
      this.clickedUser=params['id'];
      this.forkService.fetchRepositories(this.clickedUser);
    });
  }

  ngOnChanges(changes: import("@angular/core").SimpleChanges): void {
    this.forkService.fetchRepositories(changes.clickedUser.currentValue);
  }

  subscription(){
    this.repositoryInformationSub=this.forkService.repositoryInformationSub.subscribe(res=>{
      this.repositoryInformation=res;
    });
  }

  onFork(repositoryName:string){
    this.subscription();
    this.forkService.fetchForks(repositoryName,this.clickedUser);
  }

  ngOnDestroy(): void {
    this.repositoryInformationSub.unsubscribe();
  }
}
