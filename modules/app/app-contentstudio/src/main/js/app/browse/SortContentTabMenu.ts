import '../../api.ts';
import {SortContentTabMenuItem} from './SortContentTabMenuItem';
import {SortContentTabMenuItems} from './SortContentTabMenuItems';

import ChildOrder = api.content.order.ChildOrder;
import ContentSummary = api.content.ContentSummary;
import DropdownHandle = api.ui.button.DropdownHandle;
import ArrayHelper = api.util.ArrayHelper;
import KeyHelper = api.ui.KeyHelper;
import AppHelper = api.util.AppHelper;

export class SortContentTabMenu extends api.ui.tab.TabMenu {

    private sortOrderChangedListeners: {():void}[] = [];

    private navigationItems: SortContentTabMenuItems;

    private dropdownHandle: DropdownHandle;

    constructor() {
        super('sort-tab-menu');

        this.navigationItems = new SortContentTabMenuItems();
        this.addNavigationItems(this.navigationItems.getAllItems());
        this.selectNavigationItem(0);

        this.dropdownHandle = new DropdownHandle();
        this.appendChild(this.dropdownHandle);
        this.dropdownHandle.up();

        this.initEventHandlers();
    }

    initEventHandlers() {
        this.dropdownHandle.onClicked(() => {
            if (this.isMenuVisible()) {
                this.hideMenu();
            } else {
                this.showMenu();
            }
        });

        this.dropdownHandle.onKeyDown((event: KeyboardEvent) => {

            if (KeyHelper.isArrowDownKey(event)) {
                if (this.isMenuVisible()) {
                    this.giveFocusToMenu();
                } else {
                    this.showMenu();
                }
                AppHelper.lockEvent(event);
            } else if (KeyHelper.isArrowUpKey(event)) {
                this.hideMenu();
                AppHelper.lockEvent(event);
            } else if (KeyHelper.isApplyKey(event)) {
                if (this.isMenuVisible()) {
                    this.hideMenu();
                } else {
                    this.showMenu();
                }
                AppHelper.lockEvent(event);
            } else if (KeyHelper.isEscKey(event)) {
                if (this.isMenuVisible()) {
                    this.hideMenu();
                    AppHelper.lockEvent(event);
                }
            }
        });
    }

    returnFocusFromMenu(): boolean {
        return this.focus();
    }

    isKeyNext(event: KeyboardEvent) {
        return KeyHelper.isArrowDownKey(event);
    }

    isKeyPrevious(event: KeyboardEvent) {
        return KeyHelper.isArrowUpKey(event);
    }

    protected hideMenu() {
        super.hideMenu();
        this.dropdownHandle.up();

    }

    protected showMenu() {
        super.showMenu();
        this.dropdownHandle.down();
        this.focus();
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
            return listener !== currentListener;
        });
    }

    private notifySortOrderChanged() {
        this.sortOrderChangedListeners.forEach((listener: () => void) => {
            listener.call(this);
        });
    }

    focus(): boolean {
        return this.dropdownHandle.giveFocus();
    }

}
