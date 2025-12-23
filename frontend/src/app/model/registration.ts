export interface AssociationDetails {
  taxCode: string;
  address: string;
}

export interface RegisterRequest<T = unknown> {
  email: string;
  password: string;
  name: string;
  details: T | null;
}

export type PlayerRegisterRequest = RegisterRequest<null>;
export type AssociationRegisterRequest = RegisterRequest<AssociationDetails>;
