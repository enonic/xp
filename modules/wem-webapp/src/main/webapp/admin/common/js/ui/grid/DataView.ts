module api_ui_grid {

    export class DataView<T extends Slick.SlickData> {

        private slickDataView:Slick.Data.DataView<T>;

        constructor() {
            this.slickDataView = new Slick.Data.DataView({ inlineFilters: true });
        }

        slick():Slick.Data.DataView<T> {
            return this.slickDataView;
        }

        setFilter(f:(item:T, args:any) => boolean) {
            this.slickDataView.setFilter(f);
        }

        setFilterArgs(args:any) {
            this.slickDataView.setFilterArgs(args);
        }

        refresh() {
            this.slickDataView.refresh();
        }

        setItems(items:T[], objectIdProperty?: string) {
            this.slickDataView.setItems(items, objectIdProperty);
        }

        addItem(item:T) {
            this.slickDataView.addItem(item);
        }

        getItem(index: number):T {
            return this.slickDataView.getItem(index);
        }

        getItemById(id: string):T {
            return this.slickDataView.getItemById(id);
        }

        getLength(): number {
            return this.slickDataView.getLength();
        }

        getRowById(id:string):number {
            return this.slickDataView.getRowById(id);
        }

        subscribeOnRowsChanged(callback:(eventData:Slick.EventData, args) => void) {
            this.slickDataView.onRowsChanged.subscribe(callback);
        }

        subscribeOnRowCountChanged(listener:(eventData:Slick.EventData, args) => void) {
            this.slickDataView.onRowCountChanged.subscribe(listener);
        }
    }
}