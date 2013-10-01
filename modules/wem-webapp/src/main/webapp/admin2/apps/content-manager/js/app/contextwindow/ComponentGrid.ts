module app_contextwindow {
    export interface ComponentGridOptions {
        draggableRows?:boolean;
        rowClass?:string;
        onClick?:() => void;
    }

    export class ComponentGrid extends api_grid.Grid {
        private componentGridOptions:ComponentGridOptions;

        constructor(data:any, options:ComponentGridOptions = {}) {
            super(data, this.createColumns(), {hideColumnHeaders: true, rowHeight: 50, height: 400, width: 320});
            this.componentGridOptions = options;
            this.setFilter(this.filter);
        }

        afterRender() {
            super.afterRender();
            if (this.componentGridOptions.onClick) {
                this.setOnClick();
            }
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
                        return this.buildRow(row, cell, value);
                    }
                }
            ];
        }

        private buildRow(row, cell, data):string {
            var rowHtml:string[] = [];
            rowHtml.push('<div ');

            if (this.componentGridOptions.rowClass) {
                rowHtml.push('class="' + this.componentGridOptions.rowClass + '" ');
            }
            if (this.componentGridOptions.draggableRows) {
                rowHtml.push('data-context-window-draggable="true" ');
            }
            rowHtml.push(
                'data-live-edit-key="' + data.key + '" ',
                'data-live-edit-name="' + data.name + '" ',
                'data-live-edit-type = "' + data.typeName + '"',
                '>',
                '<h5>' + data.name + '</h5>',
                '<h6>' + data.subtitle + '</h6>',
                '</div>'
            );

            return rowHtml.join("");
        }
    }


}