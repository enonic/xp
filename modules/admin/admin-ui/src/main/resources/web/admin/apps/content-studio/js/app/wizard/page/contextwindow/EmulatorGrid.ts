import '../../../../api.ts';

export class EmulatorGrid extends api.ui.grid.Grid<any> {

    protected createOptions(): api.ui.grid.GridOptions<any> {
        return new api.ui.grid.GridOptionsBuilder().setHideColumnHeaders(true).setRowHeight(50).setHeight(450).setWidth(320)
            .build();
    }

    protected createColumns(): api.ui.grid.GridColumn<any>[] {
        return [new api.ui.grid.GridColumnBuilder().setName('device').setField('device').setId('device').setWidth(320).setCssClass(
            'grid-row').setFormatter((row, cell, value, columnDef, dataContext) => {
            return this.buildRow(row, cell, value).toString();
        }).build()
        ];
    }

    private buildRow(row: any, cell: any, data: any): api.dom.DivEl {
        let rowEl = new api.dom.DivEl();
        rowEl.getEl().setData('width', data.width);
        rowEl.getEl().setData('height', data.height);
        rowEl.getEl().setData('units', data.units);

        let icon = new api.ui.FontIcon('icon-' + data.device_type);

        let title = new api.dom.H5El();
        title.getEl().setInnerHtml(data.name);

        let subtitle = new api.dom.H6El();
        let units = data.display_units ? data.units : '';
        subtitle.getEl().setInnerHtml(data.width + units + ' &times; ' + data.height + units, false);
        rowEl.appendChild(icon);
        rowEl.appendChild(title);
        rowEl.appendChild(subtitle);

        if (data.rotatable === true) {
            let rotator = new api.dom.DivEl();
            rotator.addClass('rotate');
            rotator.addClassEx('icon-loop');
            rowEl.appendChild(rotator);
        }

        return rowEl;
    }

    static toSlickData(data: any[]): any[] {
        let result = [];
        let i = 1;
        data['devices'].forEach((item, index) => {
            let tmp = {
                id: i,
                device: item
            };
            result.push(tmp);
            i++;
        });
        return result;
    }

}
