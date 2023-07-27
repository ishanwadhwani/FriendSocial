import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProfileService } from '../../services/ProfileService';
import { User } from '../../models/profile.model';
import { AuthService } from '../../services/AuthService';

@Component({
  selector: 'app-profile-page',
  templateUrl: './profile-page.component.html',
  styleUrls: ['./profile-page.component.css'],
})
export class ProfilePageComponent implements OnInit {
  userId: number = 0;
  username: string = '';
  userPic: string = '';
  bio: string = '';
  posts: any[] = [];
  isProfilePage: boolean = true;
  user!: User;

  constructor(
    private route: ActivatedRoute,
    private profileService: ProfileService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Get the username from the route parameters
    this.route.params.subscribe((params) => {
      this.username = params['username'];
      // Fetch profile information from the backend
      this.fetchProfileInfo();
    });
  }

  getUsername(): string | null {
    return this.authService.getUsername();
  }

  fetchProfileInfo(): void {
    // Make an HTTP request to fetch the profile information based on the username
    this.profileService.getUserProfile(this.username).subscribe(
      (data) => {
        this.userId = data.id;
        this.userPic = data.userPic;
        this.user = data;
      },
      (error) => {
        console.error('Error fetching profile information:', error);
      }
    );
  }
}
