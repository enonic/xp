module app.browse {

    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;

    export class ContentBrowsePanel extends api.app.browse.BrowsePanel<ContentSummary> {

        private browseActions: app.browse.action.ContentTreeGridActions;

        private toolbar: ContentBrowseToolbar;

        private contentTreeGridPanelMask: api.ui.mask.LoadMask;

        private contentTreeGridPanel2: app.browse.ContentTreeGrid;

        private contentFilterPanel: app.browse.filter.ContentBrowseFilterPanel;

        private contentBrowseItemPanel: ContentBrowseItemPanel;

        constructor() {
            this.contentTreeGridPanel2 = new app.browse.ContentTreeGrid();

            this.contentBrowseItemPanel = components.detailPanel = new ContentBrowseItemPanel();

            this.contentFilterPanel = new app.browse.filter.ContentBrowseFilterPanel();

            this.browseActions = <app.browse.action.ContentTreeGridActions>this.contentTreeGridPanel2.getContextMenu().getActions();

            this.toolbar = new ContentBrowseToolbar(this.browseActions);

            this.contentTreeGridPanelMask = new api.ui.mask.LoadMask(this.contentTreeGridPanel2);

            super({
                browseToolbar: this.toolbar,
                treeGridPanel2: this.contentTreeGridPanel2,
                browseItemPanel: this.contentBrowseItemPanel,
                filterPanel: this.contentFilterPanel
            });

            api.content.ContentDeletedEvent.on((event) => {
                this.setRefreshNeeded(true);
                /*
                Deleting content won't trigger browsePanel.onShow event,
                because we are left on the same panel. We need to refresh manually.
                 */
                this.contentTreeGridPanel2.deleteNodes(event.getContents().map((elem) => {
                    return new api.content.ContentSummaryAndCompareStatus(elem, null);
                }));
                this.refreshFilterAndGrid();
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
            this.contentTreeGridPanelMask.show();
            this.contentFilterPanel.onSearch(showMask);
            this.contentFilterPanel.onReset(showMask);
            this.contentTreeGridPanel2.onRendered(showMask);
            this.contentTreeGridPanel2.onLoaded(() => {
                this.contentTreeGridPanelMask.hide();
            });

            this.onShown((event) => {
                app.Router.setHash("browse");
            });

            ToggleSearchPanelEvent.on(() => {
                console.log("Toggling searchpanel event");
                this.toggleFilterPanel();
            });

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                if (item.isInRangeOrSmaller(ResponsiveRanges._360_540)) {
                    this.browseActions.TOGGLE_SEARCH_PANEL.setVisible(true);
                } else if (item.isInRangeOrBigger(ResponsiveRanges._540_720)) {
                    this.browseActions.TOGGLE_SEARCH_PANEL.setVisible(false);
                }
            });
        }

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
