import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Question } from './questions';
import { environment } from '../environments/environment';

export interface User {
  id?: string;
  firstName: string;
  lastName: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  user = signal<User | null>(null);
  result = signal<Question[] | null>(null);

  createUser(user: Omit<User, 'id'>): Observable<User> {
    return this.http.post<User>(`${environment.apiUrl}/participants/register`, user);
  }

  submitQuestion(question: Question[]){
    return this.http.post<Question[]>(`${environment.apiUrl}/participants/submit`, question);
  }
}
