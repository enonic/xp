module app_contextwindow_image {
    export class RecentGrid extends api_ui_grid.Grid<api_content.ContentSummary> {


        constructor(dataView:api_ui_grid.DataView<api_content.ContentSummary>) {
            super(dataView, this.createColumns(), {hideColumnHeaders: true, rowHeight: 50, height: 400, width: 320});
        }

        private createColumns():api_ui_grid.GridColumn<api_content.ContentSummary>[] {
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

        private buildRow(row, cell, data, columnDef, dataContext):api_dom.DivEl {
            var row = new api_dom.DivEl();

            var image = new api_dom.ImgEl(dataContext.getIconUrl());

            var title = new api_dom.H5El();
            title.getEl().setInnerHtml(dataContext.getDisplayName());

            var subtitle = new api_dom.H6El();
            subtitle.getEl().setInnerHtml(dataContext.path.refString);

            row.appendChild(image);
            row.appendChild(title);
            row.appendChild(subtitle);

            return row;
        }

    }


}

