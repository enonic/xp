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
    import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;
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
                setField("compareStatus").
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

            this.onSelectionChanged((currentSelection: TreeNode<ContentSummaryAndCompareStatus>[],
                                     fullSelection: TreeNode<ContentSummaryAndCompareStatus>[]) => {
                var contentSummaries: ContentSummary[] = fullSelection.map((elem) => {
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

            var data = node.getData();
            var status;

            if (!!data.getContentSummary()) {   // default node
                var compareStatus: CompareStatus = CompareStatus[CompareStatus[value]];

                switch (compareStatus) {
                case CompareStatus.NEW:
                    status = "New";
                    break;
                case CompareStatus.NEWER:
                    status = "Modified";
                    break;
                case CompareStatus.OLDER:
                    status = "Behind";
                    break;
                case CompareStatus.UNKNOWN:
                    status = "Unknown";
                    break;
                case CompareStatus.DELETED:
                    status = "Deleted";
                    break;
                case CompareStatus.EQUAL:
                    status = "Online";
                    break;
                default:
                    status = "Unknown"
                }

            } else if (!!data.getUploadItem()) {   // uploading node
                status = new api.ui.ProgressBar(data.getUploadItem().getProgress()).toString();
            }

            return status;
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

        hasChildren(data: ContentSummaryAndCompareStatus): boolean {
            return data.hasChildren();
        }

        getDataId(data: ContentSummaryAndCompareStatus): string {
            return data.getId();
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

        appendUploadNode(item: api.ui.uploader.UploadItem<ContentSummary>, nextToSelection?: boolean) {

            var data = ContentSummaryAndCompareStatus.fromUploadItem(item);
            this.appendNode(data, nextToSelection);

            item.onProgress((progress: number) => {
                this.resetAndRender();
            });
            item.onUploaded((model: ContentSummary) => {
                this.updateNode(data, item.getId());
                api.notify.showSuccess("Upload [" + item.getName() + "] finished");
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
    }
}