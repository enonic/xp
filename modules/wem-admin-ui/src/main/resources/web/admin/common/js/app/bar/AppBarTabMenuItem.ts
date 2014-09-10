module api.app.bar {

    export class AppBarTabMenuItem extends api.ui.tab.TabMenuItem {

        private tabId: AppBarTabId;

        private editing: boolean;

        constructor(builder : AppBarTabMenuItemBuilder) {
            super(new api.ui.tab.TabMenuItemBuilder().
                setLabel(builder.label).
                setOptions({removable: true}).
                setCloseAction(builder.closeAction));
            this.editing = builder.editing;
            this.tabId = builder.tabId;

            if (this.editing) {
                var iconEl = new api.dom.ImgEl();
                this.prependChild(iconEl);
            }
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
    export class AppBarTabMenuItemBuilder {
        label: string;
        tabId: AppBarTabId;
        editing: boolean;
        closeAction: api.ui.Action;

        setLabel(label: string) : AppBarTabMenuItemBuilder {
            this.label = label;
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
            this.closeAction = closeAction;
            return this;
        }

        build(): AppBarTabMenuItem{
            return new AppBarTabMenuItem(this);
        }

    }
}
