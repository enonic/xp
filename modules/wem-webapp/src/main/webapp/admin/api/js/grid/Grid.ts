module api_grid {
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
        private data:any[];
        private columns:Slick.Column[];
        private slickGrid:Slick.Grid<any>;
        private options:GridOptions;
        private dataView:DataView;

        constructor(data:any, columns:GridColumn[], options?:GridOptions) {
            super("Grid");
            this.addClass("grid");

            this.data = data;
            this.columns = columns;
            this.options = options ? options : {};

            this.getEl().setHeight((this.options.height ? this.options.height : this.defaultHeight) + "px");
            this.getEl().setWidth((this.options.width ? this.options.width : this.defaultWidth) + "px");
            this.dataView = new DataView(this);
            this.slickGrid = new Slick.Grid(this.getHTMLElement(), this.dataView.slick(), this.columns, this.options);

        }

        setFilter(f:(item:any, args:any) => boolean) {
            this.dataView.setFilter(f);
        }


        afterRender() {
            if (this.options) {
                if (this.options.hideColumnHeaders) {
                    jQuery(".slick-header-columns").css("height", "0px");
                }
            }
            this.options && this.options.dataIdProperty
                ? this.dataView.setItems(this.data, this.options.dataIdProperty)
                : this.dataView.setItems(this.data) ;
        }

        setData(data:any) {
            this.data = data;
        }

        updateData(data:any) {
            this.data = data;
            this.dataView.setItems(data);
            this.dataView.refresh();
        }

        addItem(item:any) {
            if (this.isRendered()) {
                this.dataView.addItem(item)
            } else {
                this.data.push(item);
            }
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