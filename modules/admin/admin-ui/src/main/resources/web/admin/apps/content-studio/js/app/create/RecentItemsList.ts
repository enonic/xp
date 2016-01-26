module app.create {

    export class RecentItemsList extends NewContentDialogList {

        constructor() {
            super("recent-content-types-list");
        }

        setItems(items: NewContentDialogListItem[]) {
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
            super.setItems(recentItems);
        }
    }
}