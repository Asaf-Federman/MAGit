import { Component, OnInit, OnDestroy } from '@angular/core';
import { RepositoryInformationService } from 'src/app/Services/repository-information.service';
import { RepositoryInformation } from 'src/app/models/repositoryInformation';
import { Subscription } from 'rxjs';
import { Params, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-repository-information',
  templateUrl: './repository-information.component.html',
  styleUrls: ['./repository-information.component.css']
})
export class RepositoryInformationComponent implements OnInit, OnDestroy {
  repositoryInformation: RepositoryInformation = null;
  repositoryInformationSub: Subscription;
  repositoryName: string;
  paramsSubscription: Subscription;

  constructor(private repositoryInfoService: RepositoryInformationService, private activeRoute: ActivatedRoute) { }

  ngOnInit() {
    this.repositoryInformationSub = this.repositoryInfoService.repositoryInformationSub.subscribe(res => {
      this.repositoryInformation = res;
    });

    this.paramsSubscription = this.activeRoute.params
    .subscribe(
      (params: Params) => {
        this.repositoryName = params.name;
        this.repositoryInfoService.getRepositoryInformation(this.repositoryName)
      });
  }

  ngOnDestroy(): void {
    this.repositoryInformationSub.unsubscribe();
    this.paramsSubscription.unsubscribe();
  }

}
