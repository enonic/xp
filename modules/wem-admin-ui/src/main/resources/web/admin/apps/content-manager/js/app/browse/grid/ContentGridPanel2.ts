module app.browse.grid {

    import GridColumn = api.ui.grid.GridColumn;

    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryViewer = api.content.ContentSummaryViewer;


    export class ContentGridPanel2 extends api.app.browse.treegrid.TreeGrid<ContentSummary> {

        constructor() {
            super("content-grid");

            var nameFormatter = (row:number, cell:number, value:any, columnDef:any, item:ContentSummary) => {
                var contentSummaryViewer = new ContentSummaryViewer();
                contentSummaryViewer.setObject(item);
                return contentSummaryViewer.toString();
            };

            var column1 = <GridColumn<ContentSummary>> {
                name: "Name",
                id: "displayName",
                field: "displayName",
                formatter: nameFormatter
            };
            var column2 = <GridColumn<ContentSummary>> {
                name: "ModifiedTime",
                id: "modifiedTime",
                field: "modifiedTime",
                cssClass: "modified",
                minWidth: 150,
                maxWidth: 170,
                formatter: api.app.browse.grid2.DateTimeFormatter.format
            };

            this.setColumns([column1, column2]);

            this.getGrid().subscribeOnDblClick((event, data) => {
                if (this.hasClass("active")) {
                    new EditContentEvent([this.getGrid().getDataView().getItem(data.row)]).fire();
                }
            });
        }

        hasChildren(item:ContentSummary):boolean {
            return item.hasChildren();
        }

        listRequest(item?:ContentSummary):Q.Promise<ContentSummary[]> {
            var id = item ? item.getId() : "";

            return new api.content.ListContentByIdRequest(id).sendAndParse();
        }
    }
}
