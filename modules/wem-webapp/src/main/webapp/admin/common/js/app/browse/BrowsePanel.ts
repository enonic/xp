module api.app.browse {

    export interface BrowsePanelParams<M> {

        browseToolbar:api.ui.toolbar.Toolbar;

        treeGridPanel:api.app.browse.grid.TreeGridPanel;

        treeGridPanel2?:api.app.browse.grid2.GridPanel2;

        browseItemPanel:BrowseItemPanel<M>;

        filterPanel?:api.app.browse.filter.BrowseFilterPanel;
    }

    export class BrowsePanel<M> extends api.ui.Panel implements api.ui.ActionContainer {

        private browseToolbar: api.ui.toolbar.Toolbar;

        private treeGridPanel: api.app.browse.grid.TreeGridPanel;

        private gridPanel2: api.app.browse.grid2.GridPanel2;

        private treeSwapperDeckPanel: api.ui.DeckPanel;

        private browseItemPanel: BrowseItemPanel<M>;

        private gridAndDetailSplitPanel: api.ui.SplitPanel;

        private filterPanel: api.app.browse.filter.BrowseFilterPanel;

        private gridContainer: api.app.browse.GridContainer;

        private gridAndFilterAndDetailSplitPanel;

        private gridAndToolbarContainer: api.ui.Panel;

        private refreshNeeded: boolean = false;

        constructor(params: BrowsePanelParams<M>) {
            super();

            this.browseToolbar = params.browseToolbar;
            this.treeGridPanel = params.treeGridPanel;
            this.gridPanel2 = params.treeGridPanel2;
            this.browseItemPanel = params.browseItemPanel;
            this.filterPanel = params.filterPanel;

            this.browseItemPanel.onDeselected((event: ItemDeselectedEvent<M>) => {
                this.treeGridPanel.deselect(event.getBrowseItem().getPath());
            });

            this.gridContainer = new api.app.browse.GridContainer(this.treeGridPanel);

            this.gridAndToolbarContainer = new api.ui.Panel();
            this.gridAndToolbarContainer.appendChild(this.browseToolbar);
            this.gridAndToolbarContainer.appendChild(this.gridContainer);

            // this.gridAndDetailSplitPanel = new api.ui.SplitPanel(this.gridAndToolbarContainer, this.browseItemPanel);
            if (this.gridPanel2 != null) {
                this.treeSwapperDeckPanel = new api.ui.DeckPanel();
                this.treeSwapperDeckPanel.addPanel(this.browseItemPanel);
                this.treeSwapperDeckPanel.addPanel(this.gridPanel2);
                this.treeSwapperDeckPanel.showPanel(0);


                this.gridAndDetailSplitPanel = new api.ui.SplitPanel(this.gridAndToolbarContainer, this.treeSwapperDeckPanel);
            }
            else {
                this.gridAndDetailSplitPanel = new api.ui.SplitPanel(this.gridAndToolbarContainer, this.browseItemPanel);
            }

            if (this.filterPanel) {
                this.gridAndFilterAndDetailSplitPanel = new api.ui.SplitPanel(this.filterPanel, this.gridAndDetailSplitPanel);
                this.gridAndFilterAndDetailSplitPanel.setDistribution(15, 85);
                this.gridAndFilterAndDetailSplitPanel.setAlignment(api.ui.SplitPanelAlignment.VERTICAL);
            } else {
                this.gridAndFilterAndDetailSplitPanel = this.gridAndDetailSplitPanel;
            }

            this.treeGridPanel.onTreeGridSelectionChanged((event: api.app.browse.grid.TreeGridSelectionChangedEvent) => {
                var browseItems: api.app.browse.BrowseItem<M>[] = this.extModelsToBrowseItems(event.getSelectedModels());
                this.browseItemPanel.setItems(browseItems);
            });

            this.onRendered((event) => {
                this.appendChild(this.gridAndFilterAndDetailSplitPanel);
            });
        }

        getActions(): api.ui.Action[] {
            return this.browseToolbar.getActions();
        }

        extModelsToBrowseItems(models: Ext_data_Model[]): BrowseItem<M>[] {
            throw Error("To be implemented by inheritor");
        }

        refreshFilterAndGrid() {
            if (this.isRefreshNeeded()) {
                // do the search to update facets as well as the grid
                if (this.filterPanel) {
                    this.filterPanel.search();
                } else {
                    this.treeGridPanel.refresh();
                }
                this.refreshNeeded = false;
            }
        }

        isRefreshNeeded(): boolean {
            return this.refreshNeeded;
        }

        setRefreshNeeded(refreshNeeded: boolean) {
            this.refreshNeeded = refreshNeeded;
        }

        toggleShowingNewGrid() {
            if (this.treeSwapperDeckPanel.getPanelShownIndex() == 0) {
                this.treeSwapperDeckPanel.showPanel(1);
            }
            else {
                this.treeSwapperDeckPanel.showPanel(0);
            }
        }

    }
}
