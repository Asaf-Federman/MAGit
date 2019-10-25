import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PullRequestFilesComponent } from './pull-request-files.component';

describe('PullRequestFilesComponent', () => {
  let component: PullRequestFilesComponent;
  let fixture: ComponentFixture<PullRequestFilesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PullRequestFilesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PullRequestFilesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
