import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `
    <div class="input-container">
      <label>{{ label() }}</label>
      <input [formControl]="control()" [placeholder]="placeholder()"/>
    </div>
  `,
  styles: [`
    .input-container {
      display: flex;
      flex-direction: column;
      margin-bottom: 1rem;
      width: 100%;
    }
    label {
      margin-bottom: 0.5rem;
      text-align: left;
    }
    input {
      padding: 0.5rem;
      border: 1px solid #ccc;
      border-radius: 4px;
      width: 100%;
      box-sizing: border-box;
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InputComponent {
  label = input.required<string>();
  placeholder = input<string>('');
  control = input.required<FormControl>();
}
