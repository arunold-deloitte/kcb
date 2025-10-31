import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { CustomDropdownComponent } from '../custom-dropdown/custom-dropdown';
import { ButtonComponent } from '../button/button';
import { LobService } from '../../lob.service';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../user';

@Component({
  selector: 'app-lob-selection',
  templateUrl: './lob-selection.html',
  styleUrls: ['./lob-selection.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, CustomDropdownComponent, ButtonComponent]
})
export class LobSelectionComponent {
  private router = inject(Router);
  private lobService = inject(LobService);
  private http = inject(HttpClient);
  private userService = inject(UserService);

  user = this.userService.user;
  userName = computed(() => {
    const user = this.user();
    if (user) {
      return `${user.firstName}`;
    }
    return '';
  });

  selectedLob = signal<string | null>(null);
  dropdownOpen = signal(false);

  lobOptions = [
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

  ngOnInit() {
    this.lobService.getLobs().subscribe((lobs: any) => {
      console.log('Fetched LOBs:', lobs);
      // this.lobOptions = lobs;
    });
  }

  onLobSelection(lob: string) {
    this.selectedLob.set(lob);
    if (lob) {
      this.lobService.getQuestions(lob).subscribe((questions: any) => {
        console.log('Fetched Questions:', questions);
      });
    }
  }

  onDropdownToggle(isOpen: boolean) {
    this.dropdownOpen.set(isOpen);
  }

  startQuiz() {
    if (this.selectedLob()) {
        this.router.navigate(['/quiz']);
    }
  }
}
