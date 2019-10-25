import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MessagesAmountComponent } from './messages-amount.component';

describe('MessagesAmountComponent', () => {
  let component: MessagesAmountComponent;
  let fixture: ComponentFixture<MessagesAmountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MessagesAmountComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MessagesAmountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
