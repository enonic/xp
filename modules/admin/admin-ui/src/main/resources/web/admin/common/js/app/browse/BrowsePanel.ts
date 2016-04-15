module api.app.browse {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import TreeNode = api.ui.treegrid.TreeNode;

    export interface BrowsePanelParams<M extends api.Equitable> {

        browseToolbar:api.ui.toolbar.Toolbar;

        treeGrid?:api.ui.treegrid.TreeGrid<Object>;

        browseItemPanel:BrowseItemPanel<M>;

        filterPanel?:api.app.browse.filter.BrowseFilterPanel;

        hasDetailsPanel?: boolean;
    }

    export class BrowsePanel<M extends api.Equitable> extends api.ui.panel.Panel implements api.ui.ActionContainer {

        private static SPLIT_PANEL_ALIGNMENT_TRESHOLD: number = 720;

        private browseToolbar: api.ui.toolbar.Toolbar;

        private treeGrid: api.ui.treegrid.TreeGrid<Object>;

        private gridAndToolbarPanel: api.ui.panel.Panel;

        private browseItemPanel: BrowseItemPanel<M>;

        private gridAndItemsSplitPanel: api.ui.panel.SplitPanel;

        private filterPanel: api.app.browse.filter.BrowseFilterPanel;

        private filterAndGridSplitPanel: api.ui.panel.SplitPanel;

        private filterPanelForcedShown: boolean = false;

        private filterPanelForcedHidden: boolean = false;

        private filterPanelToBeShownFullScreen: boolean = false;

        private filterPanelIsHiddenByDefault: boolean = true;

        private toggleFilterPanelAction: api.ui.Action;

        constructor(params: BrowsePanelParams<M>) {
            super();

            this.browseToolbar = params.browseToolbar;
            this.treeGrid = params.treeGrid;
            this.browseItemPanel = params.browseItemPanel;
            this.filterPanel = params.filterPanel;

            this.browseItemPanel.onDeselected((event: ItemDeselectedEvent<M>) => {
                let oldSelectedCount = this.treeGrid.getGrid().getSelectedRows().length;
                this.treeGrid.deselectNodes([event.getBrowseItem().getId()]);
                let newSelectedCount = this.treeGrid.getGrid().getSelectedRows().length;

                if (oldSelectedCount === newSelectedCount) {
                    this.treeGrid.getContextMenu().getActions()
                        .updateActionsEnabledState(this.browseItemPanel.getItems())
                        .then(() => {
                            this.browseItemPanel.updateDisplayedPanel();
                        });
                }
            });

            this.treeGrid.onSelectionChanged((currentSelection: TreeNode<Object>[], fullSelection: TreeNode<Object>[]) => {
                let browseItems: api.app.browse.BrowseItem<M>[] = this.treeNodesToBrowseItems(fullSelection);
                let changes = this.browseItemPanel.setItems(browseItems);
                this.treeGrid.getContextMenu().getActions()
                    .updateActionsEnabledState(this.browseItemPanel.getItems(), changes)
                    .then(() => {
                        this.browseItemPanel.updateDisplayedPanel();
                    }).catch(api.DefaultErrorHandler.handle);
            });

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                this.checkFilterPanelToBeShownFullScreen(item);

                if (this.isRendered()) {
                    if (!this.filterPanelIsHiddenByDefault) { //not relevant if filter panel is hidden by default
                        this.toggleFilterPanelDependingOnScreenSize(item);
                    }
                    this.togglePreviewPanelDependingOnScreenSize(item);
                }
            });
        }

        doRender(): boolean {
            this.gridAndItemsSplitPanel = new api.ui.panel.SplitPanelBuilder(this.treeGrid, this.browseItemPanel)
                .setAlignmentTreshold(BrowsePanel.SPLIT_PANEL_ALIGNMENT_TRESHOLD)
                .build();

            this.gridAndItemsSplitPanel.setFirstPanelSize(38, api.ui.panel.SplitPanelUnit.PERCENT);

            this.browseToolbar.addClass("browse-toolbar");
            this.gridAndItemsSplitPanel.addClass("content-grid-and-browse-split-panel");

            if (this.filterPanel) {
                this.gridAndToolbarPanel = new api.ui.panel.Panel();
                this.gridAndToolbarPanel.appendChildren<any>(this.browseToolbar, this.gridAndItemsSplitPanel);

                this.filterAndGridSplitPanel = this.setupFilterPanel();
                this.appendChild(this.filterAndGridSplitPanel);
                if (this.filterPanelIsHiddenByDefault) {
                    this.hideFilterPanel();
                }
            } else {
                this.appendChildren<any>(this.browseToolbar, this.gridAndItemsSplitPanel);
            }
            return true;
        }

        getFilterAndGridSplitPanel(): api.ui.panel.Panel {
            return this.filterAndGridSplitPanel;
        }

        getTreeGrid(): api.ui.treegrid.TreeGrid<Object> {
            return this.treeGrid;
        }

        getBrowseItemPanel(): BrowseItemPanel<M> {
            return this.browseItemPanel;
        }

        getActions(): api.ui.Action[] {
            return this.browseToolbar.getActions();
        }

        treeNodesToBrowseItems(nodes: TreeNode<Object>[]): BrowseItem<M>[] {
            return [];
        }

        refreshFilter() {
            if (this.filterPanel) {
                this.filterPanel.refresh();
            }
        }

        setRefreshOfFilterRequired() {
            if (this.filterPanel) {
                this.filterPanel.setRefreshOfFilterRequired();
            }
        }

        toggleFilterPanel() {
            this.filterAndGridSplitPanel.setFirstPanelIsFullScreen(this.filterPanelToBeShownFullScreen);

            if (this.filterPanelIsHidden()) {
                this.showFilterPanel();
            } else {
                this.hideFilterPanel();
            }
        }

        private filterPanelIsHidden(): boolean {
            return this.filterAndGridSplitPanel.isFirstPanelHidden();
        }

        private showFilterPanel() {
            this.filterPanelForcedShown = true;
            this.filterPanelForcedHidden = false;

            if (this.filterPanelToBeShownFullScreen) {
                this.filterAndGridSplitPanel.hideSecondPanel();
            }

            this.filterAndGridSplitPanel.showFirstPanel();
            this.filterPanel.giveFocusToSearch();
            this.toggleFilterPanelAction.setVisible(false);
        }

        private hideFilterPanel() {
            this.filterPanelForcedShown = false;
            this.filterPanelForcedHidden = true;
            this.filterAndGridSplitPanel.showSecondPanel();
            this.filterAndGridSplitPanel.hideFirstPanel();

            this.toggleFilterPanelAction.setVisible(true);
        }

        private setupFilterPanel() {
            var splitPanel = new api.ui.panel.SplitPanelBuilder(this.filterPanel, this.gridAndToolbarPanel)
                .setFirstPanelSize(200, api.ui.panel.SplitPanelUnit.PIXEL)
                .setAlignment(api.ui.panel.SplitPanelAlignment.VERTICAL)
                .setAnimationDelay(100)     // filter panel animation time
                .build();

            this.filterPanel.onHideFilterPanelButtonClicked(this.toggleFilterPanel.bind(this));
            this.filterPanel.onShowResultsButtonClicked(this.toggleFilterPanel.bind(this));

            this.addToggleFilterPanelButtonInToolbar();
            return splitPanel;
        }

        private addToggleFilterPanelButtonInToolbar() {
            this.toggleFilterPanelAction = new api.app.browse.action.ToggleFilterPanelAction(this);
            var existingActions: api.ui.Action[] = this.browseToolbar.getActions();
            this.browseToolbar.removeActions();
            this.browseToolbar.addAction(this.toggleFilterPanelAction);
            this.browseToolbar.addActions(existingActions);
            this.toggleFilterPanelAction.setVisible(false);
        }

        private checkFilterPanelToBeShownFullScreen(item: ResponsiveItem) {
            this.filterPanelToBeShownFullScreen = item.isInRangeOrSmaller(ResponsiveRanges._360_540);
        }

        private toggleFilterPanelDependingOnScreenSize(item: ResponsiveItem) {
            if (item.isInRangeOrSmaller(ResponsiveRanges._1380_1620)) {
                if (this.filterPanel && !this.filterAndGridSplitPanel.isFirstPanelHidden() && !this.filterPanelForcedShown) {
                    this.filterAndGridSplitPanel.hideFirstPanel();
                    this.toggleFilterPanelAction.setVisible(true);
                }
            } else if (item.isInRangeOrBigger(ResponsiveRanges._1620_1920)) {
                if (this.filterPanel && this.filterAndGridSplitPanel.isFirstPanelHidden() && !this.filterPanelForcedHidden) {
                    this.filterAndGridSplitPanel.showFirstPanel();
                    this.toggleFilterPanelAction.setVisible(false);
                }
            }
        }

        private togglePreviewPanelDependingOnScreenSize(item: ResponsiveItem) {
            if (item.isInRangeOrSmaller(ResponsiveRanges._360_540)) {
                if (!this.gridAndItemsSplitPanel.isSecondPanelHidden()) {
                    this.gridAndItemsSplitPanel.hideSecondPanel();
                }
            } else if (item.isInRangeOrBigger(ResponsiveRanges._540_720)) {
                if (this.gridAndItemsSplitPanel.isSecondPanelHidden()) {
                    this.gridAndItemsSplitPanel.showSecondPanel();
                }
            }
        }

    }
}