module app_browse_grid {

    export class ContentGridPanel2 extends api_app_browse_grid.GridPanel2 {

        private columns:api_ui_grid.GridColumn<api_content.ContentSummary>[] = [];

        private gridOptions:api_ui_grid.GridOptions;

        private grid:api_ui_grid.Grid<api_content.ContentSummary>;

        private gridData:api_ui_grid.DataView<api_content.ContentSummary>;

        constructor() {
            super();

            var column1 = <api_ui_grid.GridColumn> {
                name: "Name",
                id: "displayName",
                field: "displayName",
                formatter: this.getNameFormatter()
            };
            var column2 = <api_ui_grid.GridColumn> {
                name: "ModifiedTime",
                id: "modifiedTime",
                field: "modifiedTime",
                formatter:api_app_browse_grid.DateTimeFormatter.format
            };
            this.columns = [column1, column2];

            this.gridData = new api_ui_grid.DataView<api_content.ContentSummary>();

            this.gridOptions = <api_ui_grid.GridOptions>{
                editable: false,
                enableAddRow: true,
                enableCellNavigation: true,
                enableColumnReorder: false,
                forceFitColumns: true,
                hideColumnHeaders: true,
                checkableRows: true,
                rowHeight: 45
            };

            this.grid = new api_ui_grid.Grid<api_content.ContentSummary>(this.gridData, this.columns, this.gridOptions);
            var selectionModel = new Slick.RowSelectionModel();
            this.grid.setSelectionModel( selectionModel );

            this.appendChild(this.grid);


            jQuery.when(new api_content.ListContentByIdRequest("").send()).
                then((response:api_rest.JsonResponse) => {
                    var contents:api_content.ContentSummary[] = [];
                    response.getJson().contents.forEach((node) => {
                        contents.push(new api_content.ContentSummary(node));
                    });
                    this.initData(contents);
                }
            );
        }

        afterRender() {
            this.grid.resizeCanvas();
        }

        private initData(contents:api_content.ContentSummary[]) {

            console.log("initData", contents);
            this.gridData.setItems(contents, "id")
        }
    }
}
