import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, CanActivate, ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class AuthGuard implements CanActivate {
    constructor(private auth: AuthenticationService, private router: Router, private activeRoute:ActivatedRoute) { }

    canActivate(route: ActivatedRouteSnapshot,
        state: import("@angular/router").RouterStateSnapshot): boolean
        | import("@angular/router").UrlTree
        | Observable<boolean | import("@angular/router").UrlTree>
        | Promise<boolean |
            import("@angular/router").UrlTree> {
        const auth = this.auth.userAuth;
        if (auth === null) {
            this.router.navigate(['registration']);
            return false;
        }

        return true;
    }
}