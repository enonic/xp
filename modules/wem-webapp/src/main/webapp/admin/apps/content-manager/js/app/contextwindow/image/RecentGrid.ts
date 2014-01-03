module app.contextwindow.image {
    export class RecentGrid extends api.ui.grid.Grid<api.content.ContentSummary> {


        constructor(dataView:api.ui.grid.DataView<api.content.ContentSummary>) {
            super(dataView, this.createColumns(), {hideColumnHeaders: true, rowHeight: 50, height: 400, width: 320});
        }

        private createColumns():api.ui.grid.GridColumn<api.content.ContentSummary>[] {
            return [
                {
                    name: "component",
                    field: "component",
                    id: "component",
                    width: 320,
                    cssClass: "grid-row",
                    formatter: (row, cell, value, columnDef, dataContext) => {
                        return this.buildRow(row, cell, value, columnDef, dataContext).toString();
                    }
                }
            ];
        }

        private buildRow(row, cell, data, columnDef, dataContext):api.dom.DivEl {
            var rowEl = new api.dom.DivEl();

            var image = new api.dom.ImgEl(dataContext.getIconUrl());

            var title = new api.dom.H5El();
            title.getEl().setInnerHtml(dataContext.getDisplayName());

            var subtitle = new api.dom.H6El();
            subtitle.getEl().setInnerHtml(api.util.limitString(dataContext.path.refString, 43));

            rowEl.appendChild(image);
            rowEl.appendChild(title);
            rowEl.appendChild(subtitle);

            return rowEl;
        }

    }


}

