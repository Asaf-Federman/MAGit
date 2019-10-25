import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegistrationComponent } from './components/registration/registration.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/homePageComponents/home/home.component';
import { RepositoryPageComponent } from './components/repository-page/repository-page.component';
import { AuthGuard } from './Services/authGuard';
import { CommitComponent } from './components/repository-page/commit/commit.component';
import { BranchTableComponent } from './components/repository-page/branch-table/branch-table.component';
import { WorkingCopyInformationComponent } from './components/repository-page/working-copy-information/working-copy-information.component';
import { FileTableComponent } from './components/repository-page/file-table/file-table.component';
import { CommitTableComponent } from './components/repository-page/commit-table/commit-table.component';
import { PullRequestComponent } from './components/repository-page/pull-request/pull-request.component';
import { PrStatusInformationComponent } from './components/repository-page/pr-status-information/pr-status-information.component';
import { PullRequestFilesComponent } from './components/repository-page/pull-request-files/pull-request-files.component';
import { ForkUserAccordionComponent } from './components/homePageComponents/home/fork-user-accordion/fork-user-accordion.component';
import { RepositoriesAccordionComponent } from './components/homePageComponents/home/repositories-accordion/repositories-accordion.component';
import { MessagesAccordionComponent } from './components/messagesComponents/messages-accordion/messages-accordion.component';
import { ChatComponent } from './components/chat/chat.component';

const routes: Routes = [
  { path: '', redirectTo: 'registration', pathMatch: 'full' },
  { path: 'registration', component: RegistrationComponent },
  { path: 'messages', component: MessagesAccordionComponent, canActivate: [AuthGuard] },
  { path: 'chat', component: ChatComponent, canActivate: [AuthGuard] },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard], children: [
      { path: '', redirectTo: 'information', pathMatch: 'full' },
      { path: 'information', component: RepositoriesAccordionComponent },
      { path: 'view/:id/repositories', component: ForkUserAccordionComponent },
    ]
  },
  {
    path: 'repository/:name', component: RepositoryPageComponent, canActivate: [AuthGuard], children: [
      { path: '', redirectTo: 'branches', pathMatch: 'full' },
      { path: 'commit', component: CommitComponent },
      { path: 'pull-request', component: PullRequestComponent },
      { path: 'pull-request-status-information', component: PrStatusInformationComponent },
      { path: 'pull-request-status-files/:statusId', component: PullRequestFilesComponent },
      { path: 'branches', component: BranchTableComponent },
      { path: 'workingCopy', component: WorkingCopyInformationComponent },
      { path: 'branch/:branchName/commits', component: CommitTableComponent },
      { path: 'branch/:branchName/commit/:key/files', component: FileTableComponent },
    ]
  },

  { path: '**', component: HomeComponent, canActivate: [AuthGuard] },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule],
  providers: [AuthGuard]
})
export class AppRoutingModule { }