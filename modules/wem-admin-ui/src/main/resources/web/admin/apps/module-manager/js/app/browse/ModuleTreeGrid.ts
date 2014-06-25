module app.browse {

    import GridColumn = api.ui.grid.GridColumn;

    import ModuleSummary = api.module.ModuleSummary;
    import ModuleSummaryViewer = api.module.ModuleSummaryViewer;
    import TreeGrid = api.app.browse.treegrid.TreeGrid;
    import DateTimeFormatter = api.app.browse.treegrid.DateTimeFormatter;


    export class ModuleTreeGrid extends TreeGrid<ModuleSummary> {

        constructor() {
            super({showToolbar: true}, "module-grid");

            var nameFormatter = (row: number, cell: number, value: any, columnDef: any, item: ModuleSummary) => {
                var viewer = new ModuleSummaryViewer();
                viewer.setObject(item);
                return viewer.toString();
            };

            var column1 = <GridColumn<any>> {
                name: "Name",
                id: "displayName",
                field: "displayName",
                formatter: nameFormatter,
                minWidth: 250
            };
            var column2 = <GridColumn<any>> {
                name: "ModifiedTime",
                id: "modifiedTime",
                field: "modifiedTime",
                formatter: DateTimeFormatter.format,
                width: 150,
                minWidth: 150
            };
            var column3 = <GridColumn<any>> {
                name: "Version",
                id: "version",
                field: "version",
                maxWidth: 70,
                minWidth: 50
            };
            var column4 = <GridColumn<any>> {
                name: "State",
                id: "state",
                field: "state",
                minWidth: 80
            };

            this.setColumns([column1, column2, column3, column4]);
        }

        fetchChildren(parent?: ModuleSummary): Q.Promise<ModuleSummary[]> {
            return new api.module.ListModuleRequest().sendAndParse();
        }
    }
}
