import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { TableOrderComponent } from 'src/app/order-components/table-order/table-order.component';

@Component({
  selector: 'app-waiter-page',
  templateUrl: './waiter-page.component.html',
  styleUrls: ['./waiter-page.component.less'],
})
export class WaiterPageComponent implements OnInit {
  constructor(public dialog: MatDialog) {}

  ngOnInit(): void {}

  onTableClick(id: number) {
    const dialogRef = this.dialog.open(TableOrderComponent, {
      data: {
        tableId: id,
      },
      disableClose: true,
      width: '70%',
      height: '70%',
    });

    dialogRef.afterClosed().subscribe((result) => {
      console.log(`The dialog for table #${result} was closed`);
    });
  }
}
