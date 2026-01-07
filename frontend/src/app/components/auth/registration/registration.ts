import { Component, effect, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserRole } from '../../../model/user';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth-service';
import { ErrorResponse } from '../../../error-response';
import { HttpErrorResponse } from '@angular/common/http';
import { RoleSelector } from '../../role-selector/role-selector';
import { SubmitButton } from '../../submit-button/submit-button';
import { FormAlert } from '../../form-alert/form-alert';

@Component({
  selector: 'app-registration',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatButtonToggleModule,
    RouterLink,
    RoleSelector,
    SubmitButton,
    FormAlert,
  ],
  templateUrl: './registration.html',
  styleUrl: './registration.scss',
})
export class Registration {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly role = signal<UserRole>('PLAYER');
  readonly errorMessage = signal<string | null>(null);
  readonly isLoading = signal<boolean>(false);

  readonly registerForm = this.formBuilder.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    name: ['', [Validators.required]],
    taxCode: [''],
    address: [''],
  });

  constructor() {
    effect(() => {
      const currentRole = this.role();
      this.updateValidators(currentRole);
    });
  }

  private updateValidators(role: UserRole): void {
    const taxControl = this.registerForm.controls.taxCode;
    const addressControl = this.registerForm.controls.address;

    if (role === 'ASSOCIATION') {
      taxControl.setValidators([Validators.required]);
      addressControl.setValidators([Validators.required]);
    } else {
      taxControl.clearValidators();
      addressControl.clearValidators();
    }

    // Refresh validity status
    taxControl.updateValueAndValidity();
    addressControl.updateValueAndValidity();
  }

  processRegistrationForm(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const formValue = this.registerForm.getRawValue();
    const role = this.role();

    // Construct Payload based on Role
    let requestPayload;

    if (role === 'ASSOCIATION') {
      requestPayload = {
        email: formValue.email,
        password: formValue.password,
        name: formValue.name,
        details: {
          taxCode: formValue.taxCode,
          address: formValue.address,
        },
      };
    } else {
      requestPayload = {
        email: formValue.email,
        password: formValue.password,
        name: formValue.name,
        details: null,
      };
    }

    this.authService.register(role, requestPayload).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        this.errorMessage.set(this.getErrorMessage(err));
      },
    });
  }

  private getErrorMessage(err: HttpErrorResponse): string {
    const apiError = err.error as ErrorResponse;
    if (apiError?.message) {
      return apiError.message;
    }
    if (err.status === 401) return 'Unauthorized access.';
    if (err.status === 403) return 'You do not have permission.';
    if (err.status === 409) return 'Resource already exists.';

    return 'An unexpected error occurred.';
  }
}
