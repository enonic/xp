module app.browse.grid {

    export class ContentGridPanel2 extends api.app.browse.grid2.GridPanel2 {

        private columns:api.ui.grid.GridColumn<api.content.ContentSummary>[] = [];

        private gridOptions:api.ui.grid.GridOptions;

        private grid:api.ui.grid.Grid<api.content.ContentSummary>;

        private gridData:api.ui.grid.DataView<api.content.ContentSummary>;

        constructor() {
            super();

            var column1 = <api.ui.grid.GridColumn<any>> {
                name: "Name",
                id: "displayName",
                field: "displayName",
                formatter: this.getNameFormatter()
            };
            var column2 = <api.ui.grid.GridColumn<any>> {
                name: "ModifiedTime",
                id: "modifiedTime",
                field: "modifiedTime",
                formatter:api.app.browse.grid2.DateTimeFormatter.format
            };
            this.columns = [column1, column2];

            this.gridData = new api.ui.grid.DataView<api.content.ContentSummary>();

            this.gridOptions = <api.ui.grid.GridOptions>{
                editable: false,
                enableAddRow: true,
                enableCellNavigation: true,
                enableColumnReorder: false,
                forceFitColumns: true,
                hideColumnHeaders: true,
                checkableRows: true,
                rowHeight: 45
            };

            this.grid = new api.ui.grid.Grid<api.content.ContentSummary>(this.gridData, this.columns, this.gridOptions);
            var selectionModel = new Slick.RowSelectionModel();
            this.grid.setSelectionModel( selectionModel );

            this.appendChild(this.grid);

            new api.content.ListContentByIdRequest("").send().
                then( (response:api.rest.JsonResponse<api.content.ListContentResult<api.content.json.ContentSummaryJson>>) => {
                    var contents:api.content.ContentSummary[] = api.content.ContentSummary.fromJsonArray( response.getResult().contents );
                    this.initData(contents);
                }
            );
        }

        afterRender() {
            this.grid.resizeCanvas();
        }

        private initData(contents:api.content.ContentSummary[]) {

            //console.log("initData", contents);
            this.gridData.setItems(contents, "id")
        }
    }
}
