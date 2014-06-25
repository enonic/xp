module api.app.browse {

    export interface BrowsePanelParams<M> {

        browseToolbar:api.ui.toolbar.Toolbar;

        treeGridPanel?:api.app.browse.grid.TreeGridPanel;

        treeGridPanel2?:api.app.browse.treegrid.TreeGrid<api.node.Node>;

        browseItemPanel:BrowseItemPanel<M>;

        filterPanel?:api.app.browse.filter.BrowseFilterPanel;
    }

    export class BrowsePanel<M> extends api.ui.Panel implements api.ui.ActionContainer {

        private static SPLIT_PANEL_ALIGNMENT_TRESHOLD: number = 1180;

        private browseToolbar: api.ui.toolbar.Toolbar;

        private oldTreeGrid: api.app.browse.grid.TreeGridPanel;

        private newTreeGrid: api.app.browse.treegrid.TreeGrid<api.node.Node>;

        private treeSwapperDeckPanel: api.ui.DeckPanel;

        private browseItemPanel: BrowseItemPanel<M>;

        private gridAndDetailSplitPanel: api.ui.SplitPanel;

        private filterPanel: api.app.browse.filter.BrowseFilterPanel;

        private gridAndFilterAndDetailSplitPanel;

        private gridAndToolbarContainer: api.ui.Panel;

        private refreshNeeded: boolean = false;

        constructor(params: BrowsePanelParams<M>) {
            super();

            this.browseToolbar = params.browseToolbar;
            this.oldTreeGrid = params.treeGridPanel;
            this.newTreeGrid = params.treeGridPanel2;
            this.browseItemPanel = params.browseItemPanel;
            this.filterPanel = params.filterPanel;

            this.browseItemPanel.onDeselected((event: ItemDeselectedEvent<M>) => {
                this.oldTreeGrid.deselectItem(event.getBrowseItem().getPath());
            });

            this.gridAndToolbarContainer = new api.ui.Panel();
            this.gridAndToolbarContainer.appendChild(this.browseToolbar);

            this.treeSwapperDeckPanel = new api.ui.DeckPanel();
            if (this.oldTreeGrid) {
                this.treeSwapperDeckPanel.addPanel(this.oldTreeGrid);
            }
            if (this.newTreeGrid) {
                this.treeSwapperDeckPanel.addPanel(this.newTreeGrid);
            }
            this.treeSwapperDeckPanel.showPanelByIndex(0);

            this.gridAndToolbarContainer.appendChild(this.treeSwapperDeckPanel);

            this.gridAndDetailSplitPanel = new api.ui.SplitPanelBuilder(this.gridAndToolbarContainer, this.browseItemPanel)
                .setAlignmentTreshold(BrowsePanel.SPLIT_PANEL_ALIGNMENT_TRESHOLD).build();

            if (this.filterPanel) {
                this.gridAndFilterAndDetailSplitPanel = new api.ui.SplitPanelBuilder(this.filterPanel, this.gridAndDetailSplitPanel)
                    .fixFirstPanelSize("200px").setAlignment(api.ui.SplitPanelAlignment.VERTICAL).build();
            } else {
                this.gridAndFilterAndDetailSplitPanel = this.gridAndDetailSplitPanel;
            }

            if (this.oldTreeGrid) {
                this.oldTreeGrid.onTreeGridSelectionChanged((event: api.app.browse.grid.TreeGridSelectionChangedEvent) => {
                    var browseItems: api.app.browse.BrowseItem<M>[] = this.extModelsToBrowseItems(event.getSelectedModels());
                    this.browseItemPanel.setItems(browseItems);
                });
            }
            if( this.newTreeGrid ) {
                // TODO:
            }

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
                    if (this.oldTreeGrid) {
                        this.oldTreeGrid.refresh();
                    }
                    if (this.newTreeGrid) {
                        // TODO: ? this.treeGrid.refresh();
                    }
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
                this.treeSwapperDeckPanel.showPanelByIndex(1);
            }
            else {
                this.treeSwapperDeckPanel.showPanelByIndex(0);
            }
        }

    }
}
