module api_app_view {

    export class ItemViewPanel extends api_ui.Panel {

        private toolbar:api_ui_toolbar.Toolbar;

        private panel:api_ui.Panel;

        private browseItem:ViewItem;

        constructor(toolbar:api_ui_toolbar.Toolbar, panel:api_ui.Panel) {
            super("ItemViewPanel");
            this.getEl().addClass("item-view-panel");
            this.toolbar = toolbar;
            this.panel = panel;
            this.appendChild(this.toolbar);
            this.appendChild(this.panel)
        }

        afterRender() {
            super.afterRender();
            this.panel.afterRender();
        }

        setItem(item:ViewItem) {
            this.browseItem = item;
        }

        getItem():ViewItem {
            return this.browseItem;
        }

    }

}