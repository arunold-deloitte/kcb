import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Question } from './questions';
import { QuizResults } from './models';
import { environment } from '../environments/environment';


export interface Lob {
  value: string;
  label: string;
}

@Injectable({
  providedIn: 'root'
})
export class LobService {
  private http = inject(HttpClient);
  selectedLob = signal<string | null>(null);
  quizResults = signal<QuizResults | null>(null);

  private lobs: Lob[] = [
    { value: 'alm', label: 'ALM' },
    { value: 'hp', label: 'HP' },
    { value: 'hom', label: 'HOM' },
    { value: 'sam-clh', label: 'SAM / CLH' },
    { value: 'r-r', label: 'R&R' },
    { value: 'npi', label: 'NPI' },
    { value: 'gpn', label: 'GPN' },
    { value: 'invoice', label: 'Invoice' },
    { value: 'vmo-ma', label: 'VMO - M&A' },
    { value: 'supplier-catalog', label: 'Supplier Catalog' }
  ];

  getLobs(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.apiUrl}/lobs`);
  }

  getQuestions(lob: string): Observable<Question[]> {
    return this.http.get<Question[]>(`${environment.apiUrl}/questions?lob=${lob}`);

  }
}
