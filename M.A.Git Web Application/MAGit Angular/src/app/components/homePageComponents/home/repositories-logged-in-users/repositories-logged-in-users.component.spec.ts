import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoriesLoggedInUsersComponent } from './repositories-logged-in-users.component';

describe('RepositoriesLoggedInUsersComponent', () => {
  let component: RepositoriesLoggedInUsersComponent;
  let fixture: ComponentFixture<RepositoriesLoggedInUsersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoriesLoggedInUsersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoriesLoggedInUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
