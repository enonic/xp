module app.browse.grid {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    import TreeGrid = api.ui.treegrid.TreeGrid;
    import TreeNode = api.ui.treegrid.TreeNode;
    import TreeGridBuilder = api.ui.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;

    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

    export class ContentGridPanel2 extends TreeGrid<ContentSummaryAndCompareStatus> {

        constructor() {
            super(new TreeGridBuilder<ContentSummaryAndCompareStatus>().
                    setColumns([
                        new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                            setName("Name").
                            setId("displayName").
                            setField("content.displayName").
                            setFormatter(this.defaultNameFormatter).
                            build(),

                        new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                            setName("CompareStatus").
                            setId("compareStatus").
                            setField("compareContentResult.compareStatus").
                            setFormatter(this.statusFormatter).
                            setCssClass("status").
                            setMinWidth(90).
                            setMaxWidth(100).
                            build(),

                        new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                            setName("ModifiedTime").
                            setId("modifiedTime").
                            setField("content.modifiedTime").
                            setCssClass("modified").
                            setMinWidth(150).
                            setMaxWidth(170).
                            setFormatter(DateTimeFormatter.format).
                            build()
                    ]).prependClasses("content-grid")
            );

            this.getGrid().subscribeOnDblClick((event, data) => {
                if (this.isActive()) {
                    new EditContentEvent([this.getGrid().getDataView().getItem(data.row).getData().getContentSummary()]).fire();
                }
            });
        }

        private statusFormatter(row: number, cell: number, value: any, columnDef: any, item: ContentSummaryAndCompareStatus) {
            return api.content.CompareStatus[value];
        }

        private defaultNameFormatter(row: number, cell: number, value: any, columnDef: any, item: ContentSummaryAndCompareStatus) {
            var contentSummaryViewer = new ContentSummaryViewer();
            contentSummaryViewer.setObject(item.getContentSummary());
            return contentSummaryViewer.toString();
        }

        hasChildren(item: api.content.ContentSummaryAndCompareStatus): boolean {
            return item.hasChildren();
        }

        fetchChildren(parent?: api.content.ContentSummaryAndCompareStatus): Q.Promise<api.content.ContentSummaryAndCompareStatus[]> {
            var parentContentId = parent ? parent.getId() : "";
            return new api.content.ContentSummaryAndCompareStatusFetcher(parentContentId).fetch(parentContentId);
        }

    }
}