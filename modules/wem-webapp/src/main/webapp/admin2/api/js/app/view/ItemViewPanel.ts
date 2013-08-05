module api_app_view {

    export class ItemViewPanel extends api_ui.Panel {

        private toolbar:api_ui_toolbar.Toolbar;

        private statisticsPanel:ItemStatisticsPanel;

        private browseItem:api_app_browse.BrowseItem;

        constructor(toolbar:api_ui_toolbar.Toolbar, statisticsPanel:ItemStatisticsPanel) {
            super("ItemViewPanel");
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

        setItem(item:api_app_browse.BrowseItem) {
            this.browseItem = item;
            this.statisticsPanel.setItem(item);
        }

        getItem():api_app_browse.BrowseItem {
            return this.browseItem;
        }

    }

}