module app.browse {

    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

    // TODO: ContentSummary must be replaced with an ContentSummaryAndCompareStatus after old grid is removed
    export class ContentBrowsePanel extends api.app.browse.BrowsePanel<ContentSummary> {

        private browseActions: app.browse.ContentBrowseActions;

        private toolbar: ContentBrowseToolbar;

        private contentTreeGridPanel: app.browse.ContentTreeGridPanel;

        private contentTreeGridPanelMask: api.ui.LoadMask;

        private contentTreeGridPanel2: app.browse.grid.ContentGridPanel2;

        private contentFilterPanel: app.browse.filter.ContentBrowseFilterPanel;

        private contentBrowseItemPanel: ContentBrowseItemPanel;

        constructor() {
            var treeGridContextMenu = new ContentTreeGridContextMenu();
            this.contentTreeGridPanel = new app.browse.ContentTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.contentTreeGridPanelMask = new api.ui.LoadMask(this.contentTreeGridPanel);

            this.browseActions = ContentBrowseActions.init(this.contentTreeGridPanel);
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new ContentBrowseToolbar(this.browseActions);
            this.contentBrowseItemPanel =
            components.detailPanel = new ContentBrowseItemPanel();

            this.contentFilterPanel = new app.browse.filter.ContentBrowseFilterPanel();

            this.contentTreeGridPanel2 = new app.browse.grid.ContentGridPanel2();

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: this.contentTreeGridPanel,
                treeGridPanel2: this.contentTreeGridPanel2,
                browseItemPanel: this.contentBrowseItemPanel,
                filterPanel: this.contentFilterPanel});

            api.content.ContentDeletedEvent.on((event) => {
                this.contentFilterPanel.search();
                this.contentTreeGridPanel.deselectAll();
            });

            api.content.ContentCreatedEvent.on((event) => {
                this.setRefreshNeeded(true);
            });

            api.content.ContentUpdatedEvent.on((event) => {
                this.setRefreshNeeded(true);
            });

            var showMask = () => {
                this.contentTreeGridPanelMask.show();
            };
            this.contentFilterPanel.onSearch(showMask);
            this.contentFilterPanel.onReset(showMask);
            this.contentTreeGridPanel.onRendered(showMask);
            this.contentTreeGridPanel.onTreeGridStoreLoaded(() => {
                this.contentTreeGridPanelMask.hide();
            });

            this.contentTreeGridPanel.onTreeGridSelectionChanged((event: api.app.browse.grid.TreeGridSelectionChangedEvent) => {
                var contentSummaries = this.extModelsToContentSummaries(event.getSelectedModels());
                this.browseActions.updateActionsEnabledState(contentSummaries);
            });

            this.contentTreeGridPanel.onTreeGridItemDoubleClicked((event: api.app.browse.grid.TreeGridItemDoubleClickedEvent) => {
                var contentSummaries = this.extModelsToContentSummaries([event.getClickedModel()]);
                new app.browse.EditContentEvent(contentSummaries).fire();
            });

            ShowNewContentGridEvent.on(() => {
                super.toggleShowingNewGrid();
            });

            this.onShown((event) => {
                app.Router.setHash("browse");
            });

            ToggleSearchPanelEvent.on(() => {
                console.log("Toggling searchpanel event");
                this.toggleFilterPanel();
            });

            api.ui.ResponsiveManager.onAvailableSizeChanged(this, (item:api.ui.ResponsiveItem) => {
                if (item.isInRangeOrSmaller(api.ui.ResponsiveRanges._360_540)) {
                    this.browseActions.TOGGLE_SEARCH_PANEL.setVisible(true);
                } else if (item.isInRangeOrBigger(api.ui.ResponsiveRanges._540_720)) {
                    this.browseActions.TOGGLE_SEARCH_PANEL.setVisible(false);
                }
            });
        }

        getActions(): api.ui.Action[] {
            var actions = super.getActions();
            // TODO: Ensures shortcut for showing new experimental content grid without having the action in the toolbar
            actions.push(app.browse.ContentBrowseActions.get().SHOW_NEW_CONTENT_GRID);
            return actions;
        }

        extModelsToContentSummaries(models: Ext_data_Model[]): ContentSummary[] {

            var summaries: ContentSummary[] = [];
            for (var i = 0; i < models.length; i++) {
                summaries.push(ContentSummary.fromJson(<api.content.json.ContentSummaryJson>models[i].data))
            }
            return summaries;
        }

        extModelsToBrowseItems(models: Ext_data_Model[]): BrowseItem<ContentSummary>[] {

            var browseItems: BrowseItem<ContentSummary>[] = [];
            models.forEach((model: Ext_data_Model) => {
                var content = ContentSummary.fromJson(<api.content.json.ContentSummaryJson>model.data);
                var item = new BrowseItem<ContentSummary>(content).
                    setDisplayName(model.data['displayName']).
                    setPath(model.data['path']).
                    setIconUrl(model.data['iconUrl']);
                browseItems.push(item);
            });
            return browseItems;
        }

        // TODO: ContentSummary must be replaced with an ContentSummaryAndCompareStatus after old grid is removed
        treeNodesToBrowseItems(nodes: TreeNode<ContentSummaryAndCompareStatus>[]): BrowseItem<ContentSummary>[] {
            var browseItems: BrowseItem<ContentSummary>[] = [];

            // do not proceed duplicated content. still, it can be selected
            nodes.forEach((node: TreeNode<ContentSummaryAndCompareStatus>, index: number) => {
                for (var i = 0; i <= index; i++) {
                    if (nodes[i].getData().getId() === node.getData().getId()) {
                        break;
                    }
                }
                if (i === index) {
                    var content = node.getData().getContentSummary();
                    var item = new BrowseItem<ContentSummary>(content).
                        setId(content.getId()).
                        setDisplayName(content.getDisplayName()).
                        setPath(content.getPath().toString()).
                        setIconUrl(content.getIconUrl());
                    browseItems.push(item);
                }
            });

            return browseItems;
        }
    }

}
