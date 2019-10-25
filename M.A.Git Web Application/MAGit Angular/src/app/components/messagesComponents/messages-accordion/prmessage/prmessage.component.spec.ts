import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PRMessageComponent } from './prmessage.component';

describe('PRMessageComponent', () => {
  let component: PRMessageComponent;
  let fixture: ComponentFixture<PRMessageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PRMessageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PRMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
