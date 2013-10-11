module api_app_browse{

    export interface BrowsePanelParams {

        browseToolbar:api_ui_toolbar.Toolbar;

        treeGridPanel:api_app_browse_grid.TreeGridPanel;

        browseItemPanel:BrowseItemPanel;

        filterPanel:api_app_browse_filter.BrowseFilterPanel;
    }

    export class BrowsePanel extends api_ui.Panel implements api_ui.ActionContainer {

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
                },
                onPanelShown: (item) => {
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

            this.treeGridPanel.addListener({
                onSelectionChanged: null,
                onSelect: (event:api_app_browse_grid.TreeGridSelectEvent) => {

                    var browseItems:api_app_browse.BrowseItem[] = this.extModelsToBrowseItems([event.selectedModel]);
                    this.browseItemPanel.addItem(browseItems[0]);
                },
                onDeselect: (event:api_app_browse_grid.TreeGridDeselectEvent) => {

                    var browseItems:api_app_browse.BrowseItem[] = this.extModelsToBrowseItems([event.deselectedModel]);
                    this.browseItemPanel.removeItem(browseItems[0]);
                },
                onItemDoubleClicked: null
            });
        }

        getActions():api_ui.Action[] {
            return this.browseToolbar.getActions();
        }

        extModelsToBrowseItems(models:Ext_data_Model[]):BrowseItem[] {
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
