import { ChangeDetectionStrategy, Component, computed, input, output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-custom-dropdown',
  imports: [CommonModule],
  templateUrl: './custom-dropdown.html',
  styleUrls: ['./custom-dropdown.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CustomDropdownComponent {
  options = input.required<{ value: string; label: string }[]>();
  
  selectionChanged = output<string>();
  dropdownToggled = output<boolean>();

  isOpen = signal(false);
  selectedOption = signal<{ value: string; label: string } | null>(null);

  selectedLabel = computed(() => this.selectedOption()?.label || 'Select your LOB');

  toggleDropdown() {
    this.isOpen.update(isOpen => !isOpen);
    this.dropdownToggled.emit(this.isOpen());
  }

  selectOption(option: { value: string; label: string }) {
    this.selectedOption.set(option);
    this.selectionChanged.emit(option.value);
    this.isOpen.set(false);
    this.dropdownToggled.emit(this.isOpen());
  }
}
