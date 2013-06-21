module api_browse{

    export class AppBrowsePanel extends api_ui.Panel {

        private browseToolbar:api_ui_toolbar.Toolbar;

        private grid:api_ui_grid.TreeGridPanel;

        private detailPanel:DetailPanel;

        private filterPanel:any;

        constructor(browseToolbar:api_ui_toolbar.Toolbar, grid:api_ui_grid.TreeGridPanel, detailPanel:DetailPanel, filterPanel:any) {
            super("AppBrowsePanel");

            this.browseToolbar = browseToolbar;
            this.grid = grid;
            this.detailPanel = detailPanel;
            this.filterPanel = filterPanel;
        }

        init() {
            this.appendChild(this.browseToolbar);
            // TODO: filterPanel.renderTo(this);
            this.grid.create('center', this.getId());
            this.appendChild(this.detailPanel);
        }
    }
}
