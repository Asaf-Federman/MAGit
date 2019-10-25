import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoriesAccordionComponent } from './repositories-accordion.component';

describe('RepositoriesAccordionComponent', () => {
  let component: RepositoriesAccordionComponent;
  let fixture: ComponentFixture<RepositoriesAccordionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoriesAccordionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoriesAccordionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
