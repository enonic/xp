module app.browse.grid {

    import GridColumn = api.ui.grid.GridColumn;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;

    import TreeGrid = api.app.browse.treegrid.TreeGrid;
    import DateTimeFormatter = api.app.browse.treegrid.DateTimeFormatter;


    export class ContentGridPanel2 extends TreeGrid<ContentSummary> {

        constructor() {
            super("content-grid");

            var nameFormatter = (row: number, cell: number, value: any, columnDef: any, item: ContentSummary) => {
                var contentSummaryViewer = new ContentSummaryViewer();
                contentSummaryViewer.setObject(item);
                return contentSummaryViewer.toString();
            };

            // GridColumn<TreeNode<ContentSummary>> is a valid type
            var column1 = <GridColumn<any>> {
                name: "Name",
                id: "displayName",
                field: "displayName",
                formatter: nameFormatter
            };
            var column2 = <GridColumn<any>> {
                name: "ModifiedTime",
                id: "modifiedTime",
                field: "modifiedTime",
                cssClass: "modified",
                minWidth: 150,
                maxWidth: 170,
                formatter: DateTimeFormatter.format
            };

            this.setColumns([column1, column2]);

            this.getGrid().subscribeOnDblClick((event, data) => {
                if (this.isActive()) {
                    new EditContentEvent([this.getGrid().getDataView().getItem(data.row).getData()]).fire();
                }
            });
        }

        fetchChildren(parent?: ContentSummary): Q.Promise<ContentSummary[]> {
            var parentContentId = parent ? parent.getId() : "";
            return new api.content.ListContentByIdRequest(parentContentId).sendAndParse();
        }
    }
}
