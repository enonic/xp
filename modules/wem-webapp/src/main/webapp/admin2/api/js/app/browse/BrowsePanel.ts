module api_app_browse{

    export class AppBrowsePanel extends api_ui.Panel {

        private browseToolbar:api_ui_toolbar.Toolbar;

        private grid:api_ui_grid.TreeGridPanel;

        private browseItemPanel:BrowseItemPanel;

        private gridAndDetailSplitPanel:api_ui.SplitPanel;

        private filterPanel:any;

        private gridContainer:api_ui.Panel;


        constructor(browseToolbar:api_ui_toolbar.Toolbar, grid:api_ui_grid.TreeGridPanel, browseItemPanel:BrowseItemPanel, filterPanel:any) {
            super("BrowsePanel");

            this.browseToolbar = browseToolbar;
            this.grid = grid;
            this.browseItemPanel = browseItemPanel;
            this.filterPanel = filterPanel;
            this.gridContainer = new api_ui.Panel("grid-container");
            this.gridAndDetailSplitPanel = new api_ui.SplitPanel(this.gridContainer, this.browseItemPanel);
        }

        init() {
            this.appendChild(this.browseToolbar);
            this.appendChild(this.gridAndDetailSplitPanel);
            this.gridAndDetailSplitPanel.render();
            // TODO: filterPanel.renderTo(this);
            this.grid.create('center', this.gridContainer.getId());
        }
    }
}
