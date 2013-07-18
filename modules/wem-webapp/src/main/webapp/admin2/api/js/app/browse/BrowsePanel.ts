module api_app_browse{

    export class BrowsePanel extends api_ui.Panel {

        private browseToolbar:api_ui_toolbar.Toolbar;

        private grid:api_ui_grid.TreeGridPanel;

        private browseItemPanel:BrowseItemPanel;

        private gridAndDetailSplitPanel:api_ui.SplitPanel;

        private filterPanel:any;

        private gridContainer:api_ui.Panel;


        constructor(browseToolbar:api_ui_toolbar.Toolbar, grid:api_ui_grid.TreeGridPanel, browseItemPanel:BrowseItemPanel,
                    filterPanel:any) {
            super("BrowsePanel");

            this.browseToolbar = browseToolbar;
            this.browseToolbar.getEl().setMarginLeft('197px');
            this.grid = grid;
            this.browseItemPanel = browseItemPanel;
            this.filterPanel = filterPanel;
            this.gridContainer = new api_ui.Panel("grid-container");
            this.gridAndDetailSplitPanel = new api_ui.SplitPanel(this.gridContainer, this.browseItemPanel);
            this.gridAndDetailSplitPanel.getEl().setLeft('197px');
        }

        afterRender() {
            console.log("afterrender browsepanel");
            this.appendChild(this.browseToolbar);
            this.appendChild(this.gridAndDetailSplitPanel);
            this.appendChild(this.filterPanel);
            this.gridAndDetailSplitPanel.render();
            this.grid.create('center', this.gridContainer.getId());
        }
    }
}
