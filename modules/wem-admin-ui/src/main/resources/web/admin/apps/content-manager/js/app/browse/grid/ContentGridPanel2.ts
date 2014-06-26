module app.browse.grid {

    import GridColumn = api.ui.grid.GridColumn;
    import GridColumnBuilder = api.ui.grid.GridColumnBuilder;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    import TreeGrid = api.app.browse.treegrid.TreeGrid;
    import TreeNode = api.app.browse.treegrid.TreeNode;
    import TreeGridBuilder = api.app.browse.treegrid.TreeGridBuilder;
    import DateTimeFormatter = api.app.browse.treegrid.DateTimeFormatter;

    export class ContentGridPanel2 extends TreeGrid<ContentSummary> {

        constructor() {
            super(new TreeGridBuilder<ContentSummary>().
                setColumns([
                    new GridColumnBuilder<TreeNode<ContentSummary>>().
                        setName("Name").
                        setId("displayName").
                        setField("displayName").
                        setFormatter(this.defaultNameFormatter).
                    build(),

                    new GridColumnBuilder<TreeNode<ContentSummary>>().
                        setName("ModifiedTime").
                        setId("modifiedTime").
                        setField("modifiedTime").
                        setCssClass("modified").
                        setMinWidth(150).
                        setMaxWidth(170).
                        setFormatter(DateTimeFormatter.format).
                    build()
                ]).prependClasses("content-grid")
            );

            this.getGrid().subscribeOnDblClick((event, data) => {
                if (this.isActive()) {
                    new EditContentEvent([this.getGrid().getDataView().getItem(data.row).getData()]).fire();
                }
            });
        }

        private defaultNameFormatter(row: number, cell: number, value: any, columnDef: any, item: ContentSummary) {
            var contentSummaryViewer = new ContentSummaryViewer();
            contentSummaryViewer.setObject(item);
            return contentSummaryViewer.toString();
        }

        fetchChildren(parent?: ContentSummary): Q.Promise<ContentSummary[]> {
            var parentContentId = parent ? parent.getId() : "";
            return new api.content.ListContentByIdRequest(parentContentId).sendAndParse();
        }
    }
}
