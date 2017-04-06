module api.app.browse {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import TreeNode = api.ui.treegrid.TreeNode;
    import ActionButton = api.ui.button.ActionButton;
    import BrowseItem = api.app.browse.BrowseItem;
    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;

    export class BrowsePanel<M extends api.Equitable> extends api.ui.panel.Panel implements api.ui.ActionContainer {

        private static SPLIT_PANEL_ALIGNMENT_TRESHOLD: number = 720;

        protected browseToolbar: api.ui.toolbar.Toolbar;

        protected treeGrid: api.ui.treegrid.TreeGrid<Object>;

        protected filterPanel: api.app.browse.filter.BrowseFilterPanel<Object>;

        private gridAndToolbarPanel: api.ui.panel.Panel;

        private browseItemPanel: BrowseItemPanel<M>;

        private gridAndItemsSplitPanel: api.ui.panel.SplitPanel;

        private filterAndGridSplitPanel: api.ui.panel.SplitPanel;

        private filterPanelForcedShown: boolean = false;

        private filterPanelForcedHidden: boolean = false;

        private filterPanelToBeShownFullScreen: boolean = false;

        private filterPanelIsHiddenByDefault: boolean = true;

        private toggleFilterPanelAction: api.ui.Action;

        private toggleFilterPanelButton: ActionButton;

        constructor() {
            super();

            this.treeGrid = this.createTreeGrid();
            this.filterPanel = this.createFilterPanel();
            this.browseToolbar = this.createToolbar();

            let selectionChangedHandler = (currentSelection: TreeNode<Object>[],
                                           fullSelection: TreeNode<Object>[],
                                           highlighted: boolean
                                                   ) => {
                if (this.treeGrid.getToolbar().getSelectionPanelToggler().isActive()) {
                    this.updateSelectionModeShownItems(currentSelection, fullSelection);
                }

                let browseItems: api.app.browse.BrowseItem<M>[] = this.treeNodesToBrowseItems(fullSelection);
                let changes = this.getBrowseItemPanel().setItems(browseItems);

                if (highlighted && ((fullSelection.length == 0 && changes.getRemoved().length === 1) ||
                                    (fullSelection.length == 1 && changes.getAdded().length === 1))) {
                    return;
                }

                this.getBrowseActions().updateActionsEnabledState(this.getBrowseItemPanel().getItems(), changes)
                    .then(() => {
                        if (this.getBrowseItemPanel().getItems().length > 0 || !highlighted) {
                            this.getBrowseItemPanel().updatePreviewPanel();
                        }
                    }).catch(api.DefaultErrorHandler.handle);

                if (this.treeGrid.getToolbar().getSelectionPanelToggler().isActive() && changes.getRemoved().length > 0) {
                    // Redo the search filter panel once items are removed from selection
                    this.filterPanel.setSelectedItems(this.treeGrid.getSelectedDataList());
                }

            };

            this.treeGrid.onSelectionChanged(selectionChangedHandler);

            let highlightingChangedDebouncedHandler = api.util.AppHelper.debounce(((node: TreeNode<Object>) => {
                this.onHighlightingChanged(node);
            }).bind(this), 200, false);

            this.treeGrid.onHighlightingChanged(highlightingChangedDebouncedHandler);

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                this.checkFilterPanelToBeShownFullScreen(item);

                if (this.isRendered()) {
                    if (!this.filterPanelIsHiddenByDefault) { //not relevant if filter panel is hidden by default
                        this.toggleFilterPanelDependingOnScreenSize(item);
                    }
                    this.togglePreviewPanelDependingOnScreenSize(item);
                }
            });

            this.treeGrid.getToolbar().getSelectionPanelToggler().onActiveChanged(isActive => {
                this.treeGrid.toggleClass('selection-mode', isActive);

                if (isActive) {
                    if (this.filterPanel) {
                        this.filterPanel.setSelectedItems(this.treeGrid.getSelectedDataList());
                        this.showFilterPanel();
                    }
                } else {
                    this.filterPanel.resetConstraints();
                    this.treeGrid.resetFilter();
                    this.hideFilterPanel();
                }
            });

            this.onShown(() => {
                if (this.treeGrid.isFiltered()) {
                    this.filterPanel.refresh();
                }
            });

        }

        protected checkIfItemIsRenderable(browseItem: BrowseItem<M>): wemQ.Promise<boolean> {
            let deferred = wemQ.defer<boolean>();
            deferred.resolve(true);
            return deferred.promise;
        }

        private onHighlightingChanged(node: TreeNode<Object>) {
            if (!node) {
                if (this.treeGrid.getSelectedDataList().length === 0) {
                    this.getBrowseActions().updateActionsEnabledState([]);
                    this.getBrowseItemPanel().togglePreviewForItem();
                }

                return;
            }

            let browseItem: BrowseItem<M> = this.treeNodesToBrowseItems([node])[0];
            this.getBrowseActions().updateActionsEnabledState([browseItem]);
            this.checkIfItemIsRenderable(browseItem).then(() => {
                this.getBrowseItemPanel().togglePreviewForItem(browseItem);
            });
        }

        protected createToolbar(): api.ui.toolbar.Toolbar {
            throw 'Must be implemented by inheritors';
        }

        protected createTreeGrid(): api.ui.treegrid.TreeGrid<Object> {
            throw 'Must be implemented by inheritors';
        }

        protected createBrowseItemPanel(): BrowseItemPanel<M> {
            throw 'Must be implemented by inheritors';
        }

        protected getBrowseActions(): TreeGridActions<M> {
            return this.treeGrid.getContextMenu().getActions();
        }

        protected createFilterPanel(): api.app.browse.filter.BrowseFilterPanel<M> {
            return null;
        }

        doRender(): wemQ.Promise<boolean> {
            return super.doRender().then((rendered) => {
                if (!this.browseItemPanel) {
                    this.browseItemPanel = this.createBrowseItemPanel();
                }
                this.gridAndItemsSplitPanel = new api.ui.panel.SplitPanelBuilder(this.treeGrid, this.browseItemPanel)
                    .setAlignmentTreshold(BrowsePanel.SPLIT_PANEL_ALIGNMENT_TRESHOLD)
                    .build();

                this.gridAndItemsSplitPanel.setFirstPanelSize(38, api.ui.panel.SplitPanelUnit.PERCENT);

                this.browseToolbar.addClass('browse-toolbar');
                this.gridAndItemsSplitPanel.addClass('content-grid-and-browse-split-panel');

                if (this.filterPanel) {
                    this.gridAndToolbarPanel = new api.ui.panel.Panel();

                    this.gridAndToolbarPanel.onAdded(() => {
                        this.gridAndItemsSplitPanel.setDoOffset(true);
                    });

                    this.filterAndGridSplitPanel = this.setupFilterPanel();
                    if (this.filterPanelIsHiddenByDefault) {
                        this.hideFilterPanel();
                    }
                    this.appendChild(this.filterAndGridSplitPanel);

                    // Hack: Places the append calls farther in the engine call stack.
                    // Prevent toolbar and gridPanel not being visible when the width/height
                    // is requested and elements resize/change position/etc.
                    setTimeout(() => {
                        this.gridAndToolbarPanel.appendChild(this.browseToolbar);
                    });
                    this.browseToolbar.onRendered(() => {
                        setTimeout(() => {
                            this.gridAndToolbarPanel.appendChild(this.gridAndItemsSplitPanel);
                        });
                    });
                } else {
                    this.appendChild(this.browseToolbar);
                    // Hack: Same hack.
                    this.browseToolbar.onRendered(() => {
                        setTimeout(() => {
                            this.appendChild(this.gridAndItemsSplitPanel);
                        });
                    });
                }
                return rendered;
            });
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
            if (this.filterPanel && (this.filterPanel.isVisible() || this.treeGrid.isFiltered())) {
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

        protected showFilterPanel() {
            this.filterPanelForcedShown = true;
            this.filterPanelForcedHidden = false;

            if (this.filterPanelToBeShownFullScreen) {
                this.filterAndGridSplitPanel.hideSecondPanel();
            }

            this.filterAndGridSplitPanel.showFirstPanel();
            this.filterPanel.giveFocusToSearch();
            this.toggleFilterPanelAction.setVisible(false);
            this.toggleFilterPanelButton.removeClass('filtered');
        }

        private hideFilterPanel() {
            this.filterPanelForcedShown = false;
            this.filterPanelForcedHidden = true;
            this.filterAndGridSplitPanel.showSecondPanel();
            this.filterAndGridSplitPanel.hideFirstPanel();

            this.toggleFilterPanelAction.setVisible(true);
            if (this.filterPanel.hasFilterSet()) {
                this.toggleFilterPanelButton.addClass('filtered');
            }

        }

        private setupFilterPanel() {
            let splitPanel = new api.ui.panel.SplitPanelBuilder(this.filterPanel, this.gridAndToolbarPanel)
                .setFirstPanelMinSize(215, api.ui.panel.SplitPanelUnit.PIXEL)
                .setFirstPanelSize(215, api.ui.panel.SplitPanelUnit.PIXEL)
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
            this.toggleFilterPanelButton = new ActionButton(this.toggleFilterPanelAction);
            this.browseToolbar.prependChild(this.toggleFilterPanelButton);
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

        private updateSelectionModeShownItems(currentSelection: TreeNode<Object>[],
                                              fullSelection: TreeNode<Object>[]) {
            if (currentSelection.length === fullSelection.length) { // to filter unwanted selection change events
                let amountOfNodesShown: number = this.treeGrid.getRoot().getCurrentRoot().treeToList().length;
                if (currentSelection.length === 0 || amountOfNodesShown === 0) { // all items deselected
                    this.treeGrid.getToolbar().getSelectionPanelToggler().setActive(false);
                } else if (amountOfNodesShown > fullSelection.length) { // some item/items deselected
                    this.treeGrid.filter(this.treeGrid.getSelectedDataList());
                }
            }
        }

    }
}
