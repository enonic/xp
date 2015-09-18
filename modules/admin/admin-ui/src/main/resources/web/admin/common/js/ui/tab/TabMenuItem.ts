module api.ui.tab {

    export class TabMenuItem extends TabItem {

        private visibleInMenu: boolean = true;

        constructor(builder: TabMenuItemBuilder) {
            super(builder.setCloseButtonEnabled(true), "tab-menu-item");
        }


        isVisibleInMenu(): boolean {
            return this.visibleInMenu
        }

        setVisibleInMenu(value: boolean) {
            this.visibleInMenu = value;
            super.setVisible(value);
        }

    }

    export class TabMenuItemBuilder extends TabItemBuilder {

        build(): TabMenuItem {
            return new TabMenuItem(this);
        }

    }
}
