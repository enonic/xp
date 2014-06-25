module api.ui.grid {

    export interface GridOptions extends Slick.GridOptions<any> {

        hideColumnHeaders?:boolean;

        width?:number;

        height?:number;

        dataIdProperty?:string;

        autoRenderGridOnDataChanges?:boolean

        checkableRows?:boolean;
    }

    export interface GridColumn<T extends Slick.SlickData> extends Slick.Column<T> {

    }

    export interface GridOnClickData {
        row:number;
        cell:number;
        grid?:any;
    }

    export class Grid<T extends Slick.SlickData> extends api.dom.DivEl {

        private defaultHeight = 400;

        private defaultWidth = 800;

        private defaultAutoRenderGridOnDataChanges = true;

        private checkableRows:boolean;

        private slickGrid:Slick.Grid<T>;

        private dataView:DataView<T>;

        private checkboxSelectorPlugin;

        constructor(dataView:DataView<T>, columns:GridColumn<T>[], options:GridOptions = {}) {
            super("grid");

            if (options.hideColumnHeaders) {
                this.addClass("no-header");
            }

            this.checkboxSelectorPlugin = null;
            this.checkableRows = options.checkableRows || false;
            if (this.checkableRows) {
                this.checkboxSelectorPlugin = new Slick.CheckboxSelectColumn({
                    cssClass: "slick-cell-checkboxsel",
                    width: 40
                });
                columns.unshift(this.checkboxSelectorPlugin.getColumnDefinition());
            }

            this.getEl().setHeight((options.height || this.defaultHeight) + "px");
            this.getEl().setWidth((options.width || this.defaultWidth) + "px");
            this.dataView = dataView;
            this.slickGrid = new Slick.Grid<T>(this.getHTMLElement(), dataView.slick(), columns, options);
            if (options.autoRenderGridOnDataChanges || this.defaultAutoRenderGridOnDataChanges) {
                this.autoRenderGridOnDataChanges(this.dataView);
            }
            if (this.checkboxSelectorPlugin != null) {
                this.slickGrid.registerPlugin(this.checkboxSelectorPlugin);
            }

            ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.ResponsiveItem) => {
                this.resizeCanvas();
            });

            this.onRemoved((event) => {
                ResponsiveManager.unAvailableSizeChanged(this);
            });

            // The only way to dataIdProperty before adding items
            this.dataView.setItems([], options.dataIdProperty);
        }

        private autoRenderGridOnDataChanges(dataView:DataView<T>) {

            dataView.onRowCountChanged((eventData:Slick.EventData, args) => {
                this.updateRowCount();
                this.render();
            });

            dataView.onRowsChanged((eventData:Slick.EventData, args) => {
                this.invalidateRows(args.rows);
                this.render();
            });
        }

        setSelectionModel(selectionModel:Slick.SelectionModel<T, any>) {
            this.slickGrid.setSelectionModel(selectionModel);
        }

        getDataView():DataView<T> {
            return this.dataView;
        }

        setColumns(columns:GridColumn<T>[]) {
            if (this.checkboxSelectorPlugin) {
                columns.unshift(this.checkboxSelectorPlugin.getColumnDefinition());
            }
            this.slickGrid.setColumns(columns);
        }

        setFilter(f:(item:any, args:any) => boolean) {
            this.dataView.setFilter(f);
        }

        setOptions(options:GridOptions) {
            this.slickGrid.setOptions(options);
        }

        render() {
            this.slickGrid.render();
            super.render();
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

        syncGridSelection(preserveHidden: boolean) {
            this.dataView.syncGridSelection(this.slickGrid, preserveHidden);
        }

        focus() {
            this.slickGrid.focus();
        }

        setOnClick(callback:(event, data:GridOnClickData) => void) {
            this.slickGrid.onClick.subscribe((event, data) => {
                event.stopPropagation();
                callback(event, data);
            });
        }

        setOnKeyDown(callback:(event) => void) {
            this.slickGrid.onKeyDown.subscribe((event) => {
                event.stopPropagation();
                callback(event);
            });
        }

        getSelectedRows():number[] {
            return this.slickGrid.getSelectedRows();
        }

        getSelectedRowItems(): T[] {
            var rowItems: T[] = [];
            var rows = this.getSelectedRows();
            rows.forEach((rowIndex: number) => {
                rowItems.push(this.dataView.getItem(rowIndex));
            });
            return rowItems;
        }

        setSelectedRows(rows:number[]) {
            this.slickGrid.setSelectedRows(rows);
        }

        selectRow(row) {
            // Prevent unnecessary render on the same row
            if (this.getSelectedRows().length > 1
                || (this.getSelectedRows().length < 2 && this.getSelectedRows().indexOf(row) < 0)) {
                this.slickGrid.setSelectedRows([row]);
            }
        }

        selectAll() {
            var rows = [];
            for (var i = 0; i < this.slickGrid.getDataLength(); i++) {
                rows.push(i);
            }
            this.setSelectedRows(rows);
        }

        clearSelection() {
            this.setSelectedRows([]);
        }

        isAllSelected(): boolean {
            return this.slickGrid.getDataLength() === this.getSelectedRows().length;
        }

        resetActiveCell() {
            if (this.slickGrid.getActiveCell()) {
                this.slickGrid.resetActiveCell();
            }
        }

        moveSelectedUp() {
            if (this.slickGrid.getDataLength() > 0) {
                var selected:number[] = this.getSelectedRows().sort();
                var row = selected.length >= 1
                    ? selected[0] - 1
                    : -1;

                if (selected.length === 1) {
                    if (row >= 0) {
                        this.selectRow(row);
                    } else {
                        this.clearSelection();
                    }
                } else if (selected.length > 1) {
                    row = Math.max(row, 0);
                    this.selectRow(row);
                }
            }
        }

        moveSelectedDown() {
            if (this.slickGrid.getDataLength() > 0) {
                var selected:number[] = this.getSelectedRows().sort();
                var row = selected.length >= 1
                    ? Math.min(selected[selected.length - 1] + 1, this.slickGrid.getDataLength() - 1)
                    : 0;

                this.selectRow(row);
            }
        }

        // Operate with cells
        navigateUp() {
            this.slickGrid.navigateUp();
        }

        // Operate with cells
        navigateDown() {
            this.slickGrid.navigateDown();
        }

        getActiveCell():Slick.Cell {
            return this.slickGrid.getActiveCell();
        }

        setActiveCell(row:number, cell:number) {
            this.slickGrid.setActiveCell(row, cell);
        }

        setCellCssStyles(key:string, hash:Slick.CellCssStylesHash) {
            this.slickGrid.setCellCssStyles(key, hash);
        }

        getCellCssStyles(key:string):Slick.CellCssStylesHash {
            return this.slickGrid.getCellCssStyles(key);
        }

        subscribeOnSelectedRowsChanged(callback:(e, args) => void) {
            this.slickGrid.onSelectedRowsChanged.subscribe(callback);
        }

        subscribeOnClick(callback:(e, args) => void) {
            this.slickGrid.onClick.subscribe(callback);
        }

        subscribeOnDblClick(callback:(e, args) => void) {
            this.slickGrid.onDblClick.subscribe(callback);
        }
    }
}