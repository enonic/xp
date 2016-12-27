import "../../api.ts";
import {NewContentDialogListItem} from "./NewContentDialogListItem";
import {RecentItems} from "./RecentItems";
import {NewContentDialogList} from "./NewContentDialogList";

export class RecentItemsList extends NewContentDialogList {

    constructor() {
        super("recent-content-types-list");
    }

    createItems(items: NewContentDialogListItem[]) {
        var itemsByName: {[name: string]: NewContentDialogListItem} = {};
        items.forEach((item: NewContentDialogListItem) => {
            itemsByName[item.getName()] = item;
        });

        var recentItemsNames = RecentItems.get().getRecentItemsNames();
        var recentItems: NewContentDialogListItem[] = [];
        recentItemsNames.forEach((name: string) => {
            if (itemsByName[name]) {
                recentItems.push(itemsByName[name]);
            }
        });

        this.setItems(recentItems);
    }
}
