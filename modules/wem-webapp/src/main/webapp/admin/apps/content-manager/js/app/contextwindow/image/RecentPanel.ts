module app.contextwindow.image {
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;
    export class RecentPanel extends api.ui.Panel {

        private recentGrid:RecentGrid;

        private dataView:api.ui.grid.DataView<api.content.ContentSummary>;

        constructor() {
            super("recent-panel");
            this.dataView = new api.ui.grid.DataView<api.content.ContentSummary>();
            this.recentGrid = new RecentGrid(this.dataView);

            var contentSummaryLoader = new api.form.inputtype.content.ContentSummaryLoader();
            contentSummaryLoader.setSize(7);
            contentSummaryLoader.setAllowedContentTypes(["image"]);
            contentSummaryLoader.onLoadingData((event:LoadingDataEvent) => {
                });
            contentSummaryLoader.onLoadedData((event:LoadedDataEvent<api.content.ContentSummary>) => {
                    this.dataView.setItems(event.getData());
                });
            contentSummaryLoader.search("");

            var title = new api.dom.H3El();
            title.getEl().setInnerHtml("Recent...");

            this.appendChild(title);
            this.appendChild(this.recentGrid);
        }

        getGrid():RecentGrid {
            return this.recentGrid;
        }

        getDataView():api.ui.grid.DataView<api.content.ContentSummary> {
            return this.dataView;
        }
    }
}