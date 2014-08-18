module app.browse {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;
    import TreeGridContextMenu = api.ui.treegrid.TreeGridContextMenu;

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
                ).prependClasses("content-grid")
            );

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
                if (item.isInRangeOrSmaller(api.ui.responsive.ResponsiveRanges._240_360)) {
                    this.getGrid().setColumns([nameColumn, compareStatusColumn]);
                } else {
                    this.getGrid().setColumns([nameColumn, compareStatusColumn, modifiedTimeColumn]);
                }

                if(item.isInRangeOrSmaller(api.ui.responsive.ResponsiveRanges._360_540)) {
                    modifiedTimeColumn.setMaxWidth(100);
                    modifiedTimeColumn.setFormatter(DateTimeFormatter.formatNoTimestamp);
                } else {
                    modifiedTimeColumn.setMaxWidth(170);
                    modifiedTimeColumn.setFormatter(DateTimeFormatter.format);
                }

                this.getGrid().resizeCanvas();
            });

            this.onRowSelectionChanged((selectedRows: TreeNode<ContentSummaryAndCompareStatus>[]) => {
                var contentSummaries: ContentSummary[] = selectedRows.map((elem) => {
                    return elem.getData().getContentSummary();
                });
                (<ContentTreeGridActions>this.getContextMenu().getActions()).updateActionsEnabledState(contentSummaries);
            });

            this.getGrid().subscribeOnDblClick((event, data) => {
                if (this.isActive()) {
                    new EditContentEvent([this.getGrid().getDataView().getItem(data.row).getData().getContentSummary()]).fire();
                }
            });

            // Filter events
            ContentBrowseSearchEvent.on((event) => {
                var contentSummaries = ContentSummary.fromJsonArray(event.getJsonModels()),
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
        }

        private statusFormatter(row: number, cell: number, value: any, columnDef: any, node: TreeNode<ContentSummaryAndCompareStatus>) {

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
            var contentSummaryViewer = new ContentSummaryViewer();
            contentSummaryViewer.setObject(node.getData().getContentSummary(), node.calcLevel() > 1);
            return contentSummaryViewer.toString();
        }

        fetch(data: ContentSummaryAndCompareStatus): Q.Promise<ContentSummaryAndCompareStatus> {
            var contentId = data.getId();
            return ContentSummaryAndCompareStatusFetcher.fetch(contentId);
        }

        fetchChildren(parentData?: ContentSummaryAndCompareStatus): Q.Promise<ContentSummaryAndCompareStatus[]> {
            var parentContentId = parentData ? parentData.getId() : "";
            return ContentSummaryAndCompareStatusFetcher.fetchChildren(parentContentId);
        }

        hasChildren(data: ContentSummaryAndCompareStatus): boolean {
            return data.hasChildren();
        }

    }
}