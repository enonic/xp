module api.ui.grid {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;

    export class Grid<T extends Slick.SlickData> extends api.dom.DivEl {

        private defaultHeight = 400;

        private defaultWidth = 800;

        private defaultAutoRenderGridOnDataChanges = true;

        private checkableRows: boolean;

        private dragAndDrop: boolean;

        private slickGrid: Slick.Grid<T>;

        private dataView: DataView<T>;

        private checkboxSelectorPlugin;

        private rowManagerPlugin;

        private loadMask: api.ui.mask.LoadMask;

        constructor(dataView: DataView<T>, columns: GridColumn<T>[], options?: GridOptions<T>) {
            super("grid");

            options = new GridOptionsBuilder<T>(options).build();

            if (options.isHideColumnHeaders()) {
                this.addClass("no-header");
            }

            this.checkboxSelectorPlugin = null;
            this.checkableRows = options.isCheckableRows() || false;
            this.dragAndDrop = options.isDragAndDrop() || false;
            if (this.checkableRows) {
                this.checkboxSelectorPlugin = new Slick.CheckboxSelectColumn({
                    cssClass: "slick-cell-checkboxsel",
                    width: 40
                });
                columns.unshift(this.checkboxSelectorPlugin.getColumnDefinition());
            }
            if (this.dragAndDrop) {
                this.rowManagerPlugin = new Slick.RowMoveManager({
                    cancelEditOnDrag: true
                });
            }
            this.getEl().setHeight((options.getHeight() || this.defaultHeight) + "px");
            this.getEl().setWidth((options.getWidth() || this.defaultWidth) + "px");
            this.dataView = dataView;
            this.slickGrid = new Slick.Grid<T>(this.getHTMLElement(), dataView.slick(), columns, options);
            if (options.isAutoRenderGridOnDataChanges() ||
                (options.isAutoRenderGridOnDataChanges() == undefined && this.defaultAutoRenderGridOnDataChanges)) {
                this.autoRenderGridOnDataChanges(this.dataView);
            }
            if (this.checkboxSelectorPlugin != null) {
                this.slickGrid.registerPlugin(this.checkboxSelectorPlugin);
            }
            if (this.rowManagerPlugin != null) {
                this.slickGrid.registerPlugin(this.rowManagerPlugin);
            }

            this.onRemoved((event) => {
                ResponsiveManager.unAvailableSizeChanged(this);
            });

            // The only way to dataIdProperty before adding items
            this.dataView.setItems([], options.getDataIdProperty());
        }

        mask() {
            if (this.isVisible()) {
                if (this.loadMask) {
                    this.loadMask.show();
                }
                else { //lazy mask init
                    if (this.getParentElement()) {
                        this.createLoadMask();
                        this.loadMask.show();
                    }
                    else {
                        this.onAdded(() => {
                            this.createLoadMask();
                        });
                    }
                }
            }
        }

        unmask() {
            if (this.loadMask) {
                this.loadMask.hide();
            }

        }

        private autoRenderGridOnDataChanges(dataView: DataView<T>) {

            dataView.onRowCountChanged((eventData: Slick.EventData, args) => {
                this.updateRowCount();
                this.render();
            });

            dataView.onRowsChanged((eventData: Slick.EventData, args) => {
                this.invalidateRows(args.rows);
                this.render();
            });
        }

        private createLoadMask() {
            this.loadMask = new api.ui.mask.LoadMask(this);
            this.getParentElement().appendChild(this.loadMask);
        }

        setSelectionModel(selectionModel: Slick.SelectionModel<T, any>) {
            this.slickGrid.setSelectionModel(selectionModel);
        }

        getDataView(): DataView<T> {
            return this.dataView;
        }

        setColumns(columns: GridColumn<T>[]) {
            if (this.checkboxSelectorPlugin) {
                columns.unshift(this.checkboxSelectorPlugin.getColumnDefinition());
            }
            this.slickGrid.setColumns(columns);
        }

        getColumns(): GridColumn<T>[] {
            return <GridColumn<T>[]>this.slickGrid.getColumns();
        }

        setFilter(f: (item: any, args: any) => boolean) {
            this.dataView.setFilter(f);
        }

        setOptions(options: GridOptions<T>) {
            this.slickGrid.setOptions(options);
        }

        getOptions(): GridOptions<T> {
            return <GridOptions<T>>this.slickGrid.getOptions();
        }

        getCheckboxSelectorPlugin(): Slick.Plugin<T> {
            return this.checkboxSelectorPlugin;
        }

        registerPlugin(plugin: Slick.Plugin<T>) {
            this.slickGrid.registerPlugin(plugin);
        }

        unregisterPlugin(plugin: Slick.Plugin<T>) {
            this.slickGrid.unregisterPlugin(plugin);
        }

        render() {
            this.slickGrid.render();
            super.render();
        }

        renderGrid() {
            this.slickGrid.render();
        }

        resizeCanvas() {
            this.slickGrid.resizeCanvas();
        }

        updateRowCount() {
            this.slickGrid.updateRowCount();
        }

        invalidateRows(rows: number[]) {
            this.slickGrid.invalidateRows(rows);
        }

        invalidate() {
            this.slickGrid.invalidate();
        }

        syncGridSelection(preserveHidden: boolean) {
            this.dataView.syncGridSelection(this.slickGrid, preserveHidden);
        }

        focus() {
            this.slickGrid.focus();
        }

        setOnClick(callback: (event, data: GridOnClickData) => void) {
            this.slickGrid.onClick.subscribe((event, data) => {
                event.stopPropagation();
                callback(event, <GridOnClickData>data);
            });
        }

        setOnKeyDown(callback: (event) => void) {
            this.slickGrid.onKeyDown.subscribe((event) => {
                event.stopPropagation();
                callback(event);
            });
        }

        getSelectedRows(): number[] {
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

        setSelectedRows(rows: number[]) {
            this.slickGrid.setSelectedRows(rows);
        }

        selectRow(row: number) {
            // Prevent unnecessary render on the same row
            var rows = this.getSelectedRows();
            if (rows.length > 1 || (rows.length < 2 && rows.indexOf(row) < 0)) {
                this.slickGrid.setSelectedRows([row]);
            }
        }

        addSelectedRow(row: number) {
            var rows = this.getSelectedRows();
            if (rows.indexOf(row) < 0) {
                rows.push(row);
                this.setSelectedRows(rows);
            }
        }

        addSelectedRows(rowsToAdd: number[]) {
            var rows = this.getSelectedRows();
            rowsToAdd.forEach((row) => {
                if (rows.indexOf(row) < 0) {
                    rows.push(row);
                }
            });

            this.setSelectedRows(rows);
        }

        toggleRow(row: number): number {
            // Prevent unnecessary render on the same row
            var rows = this.getSelectedRows(),
                index = rows.indexOf(row);
            if (index < 0) {
                rows.push(row);
                rows.sort((a, b) => { return a - b; });
            } else {
                rows.splice(index, 1);
            }
            this.slickGrid.setSelectedRows(rows);

            return index;
        }

        isRowSelected(row: number): boolean {
            var rows = this.getSelectedRows(),
                index = rows.indexOf(row);

            return index >= 0;
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

        getCellFromEvent(e: Slick.Event<T>): Slick.Cell {
            return this.slickGrid.getCellFromEvent(e);
        }

        moveSelectedUp() {
            if (this.slickGrid.getDataLength() > 0) {
                var selected: number[] = this.getSelectedRows().sort((a, b) => { return a - b; });
                var row = selected.length >= 1
                    ? selected[0] - 1
                    : -1;

                if (selected.length === 1) {
                    if (row >= 0) {
                        this.selectRow(row);
                        return row;
                    } else {
                        this.clearSelection();
                        return 0;
                    }
                } else if (selected.length > 1) {
                    row = Math.max(row, 0);
                    this.selectRow(row);
                    return row;
                }
            }

            return -1;
        }

        moveSelectedDown() {
            if (this.slickGrid.getDataLength() > 0) {
                var selected: number[] = this.getSelectedRows().sort((a, b) => { return a - b; });
                var row = selected.length >= 1
                    ? Math.min(selected[selected.length - 1] + 1, this.slickGrid.getDataLength() - 1)
                    : 0;

                this.selectRow(row);

                return row;
            }

            return -1;
        }

        addSelectedUp() {
            if (this.slickGrid.getDataLength() > 0) {
                var selected: number[] = this.getSelectedRows().sort((a, b) => { return a - b; });

                if (selected.length > 0 && (selected[0] - 1) >= 0) {
                    var row = selected[0] - 1;
                    selected.push(row);
                    selected = selected.sort((a, b) => { return a - b; });
                    this.setSelectedRows(selected);
                    return row;
                }
            }

            return -1;
        }

        addSelectedDown(): number {
            if (this.slickGrid.getDataLength() > 0) {
                var selected: number[] = this.getSelectedRows().sort((a, b) => { return a - b; });

                if (selected.length > 0 && (selected[selected.length - 1] + 1) < this.slickGrid.getDataLength()) {
                    var row = selected[selected.length - 1] + 1;
                    selected.push(row);
                    this.setSelectedRows(selected);
                    return row;
                } else if (selected.length === 0) {
                    this.moveSelectedDown();
                    return 0;
                }
            }

            return -1;
        }

        // Operate with cells
        navigateUp() {
            this.slickGrid.navigateUp();
        }

        // Operate with cells
        navigateDown() {
            this.slickGrid.navigateDown();
        }

        getActiveCell(): Slick.Cell {
            return this.slickGrid.getActiveCell();
        }

        setActiveCell(row: number, cell: number) {
            this.slickGrid.setActiveCell(row, cell);
        }

        setCellCssStyles(key: string, hash: Slick.CellCssStylesHash) {
            this.slickGrid.setCellCssStyles(key, hash);
        }

        removeCellCssStyles(key: string) {
            this.slickGrid.removeCellCssStyles(key);
        }

        getCellCssStyles(key: string): Slick.CellCssStylesHash {
            return this.slickGrid.getCellCssStyles(key);
        }

        /*
         Returns the DIV element matching class grid-canvas,
         which contains every data row currently being rendered in the DOM.
         */
        getCanvasNode(): HTMLCanvasElement {
            return this.slickGrid.getCanvasNode();
        }

        /*
         Returns an object representing information about the grid's position on the page.
         */
        getGridPosition(): Slick.CellPosition {
            return this.slickGrid.getGridPosition();
        }

        /*
         If passed no arguments, returns an object that tells you the range of rows (by row number)
         currently being rendered, as well as the left/right range of pixels currently rendered.
         */
        getRenderedRange(viewportTop?, viewportLeft?): Slick.Viewport {
            return this.slickGrid.getRenderedRange(viewportTop, viewportLeft);
        }

        /*
         Returns an object telling you which rows are currently being displayed on the screen,
         and also the pixel offsets for left/right scrolling.
         */
        getViewport(viewportTop?, viewportLeft?): Slick.Viewport {
            return this.slickGrid.getViewport(viewportTop, viewportLeft);
        }

        subscribeOnSelectedRowsChanged(callback: (e, args) => void) {
            this.slickGrid.onSelectedRowsChanged.subscribe(callback);
        }

        subscribeOnClick(callback: (e, args) => void) {
            this.slickGrid.onClick.subscribe(callback);
        }

        unsubscribeOnClick(callback: (e, args) => void) {
            this.slickGrid.onClick.unsubscribe(callback);
        }

        subscribeOnDblClick(callback: (e, args) => void) {
            this.slickGrid.onDblClick.subscribe(callback);
        }

        subscribeOnContextMenu(callback: (e, args) => void) {
            this.slickGrid.onContextMenu.subscribe(callback);
        }

        subscribeOnDrag(callback: (e, args) => void) {
            this.slickGrid.onDrag.subscribe(callback);
        }

        subscribeOnDragInit(callback: (e, args) => void) {
            this.slickGrid.onDragInit.subscribe(callback);
        }

        subscribeOnDragEnd(callback: (e, args) => void) {
            this.slickGrid.onDragEnd.subscribe(callback);
        }

        subscribeBeforeMoveRows(callback: (e, args) => void) {
            if (this.rowManagerPlugin) {
                this.rowManagerPlugin.onBeforeMoveRows.subscribe(callback);
            }
        }

        subscribeMoveRows(callback: (e, args) => void) {
            if (this.rowManagerPlugin) {
                this.rowManagerPlugin.onMoveRows.subscribe(callback);
            }
        }

        subscribeOnScroll(callback: (e) => void) {
            if (this.getHTMLElement().addEventListener) {
                this.getHTMLElement().addEventListener('DOMMouseScroll', callback, false); // firefox
                this.getHTMLElement().addEventListener('mousewheel', callback, false);     // chrome
            }
        }
    }
}