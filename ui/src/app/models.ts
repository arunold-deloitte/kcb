export interface Answer {
  id: string;
  question: string;
  answer: string;
  options: string[];
  sop: string;
  lob: string;
  selectedOption: string;
  answerDescription: string;
  isCorrect?: boolean;
}

export interface QuizResults {
  id: string;
  participantId: string;
  answers: Answer[];
  score?: number;
}
