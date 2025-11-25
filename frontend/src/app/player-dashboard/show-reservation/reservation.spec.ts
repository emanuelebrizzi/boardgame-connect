import { Reservation } from './reservation.model';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservationComponent } from './reservation.component';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { DatePipe } from '@angular/common';

describe('ReservationComponent', () => {
  let component: ReservationComponent;
  let fixture: ComponentFixture<ReservationComponent>;
  let datePipe: DatePipe;
  let expectedReservation: Reservation;

  let cardTitle: DebugElement;
  let cardSubtitle: DebugElement;
  let infoRows: DebugElement[];
  let chip: DebugElement;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReservationComponent],
      providers: [DatePipe],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(ReservationComponent);

    component = fixture.componentInstance;
    datePipe = TestBed.inject(DatePipe);

    cardTitle = fixture.debugElement.query(By.css('.card-title'));
    cardSubtitle = fixture.debugElement.query(By.css('.card-subtitle'));
    infoRows = fixture.debugElement.queryAll(By.css('.info-row'));
    chip = fixture.debugElement.query(By.css('mat-chip'));

    expectedReservation = {
      id: '1',
      game: 'Root',
      association: 'La Gilda del Cassero',
      currentPlayers: 2,
      maxPlayers: 4,
      startTime: '2025-11-20T21:00:00Z',
      endTime: '2025-11-20T22:30:00Z',
    };

    fixture.componentRef.setInput('reservation', expectedReservation);
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should display reservation information', () => {
    const expectedAssociationName = expectedReservation.association.toUpperCase();
    const dateRow = infoRows[0];
    const timeRow = infoRows[1];
    const expectedDateString = datePipe.transform(expectedReservation.startTime, 'EEE, d MMM y');
    const expectedStartTime = datePipe.transform(expectedReservation.startTime, 'HH:mm');
    const expectedEndTime = datePipe.transform(expectedReservation.endTime, 'HH:mm');

    expect(cardTitle.nativeElement.textContent).toContain(expectedReservation.game);
    expect(cardSubtitle.nativeElement.textContent).toContain(expectedAssociationName);
    expect(dateRow.nativeElement.textContent).toContain('calendar_today');
    expect(dateRow.nativeElement.textContent).toContain(expectedDateString);
    expect(timeRow.nativeElement.textContent).toContain('schedule');
    expect(timeRow.nativeElement.textContent).toContain(
      `${expectedStartTime} - ${expectedEndTime}`
    );
  });

  it('should display the participants availability chip when open', () => {
    expect(chip.nativeElement.textContent).toContain(
      `${expectedReservation.currentPlayers} / ${expectedReservation.maxPlayers}`
    );
    expect(chip.classes['chip-open']).toBeTruthy();
    expect(chip.classes['chip-closed']).toBeFalsy();
  });

  it('should display the participants availability chip when closed', () => {
    const fullReservation = {
      ...expectedReservation,
      currentPlayers: expectedReservation.maxPlayers,
    };

    fixture.componentRef.setInput('reservation', fullReservation);
    fixture.detectChanges();

    expect(chip.nativeElement.textContent).toContain(
      `${fullReservation.currentPlayers} / ${fullReservation.maxPlayers}`
    );
    expect(chip.classes['chip-open']).toBeFalsy();
    expect(chip.classes['chip-closed']).toBeTruthy();
  });
});
