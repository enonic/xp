module app.wizard.page.contextwindow {

    export class EmulatorGrid extends api.ui.grid.Grid<any> {

        constructor(dataView: api.ui.grid.DataView<any>) {
            super(dataView, this.createColumns(), this.createOptions());
        }

        private createOptions(): api.ui.grid.GridOptions<any> {
            return new api.ui.grid.GridOptionsBuilder().
                setHideColumnHeaders(true).
                setRowHeight(50).
                setHeight(450).
                setWidth(320)
                .build();
        }

        private createColumns(): api.ui.grid.GridColumn<any>[] {
            return [new api.ui.grid.GridColumnBuilder().
                setName("device").
                setField("device").
                setId("device").
                setWidth(320).
                setCssClass("grid-row").
                setFormatter((row, cell, value, columnDef, dataContext) => {
                    return this.buildRow(row, cell, value).toString();
                }).
                build()
            ];
        }

        private buildRow(row, cell, data): api.dom.DivEl {
            var rowEl = new api.dom.DivEl();
            rowEl.getEl().setData('width', data.width);
            rowEl.getEl().setData('height', data.height);
            rowEl.getEl().setData('units', data.units);

            var icon = new api.ui.FontIcon("icon-" + data.device_type);

            var title = new api.dom.H5El();
            title.getEl().setInnerHtml(data.name);

            var subtitle = new api.dom.H6El();
            var units = data.display_units ? data.units : "";
            subtitle.getEl().setInnerHtml(data.width + units + " &times; " + data.height + units, false);
            rowEl.appendChild(icon);
            rowEl.appendChild(title);
            rowEl.appendChild(subtitle);

            if (data.rotatable == true) {
                var rotator = new api.dom.DivEl();
                rotator.addClass('rotate');
                rotator.addClassEx('icon-loop');
                rowEl.appendChild(rotator);
            }

            return rowEl;
        }

        static toSlickData(data: any[]): any[] {
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