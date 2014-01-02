module app.contextwindow.image {
    export class RecentPanel extends api.ui.Panel {

        private recentGrid:RecentGrid;

        private dataView:api.ui.grid.DataView<api.content.ContentSummary>;

        constructor() {
            super("RecentPanel");
            this.addClass("recent-panel");
            this.dataView = new api.ui.grid.DataView<api.content.ContentSummary>();
            this.recentGrid = new RecentGrid(this.dataView);

            var contentSummaryLoader = new api.form.inputtype.content.ContentSummaryLoader();
            contentSummaryLoader.setCount(7);
            contentSummaryLoader.setAllowedContentTypes(["image"]);
            contentSummaryLoader.addListener({
                onLoading: () => {
                },
                onLoaded: (contentSummaries:api.content.ContentSummary[]) => {
                    this.dataView.setItems(contentSummaries);
                }
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