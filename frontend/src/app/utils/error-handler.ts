import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from '../models/error';
export function extractErrorMessage(err: unknown): string {
  if (err instanceof HttpErrorResponse) {
    const apiError = err.error as ErrorResponse;

    if (apiError?.message) {
      return apiError.message;
    }

    return `Server Error: ${err.status}`;
  }

  return 'An unexpected error occurred.';
}
