import {Component, OnInit} from '@angular/core';
import {UserAccountControllerService} from '../../services/services/user-account-controller.service';
import {AccountInformation} from '../../services/models/account-information';
import {LoaderComponent} from '../../common/loader/loader.component';

@Component({
  selector: 'app-users',
  imports: [
    LoaderComponent
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent implements OnInit {
  accounts: Array<AccountInformation> = [];
  isLoading = true;

  constructor(private userService: UserAccountControllerService) {
  }

  ngOnInit() {
    this.fetchUsers();
  }

  fetchUsers() {
    this.userService.getAllUsersAccounts().subscribe({
      next: (res) => {
        this.accounts = res;
        this.isLoading = false;
      }
    })
  }

  blockUser(userId: string) {
    this.userService.blockUserAccount({'accountId': userId as string}).subscribe();
    this.fetchUsers();
  }

  switchRole(userId: string) {
    this.userService.switchRole({'accountId': userId as string}).subscribe();
    this.fetchUsers();
  }

}
