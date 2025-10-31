import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ButtonComponent } from '../button/button';
import { QUESTIONS, Question } from '../../questions';
import { LobService } from '../../lob.service';
import { UserService } from '../../user';

@Component({
  selector: 'app-quiz',
  imports: [CommonModule, ButtonComponent],
  templateUrl: './quiz.html',
  styleUrls: ['./quiz.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class QuizComponent {
  private router = inject(Router);
  private lobService = inject(LobService);
  private userService = inject(UserService);

  questions = signal<Question[]>(QUESTIONS.map(q => ({...q, userAnswer: undefined, isCorrect: undefined})));
  currentQuestionIndex = signal(0);
  userAnswers = signal<{[key: number]: string}>({});

  currentQuestion = computed(() => this.questions()[this.currentQuestionIndex()]);
  selectedOption = computed(() => this.userAnswers()[this.currentQuestionIndex()]);
  isLastQuestion = computed(() => this.currentQuestionIndex() === this.questions().length - 1);

  ngOnInit(): void {
    this.lobService.getQuestions(this.lobService.selectedLob()!).subscribe((questions: any) => {
      // this.questions.set(questions);
    });  
  }

  selectOption(option: string) {
      this.userAnswers.update(answers => ({...answers, [this.currentQuestionIndex()]: option}));
  }

  submitAnswer() {
    if (this.selectedOption()) {
      const currentQ = this.currentQuestion();
      const selected = this.selectedOption();
      const isCorrect = selected === currentQ.answer;

      this.questions.update(qs => qs.map((q, index) => {
        if (index === this.currentQuestionIndex()) {
          return { ...q, userAnswer: selected, isCorrect };
        }
        return q;
      }));
    }
  }

  nextQuestion() {
    this.submitAnswer();
    if (this.isLastQuestion()) {
      this.finishQuiz();
    } else {
      this.currentQuestionIndex.update(i => i + 1);
    }
  }

  previousQuestion() {
    if (this.currentQuestionIndex() > 0) {
      this.currentQuestionIndex.update(i => i - 1);
    }
  }

  finishQuiz() {
    const results = this.questions().map(q => ({
      question: q.question,
      userAnswer: q.userAnswer || '',
      correctAnswer: q.answer,
      answer: q.answer,
      explanation: q.answerDescription,
      isCorrect: q.isCorrect === true,
    }));
    // this.userService.submitQuestion(this.questions()).subscribe((results) => {
    //   console.log(results);
    //   this.userService.result.set(results);
    //   this.router.navigate(['/results'], { state: { results } });
    // })
    this.router.navigate(['/results'], { state: { results } });
  }

  exitTest() {
    this.router.navigate(['/']);
  }
}
