import '../../api.ts';
import {NewContentDialogListItem} from './NewContentDialogListItem';
import {RecentItems} from './RecentItems';
import {NewContentDialogList} from './NewContentDialogList';

export class RecentItemsList extends NewContentDialogList {

    constructor() {
        super('recent-content-types-list');
    }

    createItems(items: NewContentDialogListItem[]) {
        let itemsByName: {[name: string]: NewContentDialogListItem} = {};
        items.forEach((item: NewContentDialogListItem) => {
            itemsByName[item.getName()] = item;
        });

        let recentItemsNames = RecentItems.get().getRecentItemsNames();
        let recentItems: NewContentDialogListItem[] = [];
        recentItemsNames.forEach((name: string) => {
            if (itemsByName[name]) {
                recentItems.push(itemsByName[name]);
            }
        });

        this.setItems(recentItems);
    }
}
