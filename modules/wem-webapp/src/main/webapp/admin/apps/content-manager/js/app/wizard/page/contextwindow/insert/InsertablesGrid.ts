module app.wizard.page.contextwindow.insert {

    export interface InsertablesGridOptions {
        draggableRows?:boolean;
        rowClass?:string;
        onClick?:(el) => void;
    }

    export class InsertablesGrid extends api.ui.grid.Grid<Insertable> {

        private componentGridOptions: InsertablesGridOptions;

        private componentDataView: api.ui.grid.DataView<Insertable>;

        constructor(dataView: api.ui.grid.DataView<Insertable>, options: InsertablesGridOptions = {}) {
            super(dataView, this.createColumns(), {hideColumnHeaders: true, rowHeight: 50, height: 400, width: 320});
            this.componentDataView = dataView;
            this.componentGridOptions = options;

            this.onRendered((event) => {
                if (this.componentGridOptions.onClick) {
                    this.setOnClick(this.componentGridOptions.onClick);
                }
            })
        }

        private createColumns(): api.ui.grid.GridColumn<Insertable>[] {
            return [
                {
                    name: "component",
                    field: "component",
                    id: "component",
                    width: 320,
                    cssClass: "grid-row",
                    formatter: (row, cell, value, columnDef, dataContext) => {
                        return this.buildRow(row, cell, value, columnDef, <Insertable>dataContext).toString();
                    }
                }
            ];
        }

        private buildRow(row: number, cell: number, value: any, columnDef: any, insertable: Insertable): api.dom.DivEl {
            var rowEl = new api.dom.DivEl();
            rowEl.getEl().setData('live-edit-type', insertable.getName());
            rowEl.getEl().setData('live-edit-empty-component', 'true');
            if (this.componentGridOptions.draggableRows) {
                rowEl.getEl().setData('context-window-draggable', 'true');
            }
            if (this.componentGridOptions.rowClass) {
                rowEl.addClass(this.componentGridOptions.rowClass)
            }

            var icon = new api.dom.DivEl();
            icon.setClass('live-edit-font-icon-' + insertable.getIconCls());
            icon.addClass('icon');

            var title = new api.dom.H5El();
            title.getEl().setInnerHtml(insertable.getDisplayName());

            var subtitle = new api.dom.H6El();
            subtitle.getEl().setInnerHtml(insertable.getDescription());

            rowEl.appendChild(icon);
            rowEl.appendChild(title);
            rowEl.appendChild(subtitle);

            return rowEl;
        }
    }
}