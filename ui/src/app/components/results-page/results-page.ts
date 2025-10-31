import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { Router } from '@angular/router';
import { ButtonComponent } from '../button/button';
import { LobService } from '../../lob.service';
import { Answer, QuizResults } from '../../models';

@Component({
  selector: 'app-results-page',
  imports: [CommonModule, ButtonComponent, DecimalPipe],
  templateUrl: './results-page.html',
  styleUrls: ['./results-page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResultsPageComponent {
  private router = inject(Router);
  private lobService = inject(LobService);

  results = this.lobService.quizResults;
  filter = signal<'all' | 'correct' | 'incorrect'>('all');
  searchTerm = signal('');

  constructor() {
    // Mock data for demonstration purposes
    const mockResults: QuizResults = {
      id: 'vGBbcvATuJHbNDwGkd4K',
      participantId: '4OiDG5H1R4UXUNcCnSuN',
      answers: [
        {
          id: '0Gc9vyuvDE0B7jA7KfhR',
          question: 'What action is taken in Step 11 for hardware BPO purchase requests, specifically for HCL?',
          answer: 'HCL will use go/createpr to initiate requests.',
          options: [
            'HCL will approve the requests',
            'HCL will use go/createpr to initiate requests',
            'HCL will conduct the final review',
            'HCL will input demand forecasts',
          ],
          sop: 'HP - Blanket Purchase Order Process',
          lob: 'Procurement',
          selectedOption: 'HCL will use go/createpr to initiate requests.',
          answerDescription: 'Step 11 states, \'HCL will use go/createpr to initiate hardware BPO purchase requests going forward.\'.',
        },
        {
          id: '0l54ilDbJNfdnO1d9BYV',
          question: 'Who receives the \'More information required\' remark if the PO owner needs further clarification?',
          answer: 'The Buying Team.',
          options: ['The Channel Manager', 'The Demand Planning Team', 'The Buying Team', 'Deloitte'],
          sop: 'HP - Blanket Purchase Order Process',
          lob: 'Procurement',
          selectedOption: 'The Buying Team.',
          answerDescription: 'Step 10 of the Detailed Process states, \'If more information is needed, tag the Buying Team with a “More information required” remark.\'.',
        },
        {
            id: "3",
            question: "What does SOP stand for?",
            answer: "Standard Operating Procedure",
            options: ["Standard Operating Procedure", "Statement of Purpose", "Systematic Order Processing", "Service Offer Proposal"],
            sop: "General-01",
            lob: "General",
            selectedOption: "Statement of Purpose", // Incorrect answer
            answerDescription: "SOP stands for Standard Operating Procedure, which is a set of step-by-step instructions compiled by an organization to help workers carry out complex routine operations.",
        },
        {
          id: '4',
          question: 'What is the purpose of an IT help desk?',
          options: ['To reset passwords', 'To provide technical support', 'To order new hardware', 'To develop software'],
          answer: 'To provide technical support',
          selectedOption: 'To reset passwords', // Incorrect answer
          sop: 'IT-Support-01',
          lob: 'IT',
          answerDescription: 'The primary purpose of an IT help desk is to provide technical support and assistance to users.',
        }
      ],
    };
    this.lobService.quizResults.set(mockResults);
  }

  processedResults = computed(() => {
    const res = this.results();
    if (!res) return null;

    const processedAnswers = res.answers.map((a: Answer) => ({ ...a, isCorrect: a.selectedOption === a.answer }));
    const correct = processedAnswers.filter((a: Answer) => a.isCorrect).length;
    const total = processedAnswers.length;
    const score = total > 0 ? (correct / total) * 100 : 0;

    return { ...res, answers: processedAnswers, score };
  });

  correctCount = computed(() => this.processedResults()?.answers.filter((r: Answer) => r.isCorrect).length ?? 0);
  incorrectCount = computed(() => (this.processedResults()?.answers.length ?? 0) - this.correctCount());

  filteredQuestions = computed(() => {
    const results = this.processedResults();
    if (!results) {
      return [];
    }

    let filtered: Answer[] = results.answers;

    switch (this.filter()) {
      case 'correct':
        filtered = filtered.filter(r => r.isCorrect);
        break;
      case 'incorrect':
        filtered = filtered.filter(r => !r.isCorrect);
        break;
    }

    const term = this.searchTerm().toLowerCase();
    if (term) {
      filtered = filtered.filter(r => r.question.toLowerCase().includes(term));
    }

    return filtered;
  });

  setFilter(filter: 'all' | 'correct' | 'incorrect') {
    this.filter.set(filter);
  }

  onSearch(event: Event) {
    const term = (event.target as HTMLInputElement).value;
    this.searchTerm.set(term);
  }

  navigateToLobSelection() {
    this.router.navigate(['/lob-selection']);
  }
}
