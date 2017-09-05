import '../../../../api.ts';
import {EmulatorDeviceItem} from './EmulatorDevice';

export class EmulatorGrid extends api.ui.grid.Grid<any> {

    protected createOptions(): api.ui.grid.GridOptions<any> {
        return new api.ui.grid.GridOptionsBuilder().setHideColumnHeaders(true).setRowHeight(50).setHeight('450px').setWidth('320px')
            .build();
    }

    protected createColumns(): api.ui.grid.GridColumn<any>[] {
        return [new api.ui.grid.GridColumnBuilder().setName('device').setField('device').setId('device').setWidth(320).setCssClass(
            'grid-row').setFormatter((row, cell, value, columnDef, dataContext) => {
            return this.buildRow(row, cell, value).toString();
        }).build()
        ];
    }

    private buildRow(row: any, cell: any, data: EmulatorDeviceItem): api.dom.DivEl {
        let rowEl = new api.dom.DivEl();
        rowEl.getEl().setData('width', data.getWidth().toString());
        rowEl.getEl().setData('height', data.getHeight().toString());
        rowEl.getEl().setData('units', data.getUnits());

        let icon = new api.ui.FontIcon('icon-' + data.getDeviceType());

        let title = new api.dom.H5El();
        title.getEl().setInnerHtml(data.getName());

        let subtitle = new api.dom.H6El();
        let units = data.getDisplayUnits() ? data.getUnits() : '';
        subtitle.getEl().setInnerHtml(data.getWidth().toString() + units + ' &times; ' + data.getHeight().toString() + units, false);
        rowEl.appendChild(icon);
        rowEl.appendChild(title);
        rowEl.appendChild(subtitle);

        if (data.getRotatable() === true) {
            let rotator = new api.dom.DivEl();
            rotator.addClass('rotate');
            rotator.addClassEx('icon-loop');
            rowEl.appendChild(rotator);
        }

        return rowEl;
    }

}
