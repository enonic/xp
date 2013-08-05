module api_app_browse{

    export class BrowsePanel extends api_ui.Panel {

        private browseToolbar:api_ui_toolbar.Toolbar;

        private grid:api_ui_grid.TreeGridPanel;

        private browseItemPanel:BrowseItemPanel;

        private gridAndDetailSplitPanel:api_ui.SplitPanel;

        private filterPanel:any;

        private gridContainer:api_ui.Panel;

        private gridAndFilterAndDetailSplitPanel;


        constructor(browseToolbar:api_ui_toolbar.Toolbar, grid:api_ui_grid.TreeGridPanel, browseItemPanel:BrowseItemPanel,
                    filterPanel:any) {
            super("BrowsePanel");

            this.browseToolbar = browseToolbar;
            this.grid = grid;
            this.browseItemPanel = browseItemPanel;
            this.filterPanel = filterPanel;
            this.gridContainer = new api_ui.Panel("grid-container");
            this.gridAndDetailSplitPanel = new api_ui.SplitPanel(this.gridContainer, this.browseItemPanel);
            this.gridAndFilterAndDetailSplitPanel = new api_ui.SplitPanel(this.filterPanel, this.gridAndDetailSplitPanel);
            this.gridAndFilterAndDetailSplitPanel.setDistribution(30, 70);
            this.gridAndFilterAndDetailSplitPanel.setAlignment(api_ui.SplitPanelAlignment.VERTICAL);

        }

        afterRender() {
            this.appendChild(this.browseToolbar);
            this.appendChild(this.gridAndFilterAndDetailSplitPanel);
            this.gridAndFilterAndDetailSplitPanel.render();
            this.gridAndDetailSplitPanel.render();
            this.grid.create('center', this.gridContainer.getId());
        }
    }
}
