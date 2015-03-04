module app.browse {

    import ChildOrder = api.content.ChildOrder;
    import FieldOrderExpr = api.content.FieldOrderExpr;
    import FieldOrderExprBuilder = api.content.FieldOrderExprBuilder;
    import ContentSummary = api.content.ContentSummary;
    import DropdownHandle = api.ui.selector.DropdownHandle;

    export class SortContentTabMenu extends api.ui.tab.TabMenu {

        private sortOrderChangedListeners: {():void}[] = [];

        private navigationItems: SortContentTabMenuItems;

        private dropdownHandle: DropdownHandle;

        constructor() {
            super("sort-tab-menu");

            this.navigationItems = new SortContentTabMenuItems();
            this.addNavigationItems(this.navigationItems.getAllItems());
            this.selectNavigationItem(0);

            this.dropdownHandle = new DropdownHandle();
            this.appendChild(this.dropdownHandle);
            this.dropdownHandle.up();
            this.dropdownHandle.onClicked((event: any) => {
                if (this.isMenuVisible()) {
                    this.hideMenu();
                } else {
                    this.showMenu();
                }
            });

        }

        hideMenu() {
            super.hideMenu();
            this.dropdownHandle.up();

        }

        showMenu() {
            super.showMenu();
            this.dropdownHandle.down();
        }

        selectNavigationItem(tabIndex: number) {
            super.selectNavigationItem(tabIndex);
            this.notifySortOrderChanged();
        }

        getSelectedNavigationItem(): SortContentTabMenuItem {
            return (<SortContentTabMenuItem>super.getSelectedNavigationItem());
        }

        getSortMenuNavigationItems(): SortContentTabMenuItems {
            return this.navigationItems;
        }

        addNavigationItems(items: SortContentTabMenuItem[]) {
            if (items) {
                items.forEach((item: SortContentTabMenuItem) => {
                    this.addNavigationItem(item);
                });
            }
        }

        selectNavigationItemByOrder(order: ChildOrder) {
            var items = this.navigationItems.getAllItems();
            for (var key in items) {
                if (items[key].getChildOrder().equals(order)) {
                    var item = items.splice(key, 1)[0];
                    this.removeNavigationItems();
                    this.addNavigationItem(item);
                    this.addNavigationItems(items);
                    this.selectNavigationItem(0);
                    break;
                }
            }
        }

        selectManualSortingItem() {
            this.selectNavigationItemByOrder(this.navigationItems.SORT_MANUAL_ITEM.getChildOrder());
        }


        onSortOrderChanged(listener: () => void) {
            this.sortOrderChangedListeners.push(listener);
        }

        unSortOrderChanged(listener: () => void) {
            this.sortOrderChangedListeners = this.sortOrderChangedListeners.filter((currentListener: () => void) => {
                return listener != currentListener;
            });
        }

        private notifySortOrderChanged() {
            this.sortOrderChangedListeners.forEach((listener: () => void) => {
                listener.call(this);
            });
        }

    }
}
