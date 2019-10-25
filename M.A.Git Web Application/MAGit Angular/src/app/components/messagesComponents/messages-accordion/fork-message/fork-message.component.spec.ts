import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ForkMessageComponent } from './fork-message.component';

describe('ForkMessageComponent', () => {
  let component: ForkMessageComponent;
  let fixture: ComponentFixture<ForkMessageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ForkMessageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ForkMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
