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
    import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;
    import CompareContentRequest = api.content.CompareContentRequest;
    import CompareContentResults = api.content.CompareContentResults;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentSummaryAndCompareStatusFetcher = api.content.ContentSummaryAndCompareStatusFetcher;

    import ContentBrowseSearchEvent = app.browse.filter.ContentBrowseSearchEvent;
    import ContentBrowseResetEvent = app.browse.filter.ContentBrowseResetEvent;
    import ContentBrowseRefreshEvent = app.browse.filter.ContentBrowseRefreshEvent;

    import ContentQueryResult = api.content.ContentQueryResult;
    import ContentSummaryJson = api.content.json.ContentSummaryJson;
    import ContentQueryRequest = api.content.ContentQueryRequest;

    import ContentTreeGridActions = app.browse.action.ContentTreeGridActions;

    import CompareStatus = api.content.CompareStatus;

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
                setField("compareContentResult.compareStatus").
                setFormatter(this.statusFormatter).
                setCssClass("status").
                setMinWidth(75).
                setMaxWidth(75).
                build();
            var modifiedTimeColumn = new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                setName("ModifiedTime").
                setId("modifiedTime").
                setField("contentSummary.modifiedTime").
                setCssClass("modified").
                setMinWidth(150).
                setMaxWidth(170).
                setFormatter(DateTimeFormatter.format).
                build();
            var orderColumn = new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                setName("Order").
                setId("order").
                setField("contentSummary.order").
                setCssClass("order").
                setMinWidth(80).
                setMaxWidth(80).
                setFormatter(this.orderFormatter).
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

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
                if (item.isInRangeOrSmaller(api.ui.responsive.ResponsiveRanges._240_360)) {
                    this.getGrid().setColumns([nameColumn, orderColumn]);
                } else if (item.isInRangeOrSmaller(api.ui.responsive.ResponsiveRanges._360_540)) {
                    this.getGrid().setColumns([nameColumn, orderColumn, modifiedTimeColumn]);
                } else {
                    this.getGrid().setColumns([nameColumn, orderColumn, compareStatusColumn, modifiedTimeColumn]);
                }

                if (item.isInRangeOrSmaller(api.ui.responsive.ResponsiveRanges._540_720)) {
                    modifiedTimeColumn.setMaxWidth(100);
                    modifiedTimeColumn.setFormatter(DateTimeFormatter.formatNoTimestamp);
                } else {
                    modifiedTimeColumn.setMaxWidth(170);
                    modifiedTimeColumn.setFormatter(DateTimeFormatter.format);
                }

                this.getGrid().resizeCanvas();
            });

            this.onSelectionChanged((currentSelection: TreeNode<ContentSummaryAndCompareStatus>[], fullSelection: TreeNode<ContentSummaryAndCompareStatus>[]) => {
                var contentSummaries: ContentSummary[] = currentSelection.map((elem) => {
                    return elem.getData().getContentSummary();
                });
                (<ContentTreeGridActions>this.getContextMenu().getActions()).updateActionsEnabledState(contentSummaries);
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
                        new EditContentEvent([node.getData().getContentSummary()]).fire();
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
                        contents.push(new ContentSummaryAndCompareStatus(null, null));
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
        }

        private typeFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
            var wrapper = new api.dom.SpanEl();
            wrapper.getEl().setTitle(value);
            wrapper.getEl().setInnerHtml(value.toString().split(':')[1]);
            return wrapper.toString();
        }

        private orderFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
            var wrapper = new api.dom.SpanEl();
            wrapper.getEl().setTitle(value);
            if (node.getData().getContentSummary()) {
                var childOrder = node.getData().getContentSummary().getChildOrder();
                var icon;
                if (!childOrder.isDefault()) {
                    if (!childOrder.isManual()) {
                        if (childOrder.isDesc()) {
                            icon = new api.dom.DivEl("icon-arrow-up4");
                        } else {
                            icon = new api.dom.DivEl("icon-arrow-down4");
                        }
                    } else {
                        icon = new api.dom.DivEl("icon-menu3");
                    }
                    wrapper.getEl().setInnerHtml(icon.toString());
                }
            }
            return wrapper.toString();
        }

        private statusFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {

            if (!node.getData().getContentSummary()) {
                return "";
            }

            var compareLabel: string = api.content.CompareStatus[value];

            var compareStatus: CompareStatus = CompareStatus[compareLabel];

            switch (compareStatus) {
            case CompareStatus.NEW:
                return "New";
                break;
            case CompareStatus.NEWER:
                return "Modified";
                break;
            case CompareStatus.OLDER:
                return "Behind";
                break;
            case CompareStatus.UNKNOWN:
                return "Unknown";
                break;
            case CompareStatus.DELETED:
                return "Deleted";
                break;
            case CompareStatus.EQUAL:
                return "Online";
                break;
            default:
                return "Unknown"
            }
        }

        private nameFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {
            if (!!node.getData().getContentSummary()) {  // default node

                var viewer: ContentSummaryViewer = <ContentSummaryViewer>node.getViewer("name");
                if (!viewer) {
                    viewer = new ContentSummaryViewer();
                    viewer.setObject(node.getData().getContentSummary(), node.calcLevel() > 1);
                    node.setViewer("name", viewer);
                }
                return viewer.toString();

            } else { // `load more` node
                var content = new api.dom.DivEl("children-to-load"),
                    parent = node.getParent();
                content.setHtml((parent.getMaxChildren() - parent.getChildren().length + 1) + " children left to load.");

                return content.toString();
            }
        }


        fetch(node: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<ContentSummaryAndCompareStatus> {
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
                            contents.push(new ContentSummaryAndCompareStatus(null, null));
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
                                    list.push(new ContentSummaryAndCompareStatus(null, null));
                                }
                                parentNode.setMaxChildren(meta.getTotalHits());
                                return list;
                            });
                    });
            }
        }

        hasChildren(data: ContentSummaryAndCompareStatus): boolean {
            return data.hasChildren();
        }

        getDataId(data: ContentSummaryAndCompareStatus): string {
            return data.getId();
        }

        updateContentNode(contentId: api.content.ContentId) {
            var root = this.getRoot().getCurrentRoot();
            var content = root.findNode(contentId.toString()).getData();
            this.updateNode(new ContentSummaryAndCompareStatus(content.getContentSummary(), null));
        }

        appendContentNode(content: api.content.Content, nextToSelection?: boolean) {

            this.fetchById(content.getContentId())
                .then((data: ContentSummaryAndCompareStatus) => {
                    this.appendNode(data, nextToSelection);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                });
        }

        refreshNodeData(parentNode: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<TreeNode<ContentSummaryAndCompareStatus>> {
            return ContentSummaryAndCompareStatusFetcher.fetch(parentNode.getData().getContentId()).then((content: ContentSummaryAndCompareStatus) => {
                parentNode.setData(content);
                this.refreshNode(parentNode);
                return parentNode;
            });
        }

        sortNodeChildren(node: TreeNode<ContentSummaryAndCompareStatus>) {
            var comparator: api.Comparator<TreeNode<ContentSummaryAndCompareStatus>>;
            if (this.getRoot().getCurrentRoot() == node) {
                comparator = new api.content.ContentByDisplayNameComparator();
            } else {
                comparator = new api.content.ContentByModifiedTimeComparator();
            }
            var children = node.getChildren(),
                lastNode = children[children.length - 1],
                emptyNode = (!lastNode || !!lastNode.getData()) ? null : lastNode;

            children = !emptyNode ? children : children.splice(children.indexOf(emptyNode), 1);
            children = children.sort(comparator.compare);
            if (emptyNode) {
                children.push(emptyNode);
            }
            node.setChildren(children);
            this.initData(this.getRoot().getCurrentRoot().treeToList());
        }
    }
}