import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home';
import { LobSelectionComponent } from './components/lob-selection/lob-selection';
import { QuizComponent } from './components/quiz/quiz';
import { ResultsPageComponent } from './components/results-page/results-page';
import { authGuard } from './auth.guard';
import { AdminPageComponent } from './components/admin-page/admin-page';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { 
    path: 'lob-selection', 
    component: LobSelectionComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'quiz', 
    component: QuizComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'results', 
    component: ResultsPageComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'admin', 
    component: AdminPageComponent
  },
];
