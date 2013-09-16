module app_contextwindow {
    export class ComponentGrid extends api_grid.Grid {

        constructor(data:any) {
            super(data, this.createColumns(), {hideColumnHeaders: true, rowHeight: 50, height: 400, width: 320});
            this.setFilter(this.filter);
        }

        private filter(item, args) {
            if (args) {
                if (args.searchString != "" && item["component"]["name"].indexOf(args.searchString) == -1) {
                    return false;
                }
            }

            return true;
        }

        updateFilter(searchString:string) {
            this.getDataView().setFilterArgs({
                searchString: searchString
            });
            this.getDataView().refresh();
        }

        private createColumns():api_grid.GridColumn[] {
            return [
                {
                    name: "component",
                    field: "component",
                    id: "component",
                    width: 320,
                    cssClass: "component",
                    formatter: (row, cell, value, columnDef, dataContext) => {
                        return '<div data-live-edit-key="' + value.key + '">' +
                               '<h5>' + value.name + '</h5>' +
                               '<h6>' + value.subtitle + '</h6>' +
                               '</div>';
                    }
                }
            ];
        }
    }


}