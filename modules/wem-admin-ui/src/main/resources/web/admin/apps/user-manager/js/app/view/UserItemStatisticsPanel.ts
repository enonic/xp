module app.view {

    export class UserItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<app.browse.UserTreeGridItem> {

        constructor() {
            super("principal-item-statistics-panel");

        }

        setItem(item: api.app.view.ViewItem<app.browse.UserTreeGridItem>) {
            var currentItem = this.getItem();
            if (currentItem && currentItem.equals(item)) {
                // do nothing in case item has not changed
                return;
            }

            super.setItem(item);
            var currentModule = item.getModel();

        }

    }
}
