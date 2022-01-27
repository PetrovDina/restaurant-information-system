import { Component, OnInit } from '@angular/core';
import { Item } from 'src/app/model/Item';
import { Category } from 'src/app/model/Category';
import { ItemService } from 'src/app/services/item/item.service';
import { CategoryService } from 'src/app/services/category/category.service';

@Component({
  selector: 'app-guest-page',
  templateUrl: './guest-page.component.html',
  styleUrls: ['./guest-page.component.less'],
})
export class GuestPageComponent implements OnInit {
    public items: Item[] = [];
    public categories: Category[] = [];
    constructor(private itemService: ItemService, private categoryService: CategoryService,) { }

    fetchData = () => {
        this.itemService.getItems().subscribe((res) => {
            this.items = res;
        });
    }

    ngOnInit(): void {
        this.itemService.getItems().subscribe((res) => {
            this.items = res;
        })
        this.categoryService.getCategories().subscribe((res) => {
            this.categories = res;
            console.log(res);
        })
    }
}