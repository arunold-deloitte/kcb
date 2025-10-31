import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonComponent } from '../button/button';
import { User, UserService } from '../../user';
// import { UserService } from '../user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule, ButtonComponent]
})
export class HomeComponent {
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private userService = inject(UserService);

  submitted = signal(false);
  form = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]]
  });

  get firstName() {
    return this.form.get('firstName');
  }

  get lastName() {
    return this.form.get('lastName');
  }

  get email() {
    return this.form.get('email');
  }

  navigateToLobSelection() {
    this.submitted.set(true);
    if (this.form.valid) {
      const { firstName, lastName, email } = this.form.value;
      if (firstName && lastName && email) {
        this.userService.user.set({firstName, lastName, email});
        this.router.navigate(['/lob-selection']);
      }
    //   this.userService.createUser({firstName, lastName, email} as User).subscribe(user => {
    //     this.userService.user.set(user);
    //     this.router.navigate(['/lob-selection']);
    // });
    }
  }
}
