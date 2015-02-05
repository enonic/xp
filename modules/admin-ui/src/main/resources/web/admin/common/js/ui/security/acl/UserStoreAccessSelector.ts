module api.ui.security.acl {

    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
    import UserStoreAccess = api.security.acl.UserStoreAccess;

    interface UserStoreAccessSelectorOption {
        value: UserStoreAccess;
        name: string;
    }

    export class UserStoreAccessSelector extends api.ui.tab.TabMenu {

        private static OPTIONS: UserStoreAccessSelectorOption[] = [
            {value: UserStoreAccess.READ, name: 'Read'},
            {value: UserStoreAccess.CREATE_USERS, name: 'Create Users'},
            {value: UserStoreAccess.WRITE_USERS, name: 'Write Users'},
            {value: UserStoreAccess.USER_STORE_MANAGER, name: 'User Store Manager'},
            {value: UserStoreAccess.ADMINISTRATOR, name: 'Administrator'}
        ];

        private value: UserStoreAccess;
        private valueChangedListeners: {(event: api.ui.ValueChangedEvent):void}[] = [];

        constructor() {
            super("access-selector");

            UserStoreAccessSelector.OPTIONS.forEach((option: UserStoreAccessSelectorOption, index: number) => {
                var menuItem = (<TabMenuItemBuilder>new TabMenuItemBuilder().setLabel(option.name)).build();
                this.addNavigationItem(menuItem);
            });

            this.onNavigationItemSelected((event: api.ui.NavigatorEvent) => {
                var item: api.ui.tab.TabMenuItem = <api.ui.tab.TabMenuItem> event.getItem();
                this.setValue(UserStoreAccessSelector.OPTIONS[item.getIndex()].value);
            });

        }

        getValue(): UserStoreAccess {
            return this.value
        }

        setValue(value: UserStoreAccess, silent?: boolean): UserStoreAccessSelector {
            var option = this.findOptionByValue(value);
            if (option) {
                this.selectNavigationItem(UserStoreAccessSelector.OPTIONS.indexOf(option));
                if (!silent) {
                    this.notifyValueChanged(new api.ui.ValueChangedEvent(UserStoreAccess[this.value], UserStoreAccess[value]));
                }
                this.value = value;
            }
            return this;
        }

        private findOptionByValue(value: UserStoreAccess): UserStoreAccessSelectorOption {
            for (var i = 0; i < UserStoreAccessSelector.OPTIONS.length; i++) {
                var option = UserStoreAccessSelector.OPTIONS[i];
                if (option.value == value) {
                    return option;
                }
            }
            return undefined;
        }

        onValueChanged(listener: (event: api.ui.ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: api.ui.ValueChangedEvent)=>void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyValueChanged(event: api.ui.ValueChangedEvent) {
            this.valueChangedListeners.forEach((listener) => {
                listener(event);
            })
        }

    }

}