export interface Question {
  id: string;
  question: string;
  options: string[];
  answer: string;
  sop: string;
  lob: string;
  answerDescription: string;
  userAnswer?: string;
  isCorrect?: boolean;
}

export const QUESTIONS: Question[] = [
  {
    id: '1',
    question: 'What is the capital of France?',
    options: ['Paris', 'London', 'Berlin', 'Madrid'],
    answer: 'Paris',
    sop: 'Geography',
    lob: 'General Knowledge',
    answerDescription: "Paris, France's capital, is a major European city and a global center for art, fashion, gastronomy and culture.",
  },
  {
    id: '2',
    question: 'Which planet is known as the Red Planet?',
    options: ['Venus', 'Mars', 'Jupiter', 'Saturn'],
    answer: 'Mars',
    sop: 'Astronomy',
    lob: 'General Knowledge',
    answerDescription: 'Mars is often called the "Red Planet" because of its reddish appearance.'
  },
  {
    id: '3',
    question: 'What is the largest ocean on Earth?',
    options: ['Atlantic Ocean', 'Indian Ocean', 'Arctic Ocean', 'Pacific Ocean'],
    answer: 'Pacific Ocean',
    sop: 'Geography',
    lob: 'General Knowledge',
    answerDescription: 'The Pacific Ocean is the largest and deepest of Earth\'s five oceans.'
  },
  {
    id: '4',
    question: 'What is the chemical symbol for gold?',
    options: ['Au', 'Ag', 'Go', 'Gd'],
    answer: 'Au',
    sop: 'Chemistry',
    lob: 'General Knowledge',
    answerDescription: 'The chemical symbol for gold, Au, comes from the Latin word "aurum."'
  },
  {
    id: '5',
    question: 'Who wrote "To Kill a Mockingbird"?',
    options: ['Harper Lee', 'J.K. Rowling', 'Ernest Hemingway', 'Mark Twain'],
    answer: 'Harper Lee',
    sop: 'Literature',
    lob: 'General Knowledge',
    answerDescription: 'The classic novel "To Kill a Mockingbird" was written by Harper Lee.'
  },
];
