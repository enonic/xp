import "../../api.ts";
import {SortContentTabMenuItem} from "./SortContentTabMenuItem";
import {SortContentTabMenuItems} from "./SortContentTabMenuItems";

import ChildOrder = api.content.order.ChildOrder;
import ContentSummary = api.content.ContentSummary;
import DropdownHandle = api.ui.button.DropdownHandle;
import ArrayHelper = api.util.ArrayHelper;

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

    protected hideMenu() {
        super.hideMenu();
        this.dropdownHandle.up();

    }

    protected showMenu() {
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

    replaceNavigationItems(items: SortContentTabMenuItem[]) {
        this.removeNavigationItems();
        this.addNavigationItems(items);
    }

    selectNavigationItemByOrder(order: ChildOrder) {
        const items = this.navigationItems.getAllItems();

        items.some((item, index, array) => {
            if (item.getChildOrder().equals(order)) {
                // Move current order to the top of the list and select it
                const reordered = array.slice();
                ArrayHelper.moveElement(index, 0, reordered);
                this.replaceNavigationItems(reordered);
                this.selectNavigationItem(0);

                return true; // break
            }
            return false;
        });
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
