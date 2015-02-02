module api.app.bar {

    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;

    export class AppBarTabMenuItem extends api.ui.tab.TabMenuItem {

        private tabId: AppBarTabId;

        private editing: boolean;

        constructor(builder : AppBarTabMenuItemBuilder) {
            super(builder);

            this.addClass("appbar-tab-menu-item");

            this.editing = builder.editing;
            this.tabId = builder.tabId;
        }

        isEditing(): boolean {
            return this.editing;
        }

        getTabId(): AppBarTabId {
            return this.tabId;
        }

        setTabId(tabId: AppBarTabId) {
            this.tabId = tabId;
        }
    }

    export class AppBarTabMenuItemBuilder extends api.ui.tab.TabMenuItemBuilder {

        tabId: AppBarTabId;

        editing: boolean;

        constructor() {
            super();
            this.closeButtonEnabled = true;
        }

        setLabel(label: string) : AppBarTabMenuItemBuilder {
            super.setLabel(label);
            return this;
        }

        setTabId(tabId: AppBarTabId) : AppBarTabMenuItemBuilder {
            this.tabId = tabId;
            return this;
        }

        setEditing(editing: boolean) : AppBarTabMenuItemBuilder {
            this.editing = editing;
            return this;
        }

        setCloseAction(closeAction: api.ui.Action) : AppBarTabMenuItemBuilder {
            super.setCloseAction(closeAction);
            return this;
        }

        setMarkUnnamed(markUnnamed: boolean): AppBarTabMenuItemBuilder {
            super.setMarkUnnamed(markUnnamed);
            return this;
        }

        setMarkInvalid(markInvalid: boolean): AppBarTabMenuItemBuilder {
            super.setMarkInvalid(markInvalid);
            return this;
        }

        build(): AppBarTabMenuItem {
            return new AppBarTabMenuItem(this);
        }

    }
}
