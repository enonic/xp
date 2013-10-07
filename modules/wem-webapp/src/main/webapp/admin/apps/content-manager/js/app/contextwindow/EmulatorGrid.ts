module app_contextwindow {
    export class EmulatorGrid extends api_grid.Grid {

        constructor(data:any = {}) {
            super(data, this.createColumns(), {hideColumnHeaders: true, rowHeight: 50, height: 400, width: 320});
        }

        private createColumns():api_grid.GridColumn[] {
            return [
                {
                    name: "device",
                    field: "device",
                    id: "device",
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
            rowHtml.push(
                'data-width="' + data.width +'"',
                'data-height="'+ data.height +'"',
                'data-type="' + data.device_type +'"',
                '>',
                '<h5>' + data.name + '</h5>',
                '<h6>' + data.width + " &times; " + data.height + " " + data.device_type + '</h6>',
                '</div>'
            );

            return rowHtml.join("");
        }

        static toSlickData(data:any[]):any[] {
            var result = [];
            var i = 1;
            data["devices"].forEach((item, index) => {
                var tmp = {
                    "id": i,
                    "device": item
                };
                result.push(tmp);
                i++;
            });
            return result;
        }

    }
}