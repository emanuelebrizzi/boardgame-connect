import { Component, model } from '@angular/core';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { UserRole } from '../../../models/user';

@Component({
  selector: 'app-role-selector',
  imports: [MatButtonToggleModule],
  templateUrl: './role-selector.html',
  styleUrl: './role-selector.scss',
})
export class RoleSelector {
  readonly role = model.required<UserRole>();
}
