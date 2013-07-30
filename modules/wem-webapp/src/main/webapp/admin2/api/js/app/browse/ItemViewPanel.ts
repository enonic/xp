module api_app_browse {

    export class ItemViewPanel extends api_ui.Panel {

        private toolbar:api_ui_toolbar.Toolbar;

        private statisticsPanel:ItemStatisticsPanel;

        private browseItem:BrowseItem;

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

        setItem(item:BrowseItem) {
            this.browseItem = item;
            this.statisticsPanel.setItem(item);
        }

        getItem():BrowseItem {
            return this.browseItem;
        }

    }

}