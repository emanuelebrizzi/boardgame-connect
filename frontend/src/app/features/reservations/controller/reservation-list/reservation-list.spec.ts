import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservationListComponent } from './reservation-list';

describe('ReservationList', () => {
  let component: ReservationListComponent;
  let fixture: ComponentFixture<ReservationListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservationListComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ReservationListComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
