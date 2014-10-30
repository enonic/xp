module app.view {

    export class PrincipalItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.security.Principal> {

        constructor() {
            super("principal-item-statistics-panel");

        }

        setItem(item: api.app.view.ViewItem<api.security.Principal>) {
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
