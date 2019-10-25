import { HttpInterceptor } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor{
    constructor(private auth:AuthenticationService){

    }

    intercept(req: import("@angular/common/http").HttpRequest<any>, next: import("@angular/common/http").HttpHandler): import("rxjs").Observable<import("@angular/common/http").HttpEvent<any>> {
        const authToken=req.clone({
            headers:req.headers.set("Authorization",this.auth.userName)
        });

        return next.handle(authToken);
    }

}