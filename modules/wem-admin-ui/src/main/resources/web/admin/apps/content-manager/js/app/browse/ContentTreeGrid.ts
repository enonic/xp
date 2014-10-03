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
    import ContentSummaryViewer = api.content.ContentSummaryViewer;
    import CompareContentRequest = api.content.CompareContentRequest;
    import CompareContentResults = api.content.CompareContentResults;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentSummaryAndCompareStatusFetcher = api.content.ContentSummaryAndCompareStatusFetcher;

    import ContentBrowseSearchEvent = app.browse.filter.ContentBrowseSearchEvent;
    import ContentBrowseResetEvent = app.browse.filter.ContentBrowseResetEvent;

    import ContentTreeGridActions = app.browse.action.ContentTreeGridActions;

    import CompareStatus = api.content.CompareStatus;

    export class ContentTreeGrid extends TreeGrid<ContentSummaryAndCompareStatus> {

        static MAX_FETCH_SIZE: number = 5;

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
                setMinWidth(90).
                setMaxWidth(100).
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

            super(new TreeGridBuilder<ContentSummaryAndCompareStatus>().
                    setColumns([
                        nameColumn,
                        compareStatusColumn,
                        modifiedTimeColumn
                    ]).setShowContextMenu(new TreeGridContextMenu(new ContentTreeGridActions(this))
                ).prependClasses("content-tree-grid")
            );

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
                if (item.isInRangeOrSmaller(api.ui.responsive.ResponsiveRanges._240_360)) {
                    this.getGrid().setColumns([nameColumn, compareStatusColumn]);
                } else {
                    this.getGrid().setColumns([nameColumn, compareStatusColumn, modifiedTimeColumn]);
                }

                if (item.isInRangeOrSmaller(api.ui.responsive.ResponsiveRanges._360_540)) {
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
                    if (node.getData().getContentSummary()) { // default event
                        new EditContentEvent([node.getData().getContentSummary()]).fire();
                    } else {
                        this.loadEmptyNode(node);
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

            /*
             Empty links handlers
             */
            var postLoadCycle = setInterval(this.postLoad.bind(this), 1000);

            this.getGrid().onScroll(() => {
                clearInterval(postLoadCycle);
                postLoadCycle = setInterval(this.postLoad.bind(this), 1000);
            });
        }

        private loadEmptyNode(node: TreeNode<ContentSummaryAndCompareStatus>, loadMask?: api.ui.mask.LoadMask) {
            if (!node.getData().getContentSummary()) {
                this.setActive(false);

                if (loadMask) {
                    loadMask.show();
                }

                this.fetchChildren(node.getParent()).then((dataList: ContentSummaryAndCompareStatus[]) => {
                    node.getParent().setChildren(this.dataToTreeNodes(dataList, node.getParent()));
                    this.initData(this.getRoot().treeToList());
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(() => {
                    this.setActive(true);
                    if (loadMask) {
                        loadMask.hide();
                        loadMask.remove();
                    }
                }).done(() => this.notifyLoaded());
            }
        }

        private postLoad() {
            if (this.getCanvas().getEl().isVisible()) {
                this.setCanvas(Element.fromHtmlElement(this.getCanvas().getHTMLElement(), true));
                // top > point && point + 45 < bottom
                var children = this.getCanvas().getChildren(),
                    top = this.getGrid().getEl().getScrollTop(),
                    bottom = top + this.getGrid().getEl().getHeight();
                children = children.filter((el) => {
                    return (el.getEl().getOffsetTopRelativeToParent() + 5 > top) &&
                        (el.getEl().getOffsetTopRelativeToParent() + 40 < bottom);
                });

                for (var i = 0; i < children.length; i++) {
                    if (children[i].getHTMLElement().getElementsByClassName("children-to-load").length > 0 && this.isActive()) {
                        var node = this.getGrid().getDataView().getItem(children[i].getEl().getOffsetTopRelativeToParent() / 45),
                            loadMask = new api.ui.mask.LoadMask(children[i]);
                        loadMask.addClass("small");
                        this.loadEmptyNode(node, loadMask);
                        break;
                    }
                }
            }
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
                content.setHtml((parent.getMaxChildren() - parent.getChildren().length + 1) +
                                " children left to load. Double-click to load more.");

                return content.toString();
            }

        }

        fetch(node: TreeNode<ContentSummaryAndCompareStatus>): wemQ.Promise<ContentSummaryAndCompareStatus> {
            var contentId = node.getData().getId();
            return ContentSummaryAndCompareStatusFetcher.fetch(contentId);
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

    }
}