module app.browse.grid {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    import TreeGrid = api.app.browse.treegrid.TreeGrid;
    import TreeNode = api.app.browse.treegrid.TreeNode;
    import TreeGridBuilder = api.app.browse.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.app.browse.treegrid.DateTimeFormatter;

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
                            setName("ModifiedTime").
                            setId("modifiedTime").
                            setField("content.modifiedTime").
                            setCssClass("modified").
                            setMinWidth(150).
                            setMaxWidth(170).
                            setFormatter(DateTimeFormatter.format).
                            build() /*,

                         new GridColumnBuilder<TreeNode<ContentSummaryAndCompareStatus>>().
                         setName("CompareStatus").
                         setId("compareStatus").
                         setField("compareContentResult.compareStatus").
                         setFormatter(this.statusFormatter).
                         build(),  */

                    ]).prependClasses("content-grid")
            );

            this.getGrid().subscribeOnDblClick((event, data) => {
                if (this.isActive()) {
                    new EditContentEvent([this.getGrid().getDataView().getItem(data.row).getData().getContentSummary()]).fire();
                }
            });
        }

        private statusFormatter(row: number, cell: number, value: any, columnDef: any, item: ContentSummaryAndCompareStatus) {

            return api.content.CompareStatus[item.getCompareContentResult().compareStatus];
        }

        private defaultNameFormatter(row: number, cell: number, value: any, columnDef: any, item: ContentSummaryAndCompareStatus) {
            var contentSummaryViewer = new ContentSummaryViewer();
            contentSummaryViewer.setObject(item.getContentSummary());
            return contentSummaryViewer.toString();
        }

        fetchChildren(parent?: api.content.ContentSummaryAndCompareStatus): Q.Promise<api.content.ContentSummaryAndCompareStatus[]> {
            var parentContentId = parent ? parent.getId() : "";
            return new api.content.ContentSummaryAndCompareStatusFetcher(parentContentId).fetch(parentContentId);
        }

    }
}