import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateBoardgame } from './create-boardgame';

describe('CreateBoardgame', () => {
  let component: CreateBoardgame;
  let fixture: ComponentFixture<CreateBoardgame>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateBoardgame]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateBoardgame);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
