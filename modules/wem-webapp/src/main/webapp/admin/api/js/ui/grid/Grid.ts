module api_ui_grid {
    export interface GridOptions extends Slick.GridOptions<any> {
        hideColumnHeaders?:boolean;
        width?:number;
        height?:number;
        dataIdProperty?:string;
    }

    export interface GridColumn extends Slick.Column<any> {

    }

    export class Grid extends api_dom.DivEl {

        private defaultHeight = 400;
        private defaultWidth = 800;
        private slickGrid:Slick.Grid<any>;
        private dataView:DataView;

        constructor(data:any, columns:GridColumn[], options:GridOptions = {}) {
            super("Grid");
            this.addClass("grid");

            if (options.hideColumnHeaders) {
                this.addClass("no-header");
            }

            this.getEl().setHeight((options.height || this.defaultHeight) + "px");
            this.getEl().setWidth((options.width || this.defaultWidth) + "px");
            this.dataView = new DataView(this);
            this.slickGrid = new Slick.Grid(this.getHTMLElement(), this.dataView.slick(), columns, options);

            if (data) {
                this.dataView.setItems(data, options.dataIdProperty);
            }
        }

        setFilter(f:(item:any, args:any) => boolean) {
            this.dataView.setFilter(f);
        }

        updateData(data:any) {
            this.dataView.setItems(data);
        }

        addItem(item:any) {
            this.dataView.addItem(item);
        }

        getDataView():DataView {
            return this.dataView;
        }

        getDataLength():number {
            return this.slickGrid.getDataLength();
        }

        getDataItem(i:number):any {
            return this.slickGrid.getDataItem(i);
        }

        getDataItemById(id: string):any {
            return this.dataView.getItemById(id);
        }

        setOptions(options:GridOptions) {
            this.slickGrid.setOptions(options);
        }

        render() {
            this.slickGrid.render();
        }

        resizeCanvas() {
            this.slickGrid.resizeCanvas();
        }

        updateRowCount() {
            this.slickGrid.updateRowCount();
        }

        invalidateRows(rows:number[]) {
            this.slickGrid.invalidateRows(rows);
        }

        setOnClick(callback:(cell) => void) {
            this.slickGrid.onClick.subscribe((event, data) => {
                var el = this.slickGrid.getCellNode(data.row, data.cell);
                callback(el);
            });
        }

        getSelectedRows():number[] {
            return this.slickGrid.getSelectedRows();
        }

        setSelectedRows(rows:number[]) {
            this.slickGrid.setSelectedRows(rows);
        }

        setSelectionModel(selectionModel: any) {
            this.slickGrid.setSelectionModel(selectionModel);
        }

        resetActiveCell() {
            this.slickGrid.resetActiveCell();
        }

        navigateUp() {
            this.slickGrid.navigateUp();
        }

        navigateDown() {
            this.slickGrid.navigateDown();
        }

        getActiveCell():Slick.Cell {
            return this.slickGrid.getActiveCell();
        }

        setActiveCell(row:number, cell:number) {
            this.slickGrid.setActiveCell(row, cell);
        }

        setCellCssStyles(key: string, hash: Slick.CellCssStylesHash) {
            this.slickGrid.setCellCssStyles(key, hash);
        }

        getCellCssStyles(key: string):Slick.CellCssStylesHash {
            return this.slickGrid.getCellCssStyles(key);
        }

        subscribeOnSelectedRowsChanged(callback:(e, args) => void) {
            this.slickGrid.onSelectedRowsChanged.subscribe(callback);
        }

        subscribeOnRowsChanged(callback:(e, args) => void) {
            this.dataView.subscribeOnRowsChanged(callback);
        }

        subscribeOnRowCountChanged(callback:(e, args) => void) {
            this.dataView.subscribeOnRowCountChanged(callback);
        }

        subscribeOnClick(callback:(e, args) => void) {
            this.slickGrid.onClick.subscribe(callback);
        }
    }
}