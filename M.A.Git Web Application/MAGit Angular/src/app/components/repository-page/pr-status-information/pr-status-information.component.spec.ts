import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PrStatusInformationComponent } from './pr-status-information.component';

describe('PrStatusInformationComponent', () => {
  let component: PrStatusInformationComponent;
  let fixture: ComponentFixture<PrStatusInformationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PrStatusInformationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PrStatusInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
