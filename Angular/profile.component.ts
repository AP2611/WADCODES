import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {

  user: any;

  ngOnInit() {
    const isLoggedIn = localStorage.getItem('loggedIn');

    if (isLoggedIn) {
      this.user = JSON.parse(localStorage.getItem('user') || '{}');
    } else {
      alert('Please login first');
    }
  }
}