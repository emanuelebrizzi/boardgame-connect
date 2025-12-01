import { ReservationDetail } from './reservation-detail';
import { ComponentFixture, TestBed } from '@angular/core/testing';

describe('ReservationInfo', () => {
  let component: ReservationDetail;
  let fixture: ComponentFixture<ReservationDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservationDetail],
    }).compileComponents();

    fixture = TestBed.createComponent(ReservationDetail);
    component = fixture.componentInstance;

    await fixture.whenStable();
  });

  it('should initialize HTML template', () => {
    expect(component).toBeTruthy();
  });
});
