module app.browse {

    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;
    import UploadItem = api.ui.uploader.UploadItem;
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

            var showMask = () => {
                this.contentTreeGridPanelMask.show();
            };
            this.contentTreeGridPanelMask.show();
            this.contentFilterPanel.onSearch(showMask);
            this.contentFilterPanel.onReset(showMask);
            this.contentFilterPanel.onRefresh(showMask);
            this.contentTreeGridPanel.onRendered(showMask);
            this.contentTreeGridPanel.onLoaded(() => {
                this.contentTreeGridPanelMask.hide();
            });

            this.getTreeGrid().onDataChanged((event: api.ui.treegrid.DataChangedEvent<ContentSummaryAndCompareStatus>) => {
                if (event.getType() === 'updated') {
                    var browseItems = this.treeNodesToBrowseItems(event.getTreeNodes());
                    this.getBrowseItemPanel().updateItemViewers(browseItems);
                }
            });

            this.onShown((event) => {
                app.Router.setHash("browse");
            });

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                if (item.isInRangeOrSmaller(ResponsiveRanges._360_540)) {
                    this.browseActions.TOGGLE_SEARCH_PANEL.setVisible(true);
                } else if (item.isInRangeOrBigger(ResponsiveRanges._540_720)) {
                    this.browseActions.TOGGLE_SEARCH_PANEL.setVisible(false);
                }
            });

            this.handleGlobalEvents();
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


        private handleGlobalEvents() {
            api.content.ContentDeletedEvent.on((event) => {
                this.handleContentDeleted(event);
            });

            api.content.ContentCreatedEvent.on((event) => {
                this.handleContentCreated(event);
            });

            api.content.ContentDuplicatedEvent.on((event) => {
                this.handleContentDuplicated(event);
            });

            api.content.ContentUpdatedEvent.on((event) => {
                this.contentTreeGridPanel.updateContentNode(event.getContentId());
            });

            api.content.ContentPublishedEvent.on((event) => {
                this.contentTreeGridPanel.updateContentNode(event.getContentId());
            });

            api.content.ContentMovedEvent.on((event) => {
                this.contentTreeGridPanel.reload();
            });

            api.content.ContentChildOrderUpdatedEvent.on((event) => {
                this.handleChildOrderUpdated(event);
            });

            ToggleSearchPanelEvent.on(() => {
                this.toggleFilterPanel();
            });

            app.create.NewMediaUploadEvent.on((event) => {
                this.handleNewMediaUpload(event);
            });
        }

        private handleContentDeleted(event: api.content.ContentDeletedEvent) {
            this.setFilterPanelRefreshNeeded(true);
            /*
             Deleting content won't trigger browsePanel.onShow event,
             because we are left on the same panel. We need to refresh manually.
             */
            this.contentTreeGridPanel.deleteNodes(event.getContents().map((elem) => {
                return api.content.ContentSummaryAndCompareStatus.fromContentSummary(elem);
            }));
            this.refreshFilter();
        }

        private handleContentCreated(event: api.content.ContentCreatedEvent) {
            this.contentTreeGridPanel.appendContentNode(event.getContentId());
            this.setFilterPanelRefreshNeeded(true);
        }

        private handleContentDuplicated(event: api.content.ContentDuplicatedEvent) {
            this.contentTreeGridPanel.appendContentNode(event.getContent().getContentId(), event.isNextToSource());
            this.setFilterPanelRefreshNeeded(true);
            window.setTimeout(() => {
                this.refreshFilter();
            }, 1000);
        }

        private handleChildOrderUpdated(event: api.content.ContentChildOrderUpdatedEvent) {
            this.contentTreeGridPanel.updateContentNode(event.getContentId());
            var updatedNode = this.contentTreeGridPanel.getRoot().getCurrentRoot()
                .findNode(event.getContentId().toString());
            this.contentTreeGridPanel.sortNodeChildren(updatedNode);
        }

        private handleNewMediaUpload(event: app.create.NewMediaUploadEvent) {
            event.getUploadItems().forEach((item: UploadItem<ContentSummary>) => {
                this.contentTreeGridPanel.appendUploadNode(item);
            });

        }
    }

}
