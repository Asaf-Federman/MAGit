import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { AuthenticationService } from 'src/app/Services/authentication.service';
import { ILogin } from 'src/app/models/ILogin';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  form: FormGroup;
  login: ILogin;

  constructor(private auth: AuthenticationService, private route: Router) { }

  ngOnInit() {
    this.form = new FormGroup({
      nickName: new FormControl(null),
      password: new FormControl(null),
    });
  }

  onSubmit() {

    // this.validate();
    const nickName = this.form.value.nickName;
    const password = this.form.value.password;
    this.login = {nickName, password};
    // this.auth.login(this.login).subscribe(res=>{});
    this.route.navigate(['M.A.Git/home']);
  }

  validate() {
    // validate using the database;

  }

}
