import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ForkUserAccordionComponent } from './fork-user-accordion.component';

describe('ForkUserAccordionComponent', () => {
  let component: ForkUserAccordionComponent;
  let fixture: ComponentFixture<ForkUserAccordionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ForkUserAccordionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ForkUserAccordionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
