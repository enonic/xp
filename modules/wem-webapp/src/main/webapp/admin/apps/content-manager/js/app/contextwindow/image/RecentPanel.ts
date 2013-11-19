module app_contextwindow_image {
    export class RecentPanel extends api_ui.Panel {

        private recentGrid:RecentGrid;

        private dataView:api_ui_grid.DataView<api_content.ContentSummary>;

        constructor() {
            super("RecentPanel");
            this.addClass("recent-panel");
            this.dataView = new api_ui_grid.DataView<api_content.ContentSummary>();
            this.recentGrid = new RecentGrid(this.dataView);

            var contentSummaryLoader = new api_form_inputtype_content.ContentSummaryLoader();
            contentSummaryLoader.setAllowedContentTypes(["image"]);
            contentSummaryLoader.addListener({
                onLoading: () => {
                },
                onLoaded: (contentSummaries:api_content.ContentSummary[]) => {
                    this.dataView.setItems(contentSummaries);
                }
            });
            contentSummaryLoader.search("");

            var title = new api_dom.H3El();
            title.getEl().setInnerHtml("Recent...");

            this.appendChild(title);
            this.appendChild(this.recentGrid);
        }

        getGrid():RecentGrid {
            return this.recentGrid;
        }

        getDataView():api_ui_grid.DataView<api_content.ContentSummary> {
            return this.dataView;
        }
    }
}