module api_app_browse{

    export interface BrowsePanelParams {

        browseToolbar:api_ui_toolbar.Toolbar;

        treeGridPanel:api_app_browse_grid.TreeGridPanel;

        browseItemPanel:BrowseItemPanel;

        filterPanel:api_app_browse_filter.BrowseFilterPanel;
    }

    export class BrowsePanel extends api_ui.Panel {

        private browseToolbar:api_ui_toolbar.Toolbar;

        private treeGridPanel:api_app_browse_grid.TreeGridPanel;

        private browseItemPanel:BrowseItemPanel;

        private gridAndDetailSplitPanel:api_ui.SplitPanel;

        private filterPanel:api_app_browse_filter.BrowseFilterPanel;

        private gridContainer:api_app_browse.GridContainer;

        private gridAndFilterAndDetailSplitPanel;

        private gridAndToolbarContainer:api_ui.Panel;

        constructor(params:BrowsePanelParams) {
            super("BrowsePanel");

            this.browseToolbar = params.browseToolbar;
            this.treeGridPanel = params.treeGridPanel;
            this.browseItemPanel = params.browseItemPanel;
            this.filterPanel = params.filterPanel;

            this.browseItemPanel.addListener({
                onDeselected: (item:BrowseItem) => {
                    this.treeGridPanel.deselect(item);
                }
            });

            this.gridContainer = new api_app_browse.GridContainer(this.treeGridPanel);

            this.gridAndToolbarContainer = new api_ui.Panel("GridAndToolbar");
            this.gridAndToolbarContainer.appendChild(this.browseToolbar);
            this.gridAndToolbarContainer.appendChild(this.gridContainer);

            this.gridAndDetailSplitPanel = new api_ui.SplitPanel(this.gridAndToolbarContainer, this.browseItemPanel);
            this.gridAndFilterAndDetailSplitPanel = new api_ui.SplitPanel(this.filterPanel, this.gridAndDetailSplitPanel);
            this.gridAndFilterAndDetailSplitPanel.setDistribution(15, 85);
            this.gridAndFilterAndDetailSplitPanel.setAlignment(api_ui.SplitPanelAlignment.VERTICAL);

            this.treeGridPanel.addListener(<api_app_browse_grid.TreeGridPanelListener>{
                onSelectionChanged: (event:api_app_browse_grid.TreeGridSelectionChangedEvent) => {

                    var models:api_model.ContentExtModel[] = <any[]>event.selectedModels;
                    var browseItems:api_app_browse.BrowseItem[] = this.extModelsToBrowseItems(models);
                    this.browseItemPanel.setItems(browseItems);
                }
            });
        }

        extModelsToBrowseItems(models:api_model.ExtModel[]):BrowseItem[] {
            throw Error("To be implemented by inheritor");
        }

        refreshGrid() {
            if (this.treeGridPanel.isRefreshNeeded()) {
                this.treeGridPanel.refresh();
            }
        }

        afterRender() {
            this.appendChild(this.gridAndFilterAndDetailSplitPanel);
            this.gridAndFilterAndDetailSplitPanel.render();
            this.gridAndDetailSplitPanel.render();
        }
    }
}
