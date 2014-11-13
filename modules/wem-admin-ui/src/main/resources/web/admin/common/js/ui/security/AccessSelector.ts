module api.ui.security {

    export enum Access {
        READ,
        WRITE,
        PUBLISH,
        FULL,
        CUSTOM
    }

    interface AccessSelectorOption {
        value: Access;
        name: string;
    }

    export class AccessSelector extends api.ui.tab.TabMenu {

        private static OPTIONS: AccessSelectorOption[] = [
            {value: Access.READ, name: 'Can Read'},
            {value: Access.WRITE, name: 'Can Write'},
            {value: Access.PUBLISH, name: 'Can Publish'},
            {value: Access.FULL, name: 'Full Access'},
            {value: Access.CUSTOM, name: 'Custom...'}
        ];

        private value: Access;
        private valueChangedListeners: {(event: api.ui.ValueChangedEvent):void}[] = [];

        constructor() {
            super("access-selector");

            AccessSelector.OPTIONS.forEach((option: AccessSelectorOption, index: number) => {
                var menuItem = new api.ui.tab.TabMenuItemBuilder().setLabel(option.name).build();
                this.addNavigationItem(menuItem);
            });

            this.onNavigationItemSelected((event: NavigatorEvent) => {
                var item: api.ui.tab.TabMenuItem = <api.ui.tab.TabMenuItem> event.getItem();
                this.setValue(AccessSelector.OPTIONS[item.getIndex()].value);
            })
        }

        getValue(): Access {
            return this.value
        }

        setValue(value: Access) {
            var option = this.findOptionByValue(value);
            if (option) {
                this.selectNavigationItem(AccessSelector.OPTIONS.indexOf(option));
                this.notifyValueChanged(new api.ui.ValueChangedEvent(Access[this.value], Access[value]));
                this.value = value;
            }
        }

        private findOptionByValue(value: Access): AccessSelectorOption {
            for (var i = 0; i < AccessSelector.OPTIONS.length; i++) {
                var option = AccessSelector.OPTIONS[i];
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