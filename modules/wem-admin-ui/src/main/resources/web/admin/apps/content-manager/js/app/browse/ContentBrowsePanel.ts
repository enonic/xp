module app.browse {

    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;

    export class ContentBrowsePanel extends api.app.browse.BrowsePanel<ContentSummary> {

        private browseActions: app.browse.action.ContentTreeGridActions;

        private toolbar: ContentBrowseToolbar;

        private contentTreeGridPanelMask: api.ui.mask.LoadMask;

        private contentTreeGridPanel: app.browse.ContentTreeGrid;

        private contentFilterPanel: app.browse.filter.ContentBrowseFilterPanel;

        private contentBrowseItemPanel: ContentBrowseItemPanel;

        constructor() {
            this.contentTreeGridPanel = new app.browse.ContentTreeGrid();

            this.contentBrowseItemPanel = components.detailPanel = new ContentBrowseItemPanel();

            this.contentFilterPanel = new app.browse.filter.ContentBrowseFilterPanel();

            this.browseActions = <app.browse.action.ContentTreeGridActions>this.contentTreeGridPanel.getContextMenu().getActions();

            this.toolbar = new ContentBrowseToolbar(this.browseActions);

            this.contentTreeGridPanelMask = new api.ui.mask.LoadMask(this.contentTreeGridPanel);

            super({
                browseToolbar: this.toolbar,
                treeGrid: this.contentTreeGridPanel,
                browseItemPanel: this.contentBrowseItemPanel,
                filterPanel: this.contentFilterPanel
            });

            api.content.ContentDeletedEvent.on((event) => {
                this.setRefreshNeeded(true);
                /*
                 Deleting content won't trigger browsePanel.onShow event,
                 because we are left on the same panel. We need to refresh manually.
                 */
                this.contentTreeGridPanel.deleteNodes(event.getContents().map((elem) => {
                    return new api.content.ContentSummaryAndCompareStatus(elem, null);
                }));
                this.refreshFilterAndGrid();
            });

            api.content.ContentCreatedEvent.on((event) => {
                this.contentTreeGridPanel.appendContentNode(event.getContent());
                this.setRefreshNeeded(false);
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
            this.contentTreeGridPanel.onRendered(showMask);
            this.contentTreeGridPanel.onLoaded(() => {
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
                    if (!!content) {
                        var item = new BrowseItem<ContentSummary>(content).
                            setId(content.getId()).
                            setDisplayName(content.getDisplayName()).
                            setPath(content.getPath().toString()).
                            setIconUrl(new ContentIconUrlResolver().setContent(content).resolve());
                        browseItems.push(item);
                    }
                }
            });

            return browseItems;
        }
    }

}
