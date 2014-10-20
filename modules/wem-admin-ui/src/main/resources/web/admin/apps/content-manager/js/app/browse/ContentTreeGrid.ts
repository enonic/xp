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

    import ContentTreeGridActions = app.browse.action.ContentTreeGridActions;

    import CompareStatus = api.content.CompareStatus;

    export class ContentTreeGrid extends TreeGrid<ContentSummaryAndCompareStatus> {

        static MAX_FETCH_SIZE: number = 10;

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
            var contentTypeColumn = new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                setName("ContentType").
                setId("contentType").
                setField("contentSummary.type").
                setCssClass("type").
                setMinWidth(80).
                setMaxWidth(80).
                setFormatter(this.typeFormatter).
                build();

            super(new TreeGridBuilder<ContentSummaryAndCompareStatus>().
                    setColumns([
                        nameColumn,
                        contentTypeColumn,
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
                    this.getGrid().setColumns([nameColumn, contentTypeColumn]);
                } else if (item.isInRangeOrSmaller(api.ui.responsive.ResponsiveRanges._360_540)){
                    this.getGrid().setColumns([nameColumn, contentTypeColumn, modifiedTimeColumn]);
                } else {
                    this.getGrid().setColumns([nameColumn, contentTypeColumn, compareStatusColumn, modifiedTimeColumn]);
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

            this.onSelectionChanged((selectedRows: TreeNode<ContentSummaryAndCompareStatus>[]) => {
                var contentSummaries: ContentSummary[] = selectedRows.map((elem) => {
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
                var contentSummaries = event.getContent(),
                    compareRequest = CompareContentRequest.fromContentSummaries(contentSummaries);

                compareRequest.sendAndParse().then((compareResults: CompareContentResults) => {
                    this.filter(ContentSummaryAndCompareStatusFetcher.updateCompareStatus(contentSummaries, compareResults));
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                }).done(() => this.notifyLoaded());
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

                var contentSummaryViewer = new ContentSummaryViewer();
                contentSummaryViewer.setObject(node.getData().getContentSummary(), node.calcLevel() > 1);
                return contentSummaryViewer.toString();

            } else { // `load more` node
                var content = new api.dom.DivEl("children-to-load"),
                    parent = node.getParent();
                content.setHtml((parent.getMaxChildren() - parent.getChildren().length + 1) + " children left to load.");

                return content.toString();
            }
        }


        fetch(node: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<ContentSummaryAndCompareStatus> {
            return this.fetchById(node.getData().getId());
        }

        private fetchById(id: string): wemQ.Promise<ContentSummaryAndCompareStatus> {
            return ContentSummaryAndCompareStatusFetcher.fetch(id);
        }

        fetchChildren(parentNode?: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<ContentSummaryAndCompareStatus[]> {
            var parentContentId = "";
            if (parentNode) {
                parentContentId = parentNode.getData() ? parentNode.getData().getId() : parentContentId;
            } else {
                parentNode = this.getRoot();
            }

            var from = parentNode.getChildren().length;
            if (from > 0 && !parentNode.getChildren()[from - 1].getData().getContentSummary()) {
                parentNode.getChildren().pop();
                from--;
            }

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
        }

        hasChildren(data: ContentSummaryAndCompareStatus): boolean {
            return data.hasChildren();
        }

        getDataId(data: ContentSummaryAndCompareStatus): string {
            return data.getId();
        }

        updateContentNode(content: api.content.Content) {
            var root = this.getRoot();
            root.treeToList().forEach((child: TreeNode<ContentSummaryAndCompareStatus>) => {
                var curContent: ContentSummaryAndCompareStatus = child.getData();
                if (child.getData().getId() == content.getId()) {
                    curContent.setContentSummary(content);
                    this.updateNode(curContent);
                }
            });
        }

        appendContentNode(content: api.content.Content) {

            this.fetchById(content.getId())
                .then((data: ContentSummaryAndCompareStatus) => {
                    this.appendNode(data);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                });
        }

        updateDataChildrenStatus(parentNode: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<TreeNode<ContentSummaryAndCompareStatus>> {
            return ContentSummaryAndCompareStatusFetcher.fetchChildren(parentNode.getData().getId()).then((result: ContentResponse<ContentSummaryAndCompareStatus>) => {
                var hasChildren = (result.getMetadata().getTotalHits() > 0);
                if (parentNode.getData()) {
                    parentNode.getData().setContentSummary(new ContentSummaryBuilder(parentNode.getData().getContentSummary()).
                        setHasChildren(hasChildren).
                        setDeletable(!hasChildren).
                        build());
                    this.refreshNode(parentNode);
                    return parentNode;
                }
            });

        }
    }
}