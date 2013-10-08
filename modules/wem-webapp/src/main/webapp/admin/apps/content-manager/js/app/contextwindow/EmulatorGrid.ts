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
                        return this.buildRow(row, cell, value).toString();
                    }
                }
            ];
        }

        private buildRow(row, cell, data):api_dom.DivEl {
            var row = new api_dom.DivEl();
            row.getEl().setData('width', data.width);
            row.getEl().setData('height', data.height);
            row.getEl().setData('type', data.device_type);

            var title = new api_dom.H5El();
            title.getEl().setInnerHtml(data.name);

            var subtitle = new api_dom.H6El();
            subtitle.getEl().setInnerHtml(data.width + " &times; " + data.height + " " + data.device_type);

            row.appendChild(title);
            row.appendChild(subtitle);

            if (data.rotatable == true) {
                var rotator = new api_dom.DivEl();
                rotator.addClass('rotate');
                rotator.addClass('live-edit-font-icon-spinner');
                row.appendChild(rotator);
            }

            return row;
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