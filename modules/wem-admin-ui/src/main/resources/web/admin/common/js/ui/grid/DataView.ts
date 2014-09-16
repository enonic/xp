module api.ui.grid {

    export class DataView<T extends Slick.SlickData> {

        private slickDataView:Slick.Data.DataView<T>;

        constructor() {
            this.slickDataView = new Slick.Data.DataView<T>({ inlineFilters: true });
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

        beginUpdate() {
            this.slickDataView.beginUpdate();
        }

        endUpdate() {
            this.slickDataView.endUpdate();
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

        insertItem(insertBefore: number, item: T) {
            this.slickDataView.insertItem(insertBefore, item);
        }

        updateItem(id: string, item: T) {
            this.slickDataView.updateItem(id, item);
        }

        deleteItem(id: string) {
            this.slickDataView.deleteItem(id);
        }

        syncGridSelection(grid: Slick.Grid<T>, preserveHidden: boolean) {
            this.slickDataView.syncGridSelection(grid, preserveHidden);
        }

        getItem(index: number):T {
            return this.slickDataView.getItem(index);
        }

        getItems(): T[] {
            return this.slickDataView.getItems();
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

        onRowsChanged(callback:(eventData:Slick.EventData, args) => void) {
            this.slickDataView.onRowsChanged.subscribe(callback);
        }

        onRowCountChanged(listener:(eventData:Slick.EventData, args) => void) {
            this.slickDataView.onRowCountChanged.subscribe(listener);
        }
    }
}