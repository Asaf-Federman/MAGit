import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ISignUp } from 'src/app/models/ISignUp';
import { AuthenticationService } from 'src/app/Services/authentication.service';
import { Router } from '@angular/router';
import { IUserAuth } from 'src/app/models/IUserAuth';
import { Subscription } from 'rxjs';
@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit,OnDestroy {

  errorSub:Subscription;
  form: FormGroup;
  signUp: ISignUp;
  error:string="";
  constructor(private auth: AuthenticationService) { }

  ngOnInit() {
    this.errorSub=this.auth.errorSub.subscribe(error=>{
      this.error=error;
    })
    
    this.form = new FormGroup({
      nickName: new FormControl(null, { validators: [Validators.required, Validators.minLength(4)] }),
      password: new FormControl(null, {validators: [Validators.required, Validators.minLength(4)]}),
    });
  }

  public onSubmit() {
    const userName = this.form.value.nickName;
    const password=this.form.value.password;
    const user:IUserAuth={userName:userName,password:password}
    this.auth.signUp(user);
  }

  ngOnDestroy(): void {
    this.errorSub.unsubscribe();
  }
}
