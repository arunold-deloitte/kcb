import { ChangeDetectionStrategy, Component, computed, signal } from '@angular/core';
import { TEST_RESULTS } from './admin.types';

@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.html',
  styleUrl: './admin-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdminPageComponent {
  testResults = signal(TEST_RESULTS);
  filter = signal('');

  filteredTestResults = computed(() => {
    const filterText = this.filter().toLowerCase();
    if (!filterText) {
      return this.testResults();
    }

    return this.testResults().filter(result =>
      result.firstName.toLowerCase().includes(filterText) ||
      result.lastName.toLowerCase().includes(filterText) ||
      result.email.toLowerCase().includes(filterText) ||
      result.lob.toLowerCase().includes(filterText)
    );
  });

  onFilter(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.filter.set(target.value);
  }
}
