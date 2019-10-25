import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { RegistrationComponent } from './components/registration/registration.component';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToolbarComponent } from './components/toolbar/toolbar.component';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatListModule} from '@angular/material/list';
import {MatButtonModule} from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { LoginComponent } from './components/login/login.component';
import { AppRoutingModule } from './app-routing.module';
import { HomeComponent } from './components/homePageComponents/home/home.component';
import {MatIconModule} from '@angular/material/icon';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import {MatExpansionModule} from '@angular/material/expansion';
import { RepositoryPageComponent } from './components/repository-page/repository-page.component';
import { RepositoriesAccordionComponent } from './components/homePageComponents/home/repositories-accordion/repositories-accordion.component';
import { RepositoriesLoggedInUsersComponent } from './components/homePageComponents/home/repositories-logged-in-users/repositories-logged-in-users.component';
import {ScrollingModule} from '@angular/cdk/scrolling';
import {MatStepperModule} from '@angular/material/stepper';
import {MatSelectModule} from '@angular/material/select';
import {MatBadgeModule} from '@angular/material/badge';
import { AuthInterceptor } from './Services/auth.interceptor';
import {MatMenuModule} from '@angular/material/menu';
import { MessagesAmountComponent } from './components/messagesComponents/messages-amount/messages-amount.component';
import { ForkMessageComponent } from './components/messagesComponents/messages-accordion/fork-message/fork-message.component';
import { PRMessageComponent } from './components/messagesComponents/messages-accordion/prmessage/prmessage.component';
import { PRStatusesComponent } from './components/messagesComponents/messages-accordion/prstatuses/prstatuses.component';
import {MatDialogModule} from '@angular/material/dialog';
import { AlertComponent } from './components/alert/alert.component';
import { ForkUserAccordionComponent } from './components/homePageComponents/home/fork-user-accordion/fork-user-accordion.component'
import {MatTableModule} from '@angular/material/table';
import { BranchTableComponent } from './components/repository-page/branch-table/branch-table.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSidenavModule} from '@angular/material/sidenav';
import { SidenavComponent } from './components/repository-page/sidenav/sidenav.component';
import { CommitTableComponent } from './components/repository-page/commit-table/commit-table.component';
import {MatChipsModule, MatChip} from '@angular/material/chips';
import { FileTableComponent } from './components/repository-page/file-table/file-table.component';
import {MatTreeModule} from '@angular/material/tree';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import { WorkingCopyInformationComponent } from './components/repository-page/working-copy-information/working-copy-information.component';
import {TextFieldModule} from '@angular/cdk/text-field';
import { CommitComponent } from './components/repository-page/commit/commit.component';
import { RepositoryInformationComponent } from './components/repository-page/repository-information/repository-information.component';
import { PullRequestComponent } from './components/repository-page/pull-request/pull-request.component';
import { PrStatusInformationComponent } from './components/repository-page/pr-status-information/pr-status-information.component';
import { PullRequestFilesComponent } from './components/repository-page/pull-request-files/pull-request-files.component';
import { MessagesAccordionComponent } from './components/messagesComponents/messages-accordion/messages-accordion.component';
import { DeleteMessageComponent } from './components/messagesComponents/messages-accordion/delete-message/delete-message.component';
import { ChatComponent } from './components/chat/chat.component';
import { ChatMessagesComponent } from './components/chat/chat-messages/chat-messages.component';
import { ChatUsersComponent } from './components/chat/chat-users/chat-users.component';


@NgModule({
  declarations: [
    AppComponent,
    RegistrationComponent,
    ToolbarComponent,
    LoginComponent,
    HomeComponent,
    RepositoryPageComponent,
    RepositoriesAccordionComponent,
    RepositoriesLoggedInUsersComponent,
    MessagesAmountComponent,
    ForkMessageComponent,
    PRMessageComponent,
    PRStatusesComponent,
    AlertComponent,
    ForkUserAccordionComponent,
    BranchTableComponent,
    SidenavComponent,
    CommitTableComponent,
    FileTableComponent,
    WorkingCopyInformationComponent,
    CommitComponent,
    RepositoryInformationComponent,
    PullRequestComponent,
    PrStatusInformationComponent,
    PullRequestFilesComponent,
    MessagesAccordionComponent,
    DeleteMessageComponent,
    ChatMessagesComponent,
    ChatComponent,
    ChatUsersComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatListModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    ReactiveFormsModule,
    AppRoutingModule,
    MatIconModule,
    HttpClientModule,
    MatExpansionModule,
    ScrollingModule,
    MatStepperModule,
    MatSelectModule,
    MatBadgeModule,
    MatMenuModule,
    MatDialogModule,
    MatTableModule,
    MatCheckboxModule,
    MatPaginatorModule,
    MatSidenavModule,
    MatChipsModule,
    MatTreeModule,
    MatProgressBarModule,
    TextFieldModule,
    FormsModule,
    ],
    exports:[
      MatToolbarModule,
      MatListModule,
      MatButtonModule,
      MatCardModule,
      MatFormFieldModule,
      MatInputModule,
      MatIconModule,
      MatExpansionModule,
      ScrollingModule,
      MatStepperModule,
      MatSelectModule,
      MatBadgeModule,
      MatMenuModule,
      MatDialogModule,
      MatTableModule,
      MatCheckboxModule,
      MatPaginatorModule,
      MatSidenavModule,
      MatChipsModule,
      MatTreeModule,
      MatProgressBarModule,
      TextFieldModule
    ],
    entryComponents:[AlertComponent],
  providers: [{provide:HTTP_INTERCEPTORS,useClass:AuthInterceptor,multi:true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
