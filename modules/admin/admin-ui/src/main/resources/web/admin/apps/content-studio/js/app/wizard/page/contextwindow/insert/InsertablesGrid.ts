import "../../../../../api.ts";
import {Insertable} from "./Insertable";

export interface InsertablesGridOptions {
    draggableRows?: boolean;
    rowClass?: string;
    onClick?: (event: MouseEvent) => void;
}

export class InsertablesGrid extends api.ui.grid.Grid<Insertable> {

    private componentGridOptions: InsertablesGridOptions;

    constructor(dataView: api.ui.grid.DataView<Insertable>, options: InsertablesGridOptions = {}) {

        super(dataView);

        this.componentGridOptions = options;

        this.onRendered((event) => {
            if (this.componentGridOptions.onClick) {
                this.setOnClick(this.componentGridOptions.onClick);
            }
        });
    }

    protected createOptions(): api.ui.grid.GridOptions<any> {
        return new api.ui.grid.GridOptionsBuilder().setHideColumnHeaders(true).setRowHeight(50).setHeight(400).setWidth(320).build();
    }

    protected createColumns(): api.ui.grid.GridColumn<Insertable>[] {
        return [
            new api.ui.grid.GridColumnBuilder().setName('component').setField('component').setId('component').setWidth(320).setCssClass(
                'grid-row').setFormatter((row, cell, value, columnDef, dataContext) => {
                return this.buildRow(row, cell, value, columnDef, <Insertable>dataContext).toString();
            }).build()
        ];
    }

    private buildRow(row: number, cell: number, value: any, columnDef: any, insertable: Insertable): api.dom.DivEl {
        let rowEl = new api.dom.DivEl();
        rowEl.getEl().setData('portal-component-type', insertable.getName());
        if (this.componentGridOptions.draggableRows) {
            rowEl.getEl().setData('context-window-draggable', 'true');
        }
        if (this.componentGridOptions.rowClass) {
            rowEl.addClass(this.componentGridOptions.rowClass);
        }

        let icon = new api.ui.FontIcon(insertable.getIconCls());

        let title = new api.dom.H5El();
        title.getEl().setInnerHtml(insertable.getDisplayName());

        let subtitle = new api.dom.H6El();
        subtitle.getEl().setInnerHtml(insertable.getDescription());

        rowEl.appendChild(icon);
        rowEl.appendChild(title);
        rowEl.appendChild(subtitle);

        return rowEl;
    }
}
