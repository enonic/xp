module api_app_browse{

    export class BrowsePanel extends api_ui.Panel {

        private browseToolbar:api_ui_toolbar.Toolbar;

        private grid:api_ui_grid.TreeGridPanel;

        private browseItemPanel:BrowseItemPanel;

        private gridAndDetailSplitPanel:api_ui.SplitPanel;

        private filterPanel:api_app_browse_filter.BrowseFilterPanel;

        private gridContainer:api_app_browse.GridContainer;

        private gridAndFilterAndDetailSplitPanel;

        private gridAndToolbarContainer:api_ui.Panel;


        constructor(browseToolbar:api_ui_toolbar.Toolbar, grid:api_ui_grid.TreeGridPanel, browseItemPanel:BrowseItemPanel,
                    filterPanel:api_app_browse_filter.BrowseFilterPanel) {
            super("BrowsePanel");

            this.browseToolbar = browseToolbar;
            this.grid = grid;
            this.browseItemPanel = browseItemPanel;
            this.filterPanel = filterPanel;

            this.browseItemPanel.addDeselectionListener((item:BrowseItem) => {
                this.grid.deselect(item);
            });

            this.gridAndToolbarContainer = new api_ui.Panel();
            this.gridContainer = new api_app_browse.GridContainer(this.grid);

            this.gridAndToolbarContainer.appendChild(this.browseToolbar);
            this.gridAndToolbarContainer.appendChild(this.gridContainer);

            this.gridAndDetailSplitPanel = new api_ui.SplitPanel(this.gridAndToolbarContainer, this.browseItemPanel);
            this.gridAndFilterAndDetailSplitPanel = new api_ui.SplitPanel(this.filterPanel, this.gridAndDetailSplitPanel);
            this.gridAndFilterAndDetailSplitPanel.setDistribution(15, 85);
            this.gridAndFilterAndDetailSplitPanel.setAlignment(api_ui.SplitPanelAlignment.VERTICAL);

        }

        refreshGrid() {
            if (this.grid.isRefreshNeeded()) {
                this.grid.refresh();
            }
        }

        afterRender() {
            this.appendChild(this.gridAndFilterAndDetailSplitPanel);
            this.gridAndFilterAndDetailSplitPanel.render();
            this.gridAndDetailSplitPanel.render();
        }
    }
}
