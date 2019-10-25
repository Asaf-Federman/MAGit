import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkingCopyInformationComponent } from './working-copy-information.component';

describe('WorkingCopyInformationComponent', () => {
  let component: WorkingCopyInformationComponent;
  let fixture: ComponentFixture<WorkingCopyInformationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkingCopyInformationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkingCopyInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
