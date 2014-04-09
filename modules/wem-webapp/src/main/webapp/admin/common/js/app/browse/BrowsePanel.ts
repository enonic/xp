module api.app.browse {

    export interface BrowsePanelParams<M> {

        browseToolbar:api.ui.toolbar.Toolbar;

        treeGridPanel:api.app.browse.grid.TreeGridPanel;

        treeGridPanel2?:api.app.browse.grid2.GridPanel2;

        browseItemPanel:BrowseItemPanel<M>;

        filterPanel?:api.app.browse.filter.BrowseFilterPanel;
    }

    export class BrowsePanel<M> extends api.ui.Panel implements api.ui.ActionContainer {

        private static SPLIT_PANEL_ALIGNMENT_TRESHOLD: number = 1240;

        private browseToolbar: api.ui.toolbar.Toolbar;

        private treeGridPanel: api.app.browse.grid.TreeGridPanel;

        private gridPanel2: api.app.browse.grid2.GridPanel2;

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
            this.treeGridPanel = params.treeGridPanel;
            this.gridPanel2 = params.treeGridPanel2;
            this.browseItemPanel = params.browseItemPanel;
            this.filterPanel = params.filterPanel;

            this.browseItemPanel.onDeselected((event: ItemDeselectedEvent<M>) => {
                this.treeGridPanel.deselectItem(event.getBrowseItem().getPath());
            });

            this.gridAndToolbarContainer = new api.ui.Panel();
            this.gridAndToolbarContainer.appendChild(this.browseToolbar);
            this.gridAndToolbarContainer.appendChild(this.treeGridPanel);

            var windowSize = api.dom.Body.get().getEl().getWidthWithMargin();

            if (this.gridPanel2 != null) {
                this.treeSwapperDeckPanel = new api.ui.DeckPanel();
                this.treeSwapperDeckPanel.addPanel(this.browseItemPanel);
                this.treeSwapperDeckPanel.addPanel(this.gridPanel2);
                this.treeSwapperDeckPanel.showPanelByIndex(0);

                this.gridAndDetailSplitPanel = new api.ui.SplitPanelBuilder(this.gridAndToolbarContainer, this.treeSwapperDeckPanel)
                    .setAlignmentTreshold(BrowsePanel.SPLIT_PANEL_ALIGNMENT_TRESHOLD).build();
            }
            else {
                this.gridAndDetailSplitPanel = new api.ui.SplitPanelBuilder(this.gridAndToolbarContainer, this.browseItemPanel)
                    .setAlignmentTreshold(BrowsePanel.SPLIT_PANEL_ALIGNMENT_TRESHOLD).build();
            }

            if (this.filterPanel) {
                this.gridAndFilterAndDetailSplitPanel = new api.ui.SplitPanelBuilder(this.filterPanel, this.gridAndDetailSplitPanel)
                    .fixFirstPanelSize("200px").setAlignment(api.ui.SplitPanelAlignment.VERTICAL).build();
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
                this.treeSwapperDeckPanel.showPanelByIndex(1);
            }
            else {
                this.treeSwapperDeckPanel.showPanelByIndex(0);
            }
        }

    }
}
