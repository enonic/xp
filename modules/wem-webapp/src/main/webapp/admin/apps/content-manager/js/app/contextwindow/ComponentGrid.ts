module app_contextwindow {
    export interface ComponentGridOptions {
        draggableRows?:boolean;
        rowClass?:string;
        onClick?:(el) => void;
    }

    export class ComponentGrid extends api_grid.Grid {
        private componentGridOptions:ComponentGridOptions;

        constructor(data:any = {}, options:ComponentGridOptions = {}) {
            super(data, this.createColumns(), {hideColumnHeaders: true, rowHeight: 50, height: 400, width: 320});
            this.componentGridOptions = options;
            this.setFilter(this.filter);
        }

        afterRender() {
            super.afterRender();
            if (this.componentGridOptions.onClick) {
                this.setOnClick(this.componentGridOptions.onClick);
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
                        return this.buildRow(row, cell, value).toString();
                    }
                }
            ];
        }

        private buildRow(row, cell, data):api_dom.DivEl {
            var row = new api_dom.DivEl();
            row.getEl().setData('live-edit-key', data.key);
            row.getEl().setData('live-edit-name', data.name);
            row.getEl().setData('live-edit-type', data.typeName);
            if (this.componentGridOptions.draggableRows) {
                row.getEl().setData('context-window-draggable', 'true');
            }
            if (this.componentGridOptions.rowClass) {
                row.addClass(this.componentGridOptions.rowClass)
            }

            var icon = new api_dom.DivEl();
            icon.setClass('live-edit-font-icon-' + data.typeName);
            icon.addClass('icon');

            var title = new api_dom.H5El();
            title.getEl().setInnerHtml(data.name);

            var subtitle = new api_dom.H6El();
            subtitle.getEl().setInnerHtml(data.subtitle);

            row.appendChild(icon);
            row.appendChild(title);
            row.appendChild(subtitle);

            return row;
        }

        static toSlickData(data:any[]):any[] {
            var result = [];
            data["components"].forEach((item, index) => {
                var tmp = {
                    "id": item.key,
                    "component": item
                };
                result.push(tmp);
            });
            return result;
        }
    }


}