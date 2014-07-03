module api.app.browse {

    export interface BrowsePanelParams<M> {

        browseToolbar:api.ui.toolbar.Toolbar;

        treeGridPanel?:api.app.browse.grid.TreeGridPanel;

        treeGridPanel2?:api.ui.treegrid.TreeGrid<api.ui.treegrid.TreeItem>;

        browseItemPanel:BrowseItemPanel<M>;

        filterPanel?:api.app.browse.filter.BrowseFilterPanel;
    }

    export class BrowsePanel<M> extends api.ui.Panel implements api.ui.ActionContainer {

        private static SPLIT_PANEL_ALIGNMENT_TRESHOLD: number = 1180;

        private browseToolbar: api.ui.toolbar.Toolbar;

        private oldTreeGrid: api.app.browse.grid.TreeGridPanel;

        private newTreeGrid: api.ui.treegrid.TreeGrid<api.ui.treegrid.TreeItem>;

        private treeSwapperDeckPanel: api.ui.DeckPanel;

        private browseItemPanel: BrowseItemPanel<M>;

        private gridAndDetailSplitPanel: api.ui.SplitPanel;

        private filterPanel: api.app.browse.filter.BrowseFilterPanel;

        private gridAndFilterAndDetailSplitPanel:api.ui.SplitPanel;

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
                    .setFirstPanelSize(200, api.ui.SplitPanelUnit.PIXEL).setAlignment(api.ui.SplitPanelAlignment.VERTICAL).build();
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

            api.ui.ResponsiveManager.onAvailableSizeChanged(this, (item:api.ui.ResponsiveItem) => {
                if (item.isInRangeOrSmaller(api.ui.ResponsiveRanges._360_540)) {
                    if (this.filterPanel && !this.gridAndFilterAndDetailSplitPanel.isPanelHidden(1)) {
                        this.gridAndFilterAndDetailSplitPanel.hidePanel(1);
                    }
                    if (!this.gridAndDetailSplitPanel.isPanelHidden(2)) {
                        this.gridAndDetailSplitPanel.hidePanel(2);
                    }
                } else if (item.isInRangeOrBigger(api.ui.ResponsiveRanges._540_720)) {
                    if (this.filterPanel && this.gridAndFilterAndDetailSplitPanel.isPanelHidden(1)) {
                        this.gridAndFilterAndDetailSplitPanel.showPanel(1);
                    }
                    if (this.gridAndDetailSplitPanel.isPanelHidden(2)) {
                        this.gridAndDetailSplitPanel.showPanel(2);
                    }
                }
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
