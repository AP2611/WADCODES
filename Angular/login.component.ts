import { Component } from '@angular/core';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {

  email = '';
  password = '';

  login() {
    const user = JSON.parse(localStorage.getItem('user') || '{}');

    if (user.email === this.email && user.password === this.password) {
      localStorage.setItem('loggedIn', 'true');
      alert('Login Success');
    } else {
      alert('Wrong email or password');
    }
  }
}