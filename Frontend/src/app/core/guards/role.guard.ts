import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { RolUsuario } from '../models/models';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRoles = route.data['expectedRoles'] as RolUsuario[];
  const user = authService.currentUser();

  if (user && expectedRoles && expectedRoles.includes(user.rol)) {
    return true;
  }

  router.navigate(['/dashboard']);
  return false;
};
