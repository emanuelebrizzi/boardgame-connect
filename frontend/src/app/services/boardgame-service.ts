import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Boardgame } from '../model/boardgame';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BoardgameService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/boardgames`;

  createBoardgame(boardgame: Boardgame): Observable<Boardgame> {
    return this.http.post<Boardgame>(`${this.apiUrl}/create`, boardgame);
  }
}
