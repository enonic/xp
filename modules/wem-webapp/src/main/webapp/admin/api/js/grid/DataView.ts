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

        setItems(data:any, objectIdProperty?: string) {
            this.slickDataView.setItems(data, objectIdProperty);
        }

        addItem(item:any) {
            this.slickDataView.addItem(item);
        }

        getItem(index: number):any {
            return this.slickDataView.getItem(index);
        }

        getItemById(id: string): any {
            return this.slickDataView.getItemById(id);
        }

        getLength(): number {
            return this.slickDataView.getLength();
        }

        getRowById(id:string):number {
            return this.slickDataView.getRowById(id);
        }

        subscribeOnRowsChanged(callback:(e, args) => void) {
            this.slickDataView.onRowsChanged.subscribe(callback);
        }

        subscribeOnRowCountChanged(callback:(e, args) => void) {
            this.slickDataView.onRowCountChanged.subscribe(callback);
        }
    }
}