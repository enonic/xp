module api_grid {
    export class DataView {
        private slickDataView:Slick.Data.DataView<any[]>;
        private grid:Grid;

        constructor(grid:api_grid.Grid) {
            this.slickDataView = new Slick.Data.DataView({ inlineFilters: true });
            this.grid = grid;

            this.slickDataView.onRowCountChanged.subscribe((e, args) => {
                this.grid.updateRowCount();
                this.grid.render();
            });

            this.slickDataView.onRowsChanged.subscribe((e, args) => {
                this.grid.invalidateRows(args.rows);
                this.grid.render();
            });
        }


        slick():Slick.Data.DataView<any[]> {
            return this.slickDataView;
        }

        setFilter(f:(item:any, args:any) => boolean) {
            this.slickDataView.setFilter(f);
        }

        setFilterArgs(args:any) {
            this.slickDataView.setFilterArgs(args);
        }

        refresh() {
            this.slickDataView.refresh();
        }

        setItems(data:any) {
            this.slickDataView.setItems(data);
        }
    }
}