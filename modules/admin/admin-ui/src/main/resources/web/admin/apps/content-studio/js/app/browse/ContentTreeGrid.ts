module app.browse {

    import Element = api.dom.Element;
    import ElementHelper = api.dom.ElementHelper;

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
    import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;

    import ContentResponse = api.content.ContentResponse;
    import ContentSummary = api.content.ContentSummary;
    import ContentPath = api.content.ContentPath;
    import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
    import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;
    import CompareContentRequest = api.content.CompareContentRequest;
    import CompareContentResults = api.content.CompareContentResults;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentSummaryAndCompareStatusFetcher = api.content.ContentSummaryAndCompareStatusFetcher;
    import TreeNodesOfContentPath = api.content.TreeNodesOfContentPath;
    import TreeNodeParentOfContent = api.content.TreeNodeParentOfContent;

    import ContentBrowseSearchEvent = app.browse.filter.ContentBrowseSearchEvent;
    import ContentBrowseResetEvent = app.browse.filter.ContentBrowseResetEvent;
    import ContentBrowseRefreshEvent = app.browse.filter.ContentBrowseRefreshEvent;
    import ContentVersionSetEvent = api.content.event.ActiveContentVersionSetEvent;

    import ContentQueryResult = api.content.ContentQueryResult;
    import ContentSummaryJson = api.content.json.ContentSummaryJson;
    import ContentQueryRequest = api.content.ContentQueryRequest;

    import ContentTreeGridActions = app.browse.action.ContentTreeGridActions;

    import CompareStatus = api.content.CompareStatus;

    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;

    export class ContentTreeGrid extends TreeGrid<ContentSummaryAndCompareStatus> {

        static MAX_FETCH_SIZE: number = 10;

        private filterQuery: api.content.query.ContentQuery;

        constructor() {
            var nameColumn = new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                setName("Name").
                setId("displayName").
                setField("contentSummary.displayName").
                setMinWidth(130).
                setFormatter(this.nameFormatter).
                build();
            var compareStatusColumn = new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                setName("CompareStatus").
                setId("compareStatus").
                setField("compareStatus").
                setFormatter(this.statusFormatter).
                setCssClass("status").
                setMinWidth(75).
                setMaxWidth(75).
                build();
            var orderColumn = new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                setName("Order").
                setId("order").
                setField("contentSummary.order").
                setCssClass("order").
                setMinWidth(25).
                setMaxWidth(40).
                setFormatter(this.orderFormatter).
                build();
            var modifiedTimeColumn = new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                setName("ModifiedTime").
                setId("modifiedTime").
                setField("contentSummary.modifiedTime").
                setCssClass("modified").
                setMinWidth(135).
                setMaxWidth(135).
                setFormatter(DateTimeFormatter.format).
                build();

            super(new TreeGridBuilder<ContentSummaryAndCompareStatus>().
                    setColumns([
                        nameColumn,
                        orderColumn,
                        compareStatusColumn,
                        modifiedTimeColumn
                    ]).
                    setShowContextMenu(new TreeGridContextMenu(new ContentTreeGridActions(this))).
                    setPartialLoadEnabled(true).
                    setLoadBufferSize(20). // rows count
                    prependClasses("content-tree-grid")
            );

            let updateColumns = (force?: boolean) => {
                if (force) {
                    var width = this.getEl().getWidth();

                    if (ResponsiveRanges._240_360.isFitOrSmaller(width)) {
                        this.getGrid().setColumns([nameColumn, orderColumn]);
                    } else if (ResponsiveRanges._360_540.isFitOrSmaller(width)) {
                        this.getGrid().setColumns([nameColumn, orderColumn, compareStatusColumn]);
                    } else {
                        if (ResponsiveRanges._540_720.isFitOrSmaller(width)) {
                            modifiedTimeColumn.setMaxWidth(90);
                            modifiedTimeColumn.setFormatter(DateTimeFormatter.formatNoTimestamp);
                        } else {
                            modifiedTimeColumn.setMaxWidth(135);
                            modifiedTimeColumn.setFormatter(DateTimeFormatter.format);
                        }
                        this.getGrid().setColumns([nameColumn, orderColumn, compareStatusColumn, modifiedTimeColumn]);
                    }
                } else {
                    this.getGrid().resizeCanvas();
                }
            };

            var onBecameActive = (active: boolean) => {
                if (active) {
                    updateColumns(true);
                    this.unActiveChanged(onBecameActive);
                }
            };
            // update columns when grid becomes active for the first time
            this.onActiveChanged(onBecameActive);

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                if (this.isInRenderingView()) {
                    updateColumns(item.isRangeSizeChanged());
                }
            });

            this.getGrid().subscribeOnClick((event, data) => {
                var elem = new ElementHelper(event.target);
                if (elem.hasClass("sort-dialog-trigger")) {
                    this.sortIconClickCallback();
                }
            });

            this.getGrid().subscribeOnDblClick((event, data) => {
                if (this.isActive()) {
                    var node = this.getGrid().getDataView().getItem(data.row);
                    /*
                     * Empty node double-clicked. Additional %MAX_FETCH_SIZE%
                     * nodes will be loaded and displayed. If the any other
                     * node is clicked, edit event will be triggered by default.
                     */
                    if (!!this.getDataId(node.getData())) { // default event
                        new api.content.event.EditContentEvent([node.getData()]).fire();
                    }
                }
            });

            /*
             * Filter (search) events.
             */
            ContentBrowseSearchEvent.on((event) => {
                var contentQueryResult = <ContentQueryResult<ContentSummary,ContentSummaryJson>>event.getContentQueryResult();
                var contentSummaries = contentQueryResult.getContents(),
                    compareRequest = CompareContentRequest.fromContentSummaries(contentSummaries);
                this.filterQuery = event.getContentQuery();
                compareRequest.sendAndParse().then((compareResults: CompareContentResults) => {
                    var contents: ContentSummaryAndCompareStatus[] = ContentSummaryAndCompareStatusFetcher.updateCompareStatus(contentSummaries,
                        compareResults);
                    var metadata = contentQueryResult.getMetadata();
                    if (metadata.getTotalHits() > metadata.getHits()) {
                        contents.push(new ContentSummaryAndCompareStatus());
                    }
                    this.filter(contents);
                    this.getRoot().getCurrentRoot().setMaxChildren(metadata.getTotalHits());
                    this.notifyLoaded();
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            });

            ContentBrowseResetEvent.on((event) => {
                this.resetFilter();
            });
            ContentBrowseRefreshEvent.on((event) => {
                this.notifyLoaded();
            });
            ContentVersionSetEvent.on((event: ContentVersionSetEvent) => {
                this.updateContentNode(event.getContentId());
            });
        }

        private orderFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
            var wrapper = new api.dom.SpanEl();

            if (!api.util.StringHelper.isBlank(value)) {
                wrapper.getEl().setTitle(value);
            }

            if (node.getData().getContentSummary()) {
                var childOrder = node.getData().getContentSummary().getChildOrder();
                var icon;
                if (!childOrder.isDefault()) {
                    if (!childOrder.isManual()) {
                        if (childOrder.isDesc()) {
                            icon = new api.dom.DivEl("icon-arrow-up4 sort-dialog-trigger");
                        } else {
                            icon = new api.dom.DivEl("icon-arrow-down4 sort-dialog-trigger");
                        }
                    } else {
                        icon = new api.dom.DivEl("icon-menu3 sort-dialog-trigger");
                    }
                    wrapper.getEl().setInnerHtml(icon.toString(), false);
                }
            }
            return wrapper.toString();
        }

        private sortIconClickCallback() {
            new app.browse.SortContentEvent(this.getSelectedDataList()).fire();
        }

        private statusFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {

            var data = node.getData(),
                status,
                statusEl = new api.dom.SpanEl();

            if (!!data.getContentSummary()) {   // default node
                var compareStatus: CompareStatus = CompareStatus[CompareStatus[value]];

                status = api.content.CompareStatusFormatter.formatStatus(compareStatus);

                if (!!CompareStatus[value]) {
                    statusEl.addClass(CompareStatus[value].toLowerCase().replace("_", "-") || "unknown");
                }

                statusEl.getEl().setText(status);
            } else if (!!data.getUploadItem()) {   // uploading node
                status = new api.ui.ProgressBar(data.getUploadItem().getProgress());
                statusEl.appendChild(status);
            }

            return statusEl.toString();
        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
            var data = node.getData();
            if (!!data.getContentSummary() || !!data.getUploadItem()) {  // default node or upload node

                var viewer = <ContentSummaryAndCompareStatusViewer> node.getViewer("name");
                if (!viewer) {
                    viewer = new ContentSummaryAndCompareStatusViewer();
                    node.setViewer("name", viewer);
                }
                viewer.setObject(node.getData(), node.calcLevel() > 1);
                return viewer.toString();

            } else { // `load more` node
                var content = new api.dom.DivEl("children-to-load"),
                    parent = node.getParent();
                content.setHtml((parent.getMaxChildren() - parent.getChildren().length + 1) + " children left to load.");

                return content.toString();
            }
        }


        fetch(node: TreeNode<ContentSummaryAndCompareStatus>, dataId?: string): wemQ.Promise<ContentSummaryAndCompareStatus> {
            return this.fetchById(node.getData().getContentId());
        }

        private fetchById(id: api.content.ContentId): wemQ.Promise<ContentSummaryAndCompareStatus> {
            return ContentSummaryAndCompareStatusFetcher.fetch(id);
        }

        fetchChildren(parentNode?: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<ContentSummaryAndCompareStatus[]> {
            var parentContentId: api.content.ContentId = null;
            if (parentNode) {
                parentContentId = parentNode.getData() ? parentNode.getData().getContentId() : parentContentId;
            } else {
                parentNode = this.getRoot().getCurrentRoot();
            }
            var from = parentNode.getChildren().length;
            if (from > 0 && !parentNode.getChildren()[from - 1].getData().getContentSummary()) {
                parentNode.getChildren().pop();
                from--;
            }

            if (!this.isFiltered() || parentNode != this.getRoot().getCurrentRoot()) {
                return ContentSummaryAndCompareStatusFetcher.fetchChildren(parentContentId, from, ContentTreeGrid.MAX_FETCH_SIZE).
                    then((data: ContentResponse<ContentSummaryAndCompareStatus>) => {
                        // TODO: Will reset the ids and the selection for child nodes.
                        var contents = parentNode.getChildren().map((el) => {
                            return el.getData();
                        }).slice(0, from).concat(data.getContents());
                        var meta = data.getMetadata();
                        parentNode.setMaxChildren(meta.getTotalHits());
                        if (from + meta.getHits() < meta.getTotalHits()) {
                            contents.push(new ContentSummaryAndCompareStatus());
                        }
                        return contents;
                    });
            } else {
                this.filterQuery.setFrom(from);
                this.filterQuery.setSize(ContentTreeGrid.MAX_FETCH_SIZE);
                return new ContentQueryRequest<ContentSummaryJson,ContentSummary>(this.filterQuery).
                    setExpand(api.rest.Expand.SUMMARY).
                    sendAndParse().
                    then((contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {
                        var contentSummaries = contentQueryResult.getContents();
                        var compareRequest = CompareContentRequest.fromContentSummaries(contentSummaries);
                        return compareRequest.sendAndParse().
                            then((compareResults: CompareContentResults) => {
                                var list = parentNode.getChildren().map((el) => {
                                    return el.getData();
                                }).slice(0, from).concat(ContentSummaryAndCompareStatusFetcher.updateCompareStatus(contentSummaries,
                                    compareResults));
                                var meta = contentQueryResult.getMetadata();
                                if (from + meta.getHits() < meta.getTotalHits()) {
                                    list.push(new ContentSummaryAndCompareStatus());
                                }
                                parentNode.setMaxChildren(meta.getTotalHits());
                                return list;
                            });
                    });
            }
        }

        fetchChildrenIds(parentNode?: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<ContentSummary[]> {
            var parentContentId: api.content.ContentId = null;
            if (parentNode) {
                parentContentId = parentNode.getData() ? parentNode.getData().getContentId() : parentContentId;
            } else {
                parentNode = this.getRoot().getCurrentRoot();
            }
            var size = parentNode.getChildren().length;
            if (size > 0 && !parentNode.getChildren()[size - 1].getData().getContentSummary()) {
                parentNode.getChildren().pop();
                size--;
            }

            if (!this.isFiltered() || parentNode != this.getRoot().getCurrentRoot()) {
                return ContentSummaryAndCompareStatusFetcher.fetchChildrenIds(parentContentId, 0, size + 1).
                    then((response: ContentResponse<ContentSummary>) => {
                        return response.getContents();
                    });
            } else {
                this.filterQuery.setFrom(0);
                this.filterQuery.setSize(size + 1);
                return new ContentQueryRequest<ContentSummaryJson,ContentSummary>(this.filterQuery).
                    setExpand(api.rest.Expand.SUMMARY).
                    sendAndParse().
                    then((contentQueryResult: ContentQueryResult<ContentSummary,ContentSummaryJson>) => {
                        return contentQueryResult.getContents();
                    });
            }
        }

        hasChildren(data: ContentSummaryAndCompareStatus): boolean {
            return data.hasChildren();
        }

        getDataId(data: ContentSummaryAndCompareStatus): string {
            return data.getId();
        }

        deleteNodes(dataList: ContentSummaryAndCompareStatus[]): void {
            var root = this.getRoot().getCurrentRoot(),
                node: TreeNode<ContentSummaryAndCompareStatus>;

            // Do not remove the items, that is not new and switched to "PENDING_DELETE"
            dataList = dataList.filter((data) => {
                node = root.findNode(this.getDataId(data));
                if (node.getData().getCompareStatus() !== CompareStatus.NEW) {
                    node.clearViewers();
                    return false;
                }
                return true;
            });
            super.deleteNodes(dataList);
        }

        updateContentNode(contentId: api.content.ContentId) {
            var root = this.getRoot().getCurrentRoot();
            var treeNode = root.findNode(contentId.toString());
            if (treeNode) {
                var content = treeNode.getData();
                this.updateNode(ContentSummaryAndCompareStatus.fromContentSummary(content.getContentSummary()));
            }
        }

        appendContentNode(contentId: api.content.ContentId, nextToSelection?: boolean) {

            this.fetchById(contentId)
                .then((data: ContentSummaryAndCompareStatus) => {
                    this.appendNode(data, nextToSelection);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                });
        }

        appendUploadNode(item: api.ui.uploader.UploadItem<ContentSummary>) {

            var data = ContentSummaryAndCompareStatus.fromUploadItem(item);

            var parent: TreeNode<ContentSummaryAndCompareStatus> = this.getRoot().getCurrentSelection()[0];

            this.appendNode(data, false).then(() => {
                if (parent) {
                    var parentData = parent.getData();
                    var contentSummary = new ContentSummaryBuilder(parentData.getContentSummary()).setHasChildren(true).build();
                    this.updateNode(parentData.setContentSummary(contentSummary));
                    this.expandNode(parent);
                }
            }).done();

            item.onProgress((progress: number) => {
                this.invalidate();
            });
            item.onUploaded((model: ContentSummary) => {
                var nodeToRemove = this.getRoot().getCurrentRoot().findNode(item.getId());
                if (nodeToRemove) {
                    nodeToRemove.remove();
                    this.invalidate();
                }

                api.notify.showFeedback(data.getContentSummary().getType().toString() + " \"" + item.getName() + "\" created successfully");
            });
            item.onFailed(() => {
                this.deleteNode(data);
            })
        }

        refreshNodeData(parentNode: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<TreeNode<ContentSummaryAndCompareStatus>> {
            return ContentSummaryAndCompareStatusFetcher.fetch(parentNode.getData().getContentId()).then((content: ContentSummaryAndCompareStatus) => {
                parentNode.setData(content);
                this.refreshNode(parentNode);
                return parentNode;
            });
        }

        sortNodeChildren(node: TreeNode<ContentSummaryAndCompareStatus>) {
            var rootNode = this.getRoot().getCurrentRoot();
            if (node != rootNode) {
                if (node.hasChildren()) {
                    node.setChildren([]);
                    node.setMaxChildren(0);

                    this.fetchChildren(node)
                        .then((dataList: ContentSummaryAndCompareStatus[]) => {
                            var parentNode = this.getRoot().getCurrentRoot().findNode(node.getDataId());
                            parentNode.setChildren(this.dataToTreeNodes(dataList, parentNode));
                            var rootList = this.getRoot().getCurrentRoot().treeToList();
                            this.initData(rootList);
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).done();
                }
            }
        }

        /*
         * New API methods
         */
        findByPaths(paths: api.content.ContentPath[], useParent: boolean = false): TreeNodesOfContentPath[] {
            var root = this.getRoot().getDefaultRoot().treeToList(false, false),
                filter = this.getRoot().getFilteredRoot().treeToList(false, false),
                all: TreeNode<ContentSummaryAndCompareStatus>[] = root.concat(filter),
                result: TreeNodesOfContentPath[] = [];

            for (var i = 0; i < paths.length; i++) {
                var node = useParent
                    ? new TreeNodesOfContentPath(paths[i].getParentPath(), paths[i])
                    : new TreeNodesOfContentPath(paths[i]);
                if (useParent && node.getPath().isRoot()) {
                    node.getNodes().push(this.getRoot().getDefaultRoot());
                    if (this.isFiltered()) {
                        node.getNodes().push(this.getRoot().getFilteredRoot());
                    }
                } else {
                    for (var j = 0; j < all.length; j++) {
                        var treeNode = all[j],
                            path = (treeNode.getData() && treeNode.getData().getContentSummary())
                                ? treeNode.getData().getContentSummary().getPath()
                            : null;
                        if (path && path.equals(node.getPath())) {
                            node.getNodes().push(treeNode);
                        }
                    }
                }
                if (node.hasNodes()) {
                    result.push(node);
                }
            }

            return result;
        }

        expandTillNodeWithGivenPath(path: ContentPath, startExpandingFromNode?: TreeNode<ContentSummaryAndCompareStatus>) {
            var node: TreeNode<ContentSummaryAndCompareStatus>;
            if (startExpandingFromNode && path.isDescendantOf(startExpandingFromNode.getData().getPath())) {
                node = startExpandingFromNode;
            } else {
                node = this.getRoot().getCurrentRoot();
            }

            // go down and expand path's parents level by level until we reach the desired element within the list of fetched children
            this.expandNodeAndCheckTargetReached(node, path);
        }

        private expandNodeAndCheckTargetReached(nodeToExpand: TreeNode<ContentSummaryAndCompareStatus>, targetPathToExpand: ContentPath) {

            if (!!nodeToExpand.getData() && targetPathToExpand.equals(nodeToExpand.getData().getPath())) {
                return;
            }

            if (nodeToExpand) {
                nodeToExpand.setExpanded(true);

                if (nodeToExpand.hasChildren()) {
                    this.initData(this.getRoot().getCurrentRoot().treeToList());
                    this.updateExpanded();
                    this.expandMoreOrSelectTargetIfReached(nodeToExpand, targetPathToExpand);
                } else {
                    this.mask();
                    this.fetchChildrenData(nodeToExpand)
                        .then((dataList: ContentSummaryAndCompareStatus[]) => {
                            nodeToExpand.setChildren(this.dataToTreeNodes(dataList, nodeToExpand));
                            this.initData(this.getRoot().getCurrentRoot().treeToList());
                            this.updateExpanded();
                            this.expandMoreOrSelectTargetIfReached(nodeToExpand, targetPathToExpand);
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).finally(() => {
                        }).done(() => this.notifyLoaded());
                }
            }
        }

        private expandMoreOrSelectTargetIfReached(nodeToExpand: TreeNode<ContentSummaryAndCompareStatus>, targetPathToExpand: ContentPath) {
            var nextChildPath = targetPathToExpand.getPathAtLevel(!!nodeToExpand.getData()
                ? nodeToExpand.getData().getPath().getLevel() + 1
                : 1);

            var children = nodeToExpand.getChildren();
            for (var i = 0; i < children.length; i++) {
                var child: TreeNode<ContentSummaryAndCompareStatus> = children[i],
                    childPath = child.getData().getPath();
                if (childPath && childPath.equals(targetPathToExpand)) {
                    this.selectNode(child.getDataId());
                    this.scrollToRow(this.getGrid().getDataView().getRowById(child.getId()));
                    break;
                } else if (childPath && childPath.equals(nextChildPath)) {
                    this.expandNodeAndCheckTargetReached(child, targetPathToExpand);
                }
            }
        }

        private fetchChildrenData(parentNode: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<ContentSummaryAndCompareStatus[]> {
            return this.fetchChildren(parentNode);
        }

        xAppendContentNode(relationship: TreeNodeParentOfContent,
                           update: boolean = true): wemQ.Promise<TreeNode<ContentSummaryAndCompareStatus>> {
            var appendedNode = this.dataToTreeNode(relationship.getData(), relationship.getNode()),
                data = relationship.getNode().getData();

            return this.fetchChildrenIds(relationship.getNode()).then((result: ContentSummary[]) => {
                var map = result.map((el) => {
                    return el.getId();
                });
                var index = map.indexOf(appendedNode.getData().getId());

                if (!relationship.getNode().hasParent() ||
                    (data && relationship.getNode().hasChildren()) ||
                    (data && !relationship.getNode().hasChildren() && !data.getContentSummary().hasChildren())) {
                    relationship.getNode().insertChild(appendedNode, index);
                }

                if (data && !data.getContentSummary().hasChildren()) {
                    data.setContentSummary(new ContentSummaryBuilder(data.getContentSummary()).setHasChildren(true).build());
                }

                relationship.getNode().clearViewers();

                if (update) {
                    this.initAndRender();
                }

                return appendedNode;

            });
        }

        xAppendContentNodes(relationships: TreeNodeParentOfContent[],
                            update: boolean = true): wemQ.Promise<any> {
            var nodes = [];

            var parallelPromises: wemQ.Promise<any>[] = [];

            this.xUpdateNodesData(relationships.map((el) => {
                return el.getNode();
            }));

            relationships.forEach((relationship: TreeNodeParentOfContent) => {
                parallelPromises.push(this.xAppendContentNode(relationship, false));
            });


            return wemQ.allSettled(parallelPromises).then((results) => {
                var rootList = this.getRoot().getCurrentRoot().treeToList();
                this.initData(rootList);
                this.invalidate();
                return results;
            });
        }

        xPlaceContentNode(parent: TreeNode<ContentSummaryAndCompareStatus>,
                          child: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<TreeNode<ContentSummaryAndCompareStatus>> {
            return this.fetchChildrenIds(parent).then((result: ContentSummary[]) => {
                var map = result.map((el) => {
                    return el.getId();
                });
                var index = map.indexOf(child.getData().getId());

                if (!parent.hasParent() ||
                    (child.getData() && parent.hasChildren()) ||
                    (child.getData() && !parent.hasChildren() && !child.getData().getContentSummary().hasChildren())) {
                    var parentExpanded = parent.isExpanded();
                    parent.moveChild(child, index);
                    parent.setExpanded(parentExpanded); // in case of a single child it forces its parent to stay expanded
                }

                child.clearViewers();

                return child;

            });
        }

        xPlaceContentNodes(results: TreeNodesOfContentPath[]): wemQ.Promise<any> {
            var parallelPromises: wemQ.Promise<any>[] = [];

            var nodes = results.map((el) => {
                return el.getNodes();
            });
            var merged = [];
            // merge array of nodes arrays
            merged = merged.concat.apply(merged, nodes);

            merged.forEach((node: TreeNode<ContentSummaryAndCompareStatus>) => {
                parallelPromises.push(this.xPlaceContentNode(node.getParent(), node));
            });

            return wemQ.allSettled(parallelPromises).then((results) => {
                var rootList = this.getRoot().getCurrentRoot().treeToList();
                this.initData(rootList);
                this.invalidate();
                return results;
            });
        }

        xDeleteContentNode(node: TreeNode<ContentSummaryAndCompareStatus>,
                           update: boolean = true): TreeNode<ContentSummaryAndCompareStatus> {
            var parentNode = node.getParent();

            node.remove();

            var data = !!parentNode ? parentNode.getData() : null;
            if (data && !parentNode.hasChildren() && data.getContentSummary().hasChildren()) {
                data.setContentSummary(new ContentSummaryBuilder(data.getContentSummary()).setHasChildren(false).build());
            }

            if (update) {
                this.initAndRender();
            }

            return parentNode;
        }

        xDeleteContentNodes(nodes: TreeNode<ContentSummaryAndCompareStatus>[],
                            update: boolean = true) {

            nodes.forEach((node) => {
                this.xDeleteContentNode(node, false);
            });

            if (update) {
                this.initAndRender();
            }
        }

        xPopulateWithChildren(source: TreeNode<ContentSummaryAndCompareStatus>, dest: TreeNode<ContentSummaryAndCompareStatus>) {
            dest.setChildren(source.getChildren());
            dest.setExpanded(source.isExpanded());
            if (dest.getData() && dest.getData().getContentSummary()) {
                dest.getData().setContentSummary(
                    new ContentSummaryBuilder(dest.getData().getContentSummary()).setHasChildren(dest.hasChildren()).build()
                );
                this.xUpdatePathsInChildren(dest);
            }
            dest.clearViewers();
        }

        xUpdatePathsInChildren(node: TreeNode<ContentSummaryAndCompareStatus>) {
            node.getChildren().forEach((child) => {
                var nodeSummary = node.getData() ? node.getData().getContentSummary() : null,
                    childSummary = child.getData() ? child.getData().getContentSummary() : null;
                if (nodeSummary && childSummary) {
                    var path = ContentPath.fromParent(nodeSummary.getPath(), childSummary.getPath().getName());
                    child.getData().setContentSummary(new ContentSummaryBuilder(childSummary).setPath(path).build());
                    child.clearViewers();
                    this.xUpdatePathsInChildren(child);
                }
            });
        }

        /*
         * Updates all of the remaining parents
         * Triggers selection changed event to update toolbar
         */
        xUpdateNodesData(nodes: TreeNode<ContentSummaryAndCompareStatus>[]): TreeNode<ContentSummaryAndCompareStatus>[] {

            nodes = this.xFilterParentNodes(nodes);

            var parallelPromises: wemQ.Promise<any>[] = [];

            nodes.forEach((node) => {
                if (!node.hasChildren()) {
                    if (!!node.getData()) {
                        parallelPromises.push(
                            new api.content.GetContentByIdRequest(node.getData().getContentSummary().getContentId()).
                                sendAndParse().
                                then((content: api.content.Content) => {
                                    node.getData().setContentSummary(content);
                                })
                        );
                    }
                }
            });

            wemQ.all(parallelPromises).spread<void>(() => {
                this.triggerSelectionChangedListeners();
                return wemQ(null);
            }).
                catch((reason: any) => api.DefaultErrorHandler.handle(reason)).
                done();

            return nodes;
        }

        /*
         * Filters only the top parent nodes
         * Parent nodes, that are the children of the other parents will be missed.
         */
        private xFilterParentNodes(nodes: TreeNode<ContentSummaryAndCompareStatus>[]): TreeNode<ContentSummaryAndCompareStatus>[] {

            return nodes.filter((node, index) => {
                var result = true;

                var path = node.getData() && node.getData().getContentSummary()
                    ? node.getData().getContentSummary().getPath()
                    : null;
                if (path) {
                    for (var i = 0; i < nodes.length; i++) {
                        if (index === i) {
                            continue;
                        }

                        var nodePath = nodes[i].getData() && nodes[i].getData().getContentSummary()
                            ? nodes[i].getData().getContentSummary().getPath()
                            : null;
                        if (nodePath && (path.isChildOf(nodePath) || path.toString() === nodePath.toString())) {
                            return false;
                        }
                    }
                }

                return result;
            });
        }

        xSortNodesChildren(nodes: TreeNode<ContentSummaryAndCompareStatus>[]): wemQ.Promise<void> {

            var parallelPromises: wemQ.Promise<any>[] = [];

            nodes.sort((a, b) => {
                return a.getDataId().localeCompare(b.getDataId())
            });

            var groups = [],
                group = [];

            groups.push(group);

            for (var i = 0; i < nodes.length; i++) {
                if (!!group[group.length - 1] &&
                    nodes[i].getDataId() !== group[group.length - 1].getDataId()) {
                    group = [];
                    groups.push(group);
                }

                group.push(nodes[i]);
            }

            groups.forEach((grp: TreeNode<ContentSummaryAndCompareStatus>[]) => {
                if (grp.length > 0) {
                    parallelPromises.push(
                        this.updateNodes(grp[0].getData()).then(() => {
                            var hasChildren = grp[0].hasChildren();
                            grp[0].setChildren([]);
                            return this.fetchChildren(grp[0]).
                                then((dataList: ContentSummaryAndCompareStatus[]) => {
                                    grp.forEach((el) => {
                                        if (hasChildren) {
                                            el.setChildren(this.dataToTreeNodes(dataList, el));
                                        }
                                    });
                                }).catch((reason: any) => {
                                    api.DefaultErrorHandler.handle(reason);
                                });
                        }).then(() => {
                            var rootList = this.getRoot().getCurrentRoot().treeToList();
                            this.initData(rootList);
                        })
                    );
                }
            });

            return wemQ.all(parallelPromises).spread<void>(() => {
                return wemQ(null);
            }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
        }
    }
}