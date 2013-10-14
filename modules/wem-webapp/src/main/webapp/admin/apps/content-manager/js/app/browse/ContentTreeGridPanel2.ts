module app_browse {

    export class ContentTreeGridPanel2 extends api_app_browse_grid.TreeGridPanel2 {

        private columns:api_ui_grid.GridColumn<api_content.ContentSummary>[] = [];

        private gridOptions:api_ui_grid.GridOptions;

        private grid:api_ui_grid.Grid<api_content.ContentSummary>;

        private gridData:api_ui_grid.DataView<api_content.ContentSummary>;

        private nameFormatter:(row:number, cell:number, value:any, columnDef:any, dataContext:Slick.SlickData) => string;

        constructor() {
            super();

            this.nameFormatter = (row:number, cell:number, value:any, columnDef:any, item:api_content.ContentSummary) => {
                var content:api_content.ContentSummary = item;
                var html:string = "";
                html += '<img src=' + content.getIconUrl() + ' width="25" height="25"/>';
                html += content.getDisplayName();
                return html;
            };

            var column1 = <api_ui_grid.GridColumn> {
                name: "Name",
                id: "displayName",
                field: "displayName",
                formatter: this.nameFormatter
            };
            var column2 = <api_ui_grid.GridColumn> {
                name: "ModifiedTime",
                id: "modifiedTime",
                field: "modifiedTime"
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
                checkableRows: true
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

        private initData(contents:api_content.ContentSummary[]) {

            console.log("initData", contents);
            this.gridData.setItems(contents, "id")
        }
    }
}
