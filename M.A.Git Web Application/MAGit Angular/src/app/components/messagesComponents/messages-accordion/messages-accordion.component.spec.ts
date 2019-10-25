import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MessagesAccordionComponent } from './messages-accordion.component';

describe('MessagesAccordionComponent', () => {
  let component: MessagesAccordionComponent;
  let fixture: ComponentFixture<MessagesAccordionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MessagesAccordionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MessagesAccordionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
