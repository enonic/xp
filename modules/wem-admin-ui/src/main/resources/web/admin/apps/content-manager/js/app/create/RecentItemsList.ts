module app.create {

    export class RecentItemsList extends NewContentDialogList {

        constructor() {
            super();

            var title = new api.dom.H4El();
            title.setText("Recently used");
            this.prependChild(title);
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