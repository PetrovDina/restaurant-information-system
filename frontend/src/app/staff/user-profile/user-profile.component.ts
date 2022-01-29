import { Component, OnInit } from '@angular/core';
import { Staff } from 'src/app/model/Staff';
import { StaffService } from 'src/app/services/staff/staff.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.less'],
})
export class UserProfileComponent implements OnInit {
  staff: Staff = {
    id: 0,
    firstName: '',
    lastName: '',
    phoneNumber: '',
    email: '',
    monthlyWage: 0,
  };

  constructor(private staffService: StaffService) {}

  ngOnInit(): void {
    this.staffService.getStaffMemberById().subscribe((res) => {
      this.staff = res;
    });
  }
}
