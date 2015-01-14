module api.ui.security.acl {

    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;

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
                var menuItem = (<TabMenuItemBuilder>new TabMenuItemBuilder().setLabel(option.name)).build();
                this.addNavigationItem(menuItem);
            });

            this.onNavigationItemSelected((event: api.ui.NavigatorEvent) => {
                var item: api.ui.tab.TabMenuItem = <api.ui.tab.TabMenuItem> event.getItem();
                this.setValue(AccessSelector.OPTIONS[item.getIndex()].value);
            })
        }

        getValue(): Access {
            return this.value
        }

        setValue(value: Access, silent?: boolean): AccessSelector {
            var option = this.findOptionByValue(value);
            if (option) {
                this.selectNavigationItem(AccessSelector.OPTIONS.indexOf(option));
                if (!silent) {
                    this.notifyValueChanged(new api.ui.ValueChangedEvent(Access[this.value], Access[value]));
                }
                this.value = value;
            }
            return this;
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

        showMenu() {

            var menu = this.getMenuEl(),
                button = this.getTabMenuButtonEl(),
                entry = menu.getParentElement().getParentElement(),
                list = entry.getParentElement(),
                offset = entry.getEl().getOffsetTopRelativeToParent() -
                    (list.getEl().getOffsetTopRelativeToParent() + list.getEl().getPaddingTop() + list.getEl().getScrollTop()),
                height = menu.getEl().getHeightWithoutPadding() - button.getEl().getHeight() + 2; // 2 is a valid deviation


            if (offset > height) {
                menu.addClass("upward");
            } else {
                menu.removeClass("upward");
            }

            super.showMenu();
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