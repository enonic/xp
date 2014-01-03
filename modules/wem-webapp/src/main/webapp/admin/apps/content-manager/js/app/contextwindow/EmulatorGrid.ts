module app.contextwindow {
    export class EmulatorGrid extends api.ui.grid.Grid<any> {

        constructor(dataView:api.ui.grid.DataView<any>) {
            super(dataView, this.createColumns(), {hideColumnHeaders: true, rowHeight: 50, height: 400, width: 320});
        }

        private createColumns():api.ui.grid.GridColumn<any>[] {
            return [
                {
                    name: "device",
                    field: "device",
                    id: "device",
                    width: 320,
                    cssClass: "grid-row",
                    formatter: (row, cell, value, columnDef, dataContext) => {
                        return this.buildRow(row, cell, value).toString();
                    }
                }
            ];
        }

        private buildRow(row, cell, data):api.dom.DivEl {
            var rowEl = new api.dom.DivEl();
            rowEl.getEl().setData('width', data.width);
            rowEl.getEl().setData('height', data.height);
            rowEl.getEl().setData('type', data.device_type);

            var icon = new api.dom.DivEl();
            icon.setClass('icon-' + data.device_type);
            icon.addClass('icon');

            var title = new api.dom.H5El();
            title.getEl().setInnerHtml(data.name);

            var subtitle = new api.dom.H6El();
            subtitle.getEl().setInnerHtml(data.width + " &times; " + data.height + " " + data.device_type);

            rowEl.appendChild(icon);
            rowEl.appendChild(title);
            rowEl.appendChild(subtitle);

            if (data.rotatable == true) {
                var rotator = new api.dom.DivEl();
                rotator.addClass('rotate');
                rotator.addClass('icon-loop');
                rowEl.appendChild(rotator);
            }

            return rowEl;
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