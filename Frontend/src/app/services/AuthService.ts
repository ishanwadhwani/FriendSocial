import { Injectable } from '@angular/core';
import jwt_decode from 'jwt-decode';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { User } from '../models/profile.model';
import { ProfileService } from './ProfileService';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/v1';
  public currentUserSubject: BehaviorSubject<User | null> =
    new BehaviorSubject<User | null>(null);
  public currentUser$: Observable<User | null> =
    this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    private profileService: ProfileService
  ) {}

  async login(credentials: any): Promise<User> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    };

    try {
      const response = await this.http
        .post<any>(
          'http://localhost:8080/api/v1/auth/authenticate',
          credentials,
          httpOptions
        )
        .toPromise();

      const token = response.token;
      localStorage.setItem('token', token);
      this.router.navigate(['/home']);

      const fetchedUser = await this.profileService
        .fetchLoggedInUserData(credentials.handle)
        .toPromise();
      this.currentUserSubject.next(fetchedUser);

      return fetchedUser;
    } catch (error) {
      console.error('Login failed:', error);
      throw error; // Re-throw the error to be handled by the caller
    }
  }

  async register(user: any): Promise<User> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    };

    try {
      const response = await this.http
        .post<any>(
          'http://localhost:8080/api/v1/auth/register',
          user,
          httpOptions
        )
        .toPromise();

      const token = response.token;
      localStorage.setItem('token', token);
      this.router.navigate(['/home']);

      const fetchedUser = await this.profileService
        .fetchLoggedInUserData(user.handle)
        .toPromise();
      this.currentUserSubject.next(fetchedUser);

      return fetchedUser;
    } catch (error) {
      console.error('Registration failed:', error);
      throw error; // Re-throw the error to be handled by the caller
    }
  }

  getUserIdFromToken(): number {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken: any = jwt_decode(token);
      return decodedToken.userId;
    }
    return -1;
  }

  // Grabs the username/email from the authentication token
  getHandle(): string {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken: any = jwt_decode<any>(token);
      return decodedToken.handle;
    }
    return '';
  }

  // Confirms whether the profile being routed to is owned by current user
  viewingProfile(userId: number | undefined): boolean {
    const currentUserId = this.getUserIdFromToken();
    console.log('currentUserId: ' + currentUserId);
    return currentUserId === userId;
  }

  onProfileClick(): void {
    // this.postService.clearPosts(); // Clear the posts before navigating to the profile page
    this.router.navigate(['/profile', this.getHandle()]);
  }

  updateCurrentUser(user: any): void {
    this.currentUserSubject.next(user);
    console.log(this.currentUserSubject);
  }
}
