module api.ui.security.acl {

    import i18n = api.util.i18n;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
    import AppHelper = api.util.AppHelper;

    export class AccessSelector extends api.ui.tab.TabMenu {

        private value: Access;
        private valueChangedListeners: {(event: api.ValueChangedEvent): void}[] = [];

        constructor() {
            super('access-selector');

            accessOptions.forEach((option: AccessOption) => {
                let menuItem = (<TabMenuItemBuilder>new TabMenuItemBuilder().setLabel(option.name).setAddLabelTitleAttribute(
                    false)).build();
                this.addNavigationItem(menuItem);
            });

            this.initEventHandlers();
        }

        initEventHandlers() {
            this.onNavigationItemSelected((event: api.ui.NavigatorEvent) => {
                let item: api.ui.tab.TabMenuItem = <api.ui.tab.TabMenuItem> event.getItem();
                this.setValue(accessOptions[item.getIndex()].value);
            });

            this.getTabMenuButtonEl().onKeyDown((event: KeyboardEvent) => {
                if (!this.isEnabled()) {
                    return;
                }

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

        getValue(): Access {
            return this.value;
        }

        setValue(value: Access, silent?: boolean): AccessSelector {
            let option = accessOptions.filter((accessOption: AccessOption) => accessOption.value == value)[0];
            if (option) {
                this.selectNavigationItem(accessOptions.indexOf(option));
                if (!silent) {
                    this.notifyValueChanged(new api.ValueChangedEvent(Access[this.value], Access[value]));
                }
                this.value = value;
            }
            return this;
        }

        protected setButtonLabel(value: string): AccessSelector {
            this.getTabMenuButtonEl().setLabel(value, false);
            return this;
        }

        showMenu() {

            if (this.getSelectedNavigationItem().isVisibleInMenu()) {
                this.resetItemsVisibility();
                this.getSelectedNavigationItem().setVisibleInMenu(false);
            }

            let menu = this.getMenuEl();
            let entry = menu.getParentElement().getParentElement();
            let list = entry.getParentElement();
            let offset = entry.getEl().getOffsetTopRelativeToParent() -
                (list.getEl().getOffsetTopRelativeToParent() + list.getEl().getPaddingTop() + list.getEl().getScrollTop());
            let height = menu.getEl().getHeightWithoutPadding();

            if (offset > height) {
                menu.addClass('upward');
            } else {
                menu.removeClass('upward');
            }

            super.showMenu();

            this.focus();
        }

        onValueChanged(listener: (event: api.ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: api.ValueChangedEvent)=>void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyValueChanged(event: api.ValueChangedEvent) {
            this.valueChangedListeners.forEach((listener) => {
                listener(event);
            });
        }

        giveFocusToMenu(): boolean {
            const focused = super.giveFocusToMenu();
            return focused || (this.getSize() > 1 && this.focusNextTab());
        }

        isKeyNext(event: KeyboardEvent) {
            return KeyHelper.isArrowDownKey(event);
        }

        isKeyPrevious(event: KeyboardEvent) {
            return KeyHelper.isArrowUpKey(event);
        }

        returnFocusFromMenu(): boolean {
            return this.focus();
        }

        focus(): boolean {
            return this.getTabMenuButtonEl().focus();
        }
    }
}
