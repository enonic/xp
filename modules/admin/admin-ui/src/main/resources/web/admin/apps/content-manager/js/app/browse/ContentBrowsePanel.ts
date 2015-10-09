module app.browse {

    import TreeNode = api.ui.treegrid.TreeNode;
    import BrowseItem = api.app.browse.BrowseItem;
    import UploadItem = api.ui.uploader.UploadItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentSummaryAndCompareStatusFetcher = api.content.ContentSummaryAndCompareStatusFetcher;
    import CompareStatus = api.content.CompareStatus;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import ContentServerEvent = api.content.ContentServerEvent;
    import ContentServerChange = api.content.ContentServerChange;
    import ContentServerChangeType = api.content.ContentServerChangeType;
    import BatchContentRequest = api.content.BatchContentRequest;
    import TreeNodesOfContentPath = api.content.TreeNodesOfContentPath;
    import ContentChangeResult = api.content.ContentChangeResult;
    import ContentId = api.content.ContentId;
    import DetailsPanel = app.view.detail.DetailsPanel;
    import NonMobileDetailsPanelsToggleButton = app.view.detail.NonMobileDetailsPanelsToggleButton;
    import NonMobileDetailsPanelsToggleButtonBuilder = app.view.detail.NonMobileDetailsPanelsToggleButtonBuilder;

    export class ContentBrowsePanel extends api.app.browse.BrowsePanel<ContentSummary> {

        private browseActions: app.browse.action.ContentTreeGridActions;

        private toolbar: ContentBrowseToolbar;

        private contentTreeGrid: app.browse.ContentTreeGrid;

        private contentFilterPanel: app.browse.filter.ContentBrowseFilterPanel;

        private contentBrowseItemPanel: ContentBrowseItemPanel;

        private mobileContentItemStatisticsPanel: app.view.MobileContentItemStatisticsPanel;

        private mobileBrowseActions: app.browse.action.MobileContentTreeGridActions;

        private floatingDetailsPanel: DetailsPanel;

        private defaultDockedDetailsPanel: DetailsPanel;

        constructor() {

            this.contentTreeGrid = new app.browse.ContentTreeGrid();

            this.contentBrowseItemPanel = components.detailPanel = new ContentBrowseItemPanel();

            this.contentFilterPanel = new app.browse.filter.ContentBrowseFilterPanel();

            this.browseActions = <app.browse.action.ContentTreeGridActions>this.contentTreeGrid.getContextMenu().getActions();

            this.toolbar = new ContentBrowseToolbar(this.browseActions);

            this.defaultDockedDetailsPanel = DetailsPanel.create().setUseSplitter(false).build();

            super({
                browseToolbar: this.toolbar,
                treeGrid: this.contentTreeGrid,
                browseItemPanel: this.contentBrowseItemPanel,
                filterPanel: this.contentFilterPanel,
                hasDetailsPanel: true
            });

            var showMask = () => {
                if (this.isVisible()) {
                    this.contentTreeGrid.mask();
                }
            };
            this.contentFilterPanel.onSearch(showMask);
            this.contentFilterPanel.onReset(showMask);
            this.contentFilterPanel.onRefresh(showMask);

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

            this.onRendered((event) => {
                this.appendChild(this.floatingDetailsPanel);
                this.initItemStatisticsPanelForMobile();
            });
        }

        protected initNonMobileDetailsPanels() {

            var controlButtonBuilder = NonMobileDetailsPanelsToggleButton.create();
            this.initFloatingDetailsPanel(controlButtonBuilder);
            this.initSplitPanelWithDockedDetails(controlButtonBuilder);

            var nonMobileDetailsPanelsToggleButton = controlButtonBuilder.build();
            if (nonMobileDetailsPanelsToggleButton.requiresFloatingPanelDueToShortWidth()) {
                nonMobileDetailsPanelsToggleButton.hideDockedDetailsPanel();
            }
            nonMobileDetailsPanelsToggleButton.ensureButtonHasCorrectState();
            this.subscribeDetailsPanelsOnEvents(nonMobileDetailsPanelsToggleButton);

            this.toolbar.appendChild(nonMobileDetailsPanelsToggleButton);
        }

        private subscribeDetailsPanelsOnEvents(nonMobileDetailsPanelsToggleButton: NonMobileDetailsPanelsToggleButton) {

            this.getTreeGrid().onSelectionChanged((currentSelection: TreeNode<Object>[], fullSelection: TreeNode<Object>[]) => {
                var browseItems: api.app.browse.BrowseItem<ContentSummary>[] = this.getBrowseItemPanel().getItems();
                if (browseItems.length == 1) {
                    var item: api.app.view.ViewItem<ContentSummary> = browseItems[0].toViewItem();

                    this.floatingDetailsPanel.unMakeLookEmpty();
                    this.floatingDetailsPanel.setItem(item);

                    this.defaultDockedDetailsPanel.unMakeLookEmpty();
                    this.defaultDockedDetailsPanel.setItem(item);
                } else {

                    this.floatingDetailsPanel.makeLookEmpty();
                    this.defaultDockedDetailsPanel.makeLookEmpty();
                }
            });

            ResponsiveManager.onAvailableSizeChanged(this.getFilterAndContentGridAndBrowseSplitPanel(), (item: ResponsiveItem) => {
                nonMobileDetailsPanelsToggleButton.handleResizeEvent();
            });

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                if (ResponsiveRanges._540_720.isFitOrBigger(item.getOldRangeValue()) &&
                    item.isInRangeOrSmaller(ResponsiveRanges._360_540)) {
                    nonMobileDetailsPanelsToggleButton.hideActivePanel();
                }
            });

        }

        private initSplitPanelWithDockedDetails(controlButtonBuilder: NonMobileDetailsPanelsToggleButtonBuilder) {

            var contentPanelsAndDetailPanel: api.ui.panel.SplitPanel = new api.ui.panel.SplitPanelBuilder(this.getFilterAndContentGridAndBrowseSplitPanel(),
                this.defaultDockedDetailsPanel).
                setAlignment(api.ui.panel.SplitPanelAlignment.VERTICAL).
                setSecondPanelSize(280, api.ui.panel.SplitPanelUnit.PIXEL).
                setSecondPanelMinSize(280, api.ui.panel.SplitPanelUnit.PIXEL).
                setAnimationDelay(600).
                build();

            contentPanelsAndDetailPanel.addClass("split-panel-with-details");
            contentPanelsAndDetailPanel.setSecondPanelSize(280, api.ui.panel.SplitPanelUnit.PIXEL);

            this.appendChild(contentPanelsAndDetailPanel);

            this.defaultDockedDetailsPanel.makeLookEmpty();

            controlButtonBuilder.setSplitPanelWithGridAndDetails(contentPanelsAndDetailPanel);
            controlButtonBuilder.setDefaultDetailsPanel(this.defaultDockedDetailsPanel);
        }

        private initFloatingDetailsPanel(controlButtonBuilder: NonMobileDetailsPanelsToggleButtonBuilder) {

            this.floatingDetailsPanel = DetailsPanel.create().build();
            this.floatingDetailsPanel.makeLookEmpty();

            controlButtonBuilder.setFloatingDetailsPanel(this.floatingDetailsPanel);
        }

        private initItemStatisticsPanelForMobile() {
            this.mobileBrowseActions = new app.browse.action.MobileContentTreeGridActions(this.contentTreeGrid);
            this.mobileContentItemStatisticsPanel = new app.view.MobileContentItemStatisticsPanel(this.mobileBrowseActions);

            api.content.TreeGridItemClickedEvent.on((event) => {
                var browseItems: api.app.browse.BrowseItem<ContentSummary>[] = this.getBrowseItemPanel().getItems();
                if (browseItems.length == 1) {
                    new api.content.page.IsRenderableRequest(new api.content.ContentId(browseItems[0].getId())).sendAndParse().
                        then((renderable: boolean) => {
                            var item: api.app.view.ViewItem<ContentSummary> = browseItems[0].toViewItem();
                            item.setRenderable(renderable);
                            this.mobileBrowseActions.updateActionsEnabledState(browseItems);
                        });
                }
            });

            this.appendChild(this.mobileContentItemStatisticsPanel);
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

            api.content.ContentChildOrderUpdatedEvent.on((event) => {
                //this.handleChildOrderUpdated(event);
            });

            ToggleSearchPanelEvent.on(() => {
                this.toggleFilterPanel();
            });

            app.create.NewMediaUploadEvent.on((event) => {
                this.handleNewMediaUpload(event);
            });

            /*
             * ContentServerEvent handlers for the new API.
             * TODO: Replace all of the old events with the new one.
             */
            var handler = this.contentServerEventHandler.bind(this);

            ContentServerEvent.on(handler);
            this.onRemoved(() => {
                ContentServerEvent.un(handler);
            });
        }

        private contentServerEventHandler(event: ContentServerEvent) {
            // TODO: IMPORTANT! Publishing multiple items may generate repeated event.
            /*
             * When handling the DELETE-CREATE sequence, we need to remember the removed nodes,
             * because this sequence only appears when node is updated or moved (path is changed).
             * We need to restore the expanded nodes and common tree structure after that.
             * The ContentPath of all of the children elements need to be updated too.
             */

            var changes = event.getContentChanges();

            var deferred = wemQ.defer<ContentChangeResult>(),
                promise = <wemQ.Promise<ContentChangeResult>>deferred.promise;

            changes.forEach((change: ContentServerChange) => {
                switch (change.getChangeType()) {
                case ContentServerChangeType.CREATE:
                    promise = this.handleContentCreated(change, promise, changes);
                    break;
                case ContentServerChangeType.UPDATE:
                    promise = this.handleContentUpdated(change, promise, changes);
                    break;
                case ContentServerChangeType.RENAME:
                    promise = this.handleContentRenamed(change, promise, changes);
                    break;
                case ContentServerChangeType.DELETE:
                    promise = this.handleContentDeleted(change, promise, changes);
                    break;
                case ContentServerChangeType.PENDING:
                    promise = this.handleContentPending(change, promise, changes);
                    break;
                case ContentServerChangeType.DUPLICATE:
                    // same logic as for creation
                    promise = this.handleContentCreated(change, promise, changes);
                    break;
                case ContentServerChangeType.PUBLISH:
                    promise = this.handleContentPublished(change, promise, changes);
                    break;
                case ContentServerChangeType.SORT:
                    promise = this.handleContentSorted(change, promise, changes);
                    break;
                case ContentServerChangeType.UNKNOWN:
                    break;
                default:
                    // MOVE event is a combination of DELETE and CREATE
                }
            });

            deferred.resolve(null);
        }

        private handleContentCreated(change: ContentServerChange, promise: wemQ.Promise<any>,
                                     changes: ContentServerChange[]): wemQ.Promise<any> {
            promise = promise.then((result: ContentChangeResult) => {

                var createResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(change.getContentPaths(), true);

                return ContentSummaryAndCompareStatusFetcher.
                    fetchByPaths(createResult.map((el) => {
                        return el.getAltPath();
                    })).
                    then((data: ContentSummaryAndCompareStatus[]) => {
                        var isFiltered = this.contentTreeGrid.getRoot().isFiltered(),
                            nodes: TreeNode<ContentSummaryAndCompareStatus>[] = [];

                        data.forEach((el) => {
                            for (var i = 0; i < createResult.length; i++) {
                                if (el.getContentSummary().getPath().isChildOf(createResult[i].getPath())) {
                                    if (result && result.getChangeType() === ContentServerChangeType.RENAME) {
                                        var premerged = result.getResult().map((el) => {
                                            return el.getNodes();
                                        });
                                        // merge array of nodes arrays
                                        nodes = nodes.concat.apply(nodes, premerged);
                                        nodes.forEach((node) => {
                                            if (node.getDataId() === el.getId()) {
                                                node.setData(el);
                                                node.clearViewers();
                                                this.contentTreeGrid.xUpdatePathsInChildren(node);
                                            }
                                        });
                                    } else {
                                        this.contentTreeGrid.xAppendContentNodes(
                                            createResult[i].getNodes().map((node) => {
                                                return new api.content.TreeNodeParentOfContent(el, node);
                                            }),
                                            !isFiltered
                                        ).then((results) => {
                                                nodes = nodes.concat(results);
                                            });
                                    }
                                    break;
                                }
                            }
                        });

                        // Read the notice in the header of the `contentServerEventHandler()` method
                        if (result && result.getChangeType() === ContentServerChangeType.DELETE) {
                            var ids = result.getResult().map((el) => {
                                return el.getId();
                            });
                            nodes.forEach((node) => {
                                var index = ids.indexOf(node.getDataId());
                                if (index >= 0) {
                                    this.contentTreeGrid.xPopulateWithChildren(result.getResult()[index].getNodes()[0], node);
                                }
                            });
                        }
                        this.contentTreeGrid.initAndRender();

                        isFiltered = true;
                        if (isFiltered) {
                            this.setFilterPanelRefreshNeeded(true);
                            window.setTimeout(() => {
                                this.refreshFilter();
                            }, 1000);
                        }
                    });
            });
            return promise;
        }

        private handleContentUpdated(change: ContentServerChange, promise: wemQ.Promise<any>,
                                     changes: ContentServerChange[]): wemQ.Promise<any> {

            if (changes.length === 1) {
                promise = promise.then((result: ContentChangeResult) => {

                    var updateResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(change.getContentPaths());

                    var ids: ContentId[] = [];
                    updateResult.forEach((el) => {
                        ids = ids.concat(el.getNodes().map((node) => {
                            return node.getData().getContentId();
                        }));
                    });
                    return ContentSummaryAndCompareStatusFetcher.
                        fetchByIds(ids).
                        then((data: ContentSummaryAndCompareStatus[]) => {
                            var results = [];
                            data.forEach((el) => {
                                for (var i = 0; i < updateResult.length; i++) {
                                    if (updateResult[i].getId() === el.getId()) {
                                        updateResult[i].updateNodeData(el);
                                        this.updateStatisticsPreview(el); // update preview item

                                        var viewItem = this.getBrowseItemPanel().getItems()[0].toViewItem();
                                        this.updateDetailsPanels(el.getContentId(), el.getCompareStatus(), viewItem);

                                        results.push(updateResult[i]);

                                        break;
                                    }
                                }
                            });
                            this.browseActions.updateActionsEnabledState(this.getBrowseItemPanel().getItems()); // update actions state in case of permission changes
                            this.mobileBrowseActions.updateActionsEnabledState(this.getBrowseItemPanel().getItems());

                            return this.contentTreeGrid.xPlaceContentNodes(results);
                        });
                });
            }

            return promise;
        }

        private handleContentRenamed(change: ContentServerChange, promise: wemQ.Promise<any>,
                                     changes: ContentServerChange[]): wemQ.Promise<any> {
            promise = promise.then((result: ContentChangeResult) => {
                var renameResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(change.getContentPaths());

                return new ContentChangeResult(ContentServerChangeType.RENAME, renameResult);
            });
            return promise;
        }

        private handleContentDeleted(change: ContentServerChange, promise: wemQ.Promise<any>,
                                     changes: ContentServerChange[]): wemQ.Promise<any> {
            promise = promise.then((result: ContentChangeResult) => {
                // Do not remove renamed elements
                if (result && result.getChangeType() === ContentServerChangeType.RENAME) {
                    return result;
                }

                var deleteResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(change.getContentPaths());
                var nodes = deleteResult.map((el) => {
                    return el.getNodes();
                });
                var merged = [];
                // merge array of nodes arrays
                merged = merged.concat.apply(merged, nodes);

                merged.forEach((node: TreeNode<ContentSummaryAndCompareStatus>) => {
                    if (node.getData() && node.getData().getContentSummary()) {
                        new api.content.ContentDeletedEvent(node.getData().getContentSummary().getContentId()).fire();
                        this.updateDetailsPanels(node.getData().getContentId(), node.getData().getCompareStatus());
                    }
                });

                this.contentTreeGrid.xDeleteContentNodes(merged);

                var isFiltered = this.contentTreeGrid.getRoot().isFiltered();
                isFiltered = true;
                if (isFiltered) {
                    this.setFilterPanelRefreshNeeded(true);
                    window.setTimeout(() => {
                        this.refreshFilter();
                    }, 1000);
                }

                return new ContentChangeResult(ContentServerChangeType.DELETE, deleteResult);
            });
            return promise;
        }

        private handleContentPending(change: ContentServerChange, promise: wemQ.Promise<any>,
                                     changes: ContentServerChange[]): wemQ.Promise<any> {
            promise = promise.then(() => {

                var pendingResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(change.getContentPaths());

                return ContentSummaryAndCompareStatusFetcher.
                    fetchByPaths(pendingResult.
                        map((el) => {
                            return (el.getNodes().length > 0 && el.getNodes()[0].getData())
                                ? el.getNodes()[0].getData().getContentSummary().getPath()
                                : null;
                        }).
                        filter((el) => {
                            return el !== null;
                        })
                ).then((data: ContentSummaryAndCompareStatus[]) => {
                        data.forEach((el) => {
                            for (var i = 0; i < pendingResult.length; i++) {
                                if (pendingResult[i].getId() === el.getId()) {
                                    pendingResult[i].updateNodeData(el);
                                    this.updateDetailsPanels(el.getContentId(), el.getCompareStatus());
                                    break;
                                }
                            }
                        });
                        this.contentTreeGrid.invalidate();
                    });
            });
            return promise;
        }

        private handleContentPublished(change: ContentServerChange, promise: wemQ.Promise<any>,
                                       changes: ContentServerChange[]): wemQ.Promise<any> {
            promise = promise.then(() => {
                var publishResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(change.getContentPaths());

                return ContentSummaryAndCompareStatusFetcher.
                    fetchByPaths(publishResult.map((el) => {
                        return el.getPath();
                    })).
                    then((data: ContentSummaryAndCompareStatus[]) => {
                        data.forEach((el) => {
                            for (var i = 0; i < publishResult.length; i++) {
                                if (publishResult[i].getId() === el.getId()) {
                                    new api.content.ContentPublishedEvent(new api.content.ContentId(el.getId())).fire();
                                    publishResult[i].updateNodeData(el);
                                    this.updateDetailsPanels(el.getContentId(), el.getCompareStatus());
                                    break;
                                }
                            }
                        });
                        this.contentTreeGrid.invalidate();
                    });
            });
            return promise;
        }

        private handleContentSorted(change: ContentServerChange, promise: wemQ.Promise<any>,
                                    changes: ContentServerChange[]): wemQ.Promise<any> {
            promise = promise.then((result: ContentChangeResult) => {
                var sortResult: TreeNodesOfContentPath[] = this.contentTreeGrid.findByPaths(change.getContentPaths());

                var nodes = sortResult.map((el) => {
                    return el.getNodes();
                });
                var merged = [];
                // merge array of nodes arrays
                merged = merged.concat.apply(merged, nodes);

                this.contentTreeGrid.xSortNodesChildren(merged).then(() => this.contentTreeGrid.invalidate());
            });
            return promise;
        }

        private handleNewMediaUpload(event: app.create.NewMediaUploadEvent) {
            event.getUploadItems().forEach((item: UploadItem<ContentSummary>) => {
                this.contentTreeGrid.appendUploadNode(item);
            });
        }

        private updateStatisticsPreview(el: ContentSummaryAndCompareStatus) {
            var content = el.getContentSummary();
            var previewItem = this.getBrowseItemPanel().getStatisticsItem();
            var previewItemPath = previewItem.getPath();

            if (!!content && content.getPath().toString() === previewItemPath) {
                new api.content.page.IsRenderableRequest(el.getContentId()).sendAndParse().
                    then((renderable: boolean) => {
                        var item = new BrowseItem<ContentSummary>(content).
                            setId(content.getId()).
                            setDisplayName(content.getDisplayName()).
                            setPath(content.getPath().toString()).
                            setIconUrl(new ContentIconUrlResolver().setContent(content).resolve()).
                            setRenderable(renderable);
                        this.getBrowseItemPanel().setStatisticsItem(item);
                    });
            }
        }

        private updateDetailsPanels(contentId: ContentId, status: CompareStatus, viewItem?: api.app.view.ViewItem<ContentSummary>) {
            if (viewItem) {
                this.defaultDockedDetailsPanel.setItem(viewItem);
                this.floatingDetailsPanel.setItem(viewItem);
                this.mobileContentItemStatisticsPanel.setItem(viewItem);
            }
            this.updateDetailsPanelContentStatus(this.defaultDockedDetailsPanel, contentId, status);
            this.updateDetailsPanelContentStatus(this.floatingDetailsPanel, contentId, status);
            this.updateDetailsPanelContentStatus(this.mobileContentItemStatisticsPanel.getDetailsPanel(), contentId, status);
        }

        private updateDetailsPanelContentStatus(detailsPanel: DetailsPanel, contentId: ContentId, status: CompareStatus) {
            if (contentId && contentId.equals(detailsPanel.getItem().getModel().getContentId())) {
                detailsPanel.setContentStatus(status);
            }
        }
    }
}