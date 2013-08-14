module api_app_view {

    export class ItemViewPanel extends api_ui.Panel {

        private toolbar:api_ui_toolbar.Toolbar;

        private statisticsPanel:ItemStatisticsPanel;

        private browseItem:ViewItem;

        constructor(toolbar:api_ui_toolbar.Toolbar, statisticsPanel:ItemStatisticsPanel) {
            super("ItemViewPanel");
            this.getEl().addClass("item-view-panel");
            this.toolbar = toolbar;
            this.statisticsPanel = statisticsPanel;
            this.appendChild(this.toolbar);
            this.appendChild(this.statisticsPanel);
        }

        afterRender() {
            console.log("afterRender viewPanel");
            super.afterRender();
            this.statisticsPanel.afterRender();
        }

        setItem(item:ViewItem) {
            this.browseItem = item;
            this.statisticsPanel.setItem(item);
        }

        getItem():ViewItem {
            return this.browseItem;
        }

    }

}