import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PRStatusesComponent } from './prstatuses.component';

describe('PRStatusesComponent', () => {
  let component: PRStatusesComponent;
  let fixture: ComponentFixture<PRStatusesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PRStatusesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PRStatusesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
