module api.ui.grid {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;

    export class Grid<T extends Slick.SlickData> extends api.dom.DivEl {

        private defaultHeight: number = 400;

        private defaultWidth: number = 800;

        private defaultAutoRenderGridOnDataChanges: boolean = true;

        private checkableRows: boolean;

        private dragAndDrop: boolean;

        private slickGrid: Slick.Grid<T>;

        private dataView: DataView<T>;

        private checkboxSelectorPlugin: Slick.CheckboxSelectColumn<T>; // CheckboxSelectColumn

        private rowManagerPlugin: Slick.RowMoveManager<T>; // RowMoveManager

        private loadMask: api.ui.mask.LoadMask;

        private debounceSelectionChange: boolean;

        public static debug: boolean = false;

        constructor(dataView: DataView<T>, gridColumns?: GridColumn<T>[], gridOptions?: GridOptions<T>) {
            super('grid');

            let options = gridOptions || this.createOptions();
            let columns = gridColumns || this.createColumns();

            if (options.isHideColumnHeaders()) {
                this.addClass('no-header');
            }

            this.checkboxSelectorPlugin = null;
            this.checkableRows = options.isCheckableRows() || false;
            this.dragAndDrop = options.isDragAndDrop() || false;
            if (this.checkableRows) {
                this.checkboxSelectorPlugin = new Slick.CheckboxSelectColumn({
                    cssClass: 'slick-cell-checkboxsel',
                    width: 40
                });
                columns.unshift(<GridColumn<T>>this.checkboxSelectorPlugin.getColumnDefinition());
            }
            if (this.dragAndDrop) {
                this.rowManagerPlugin = new Slick.RowMoveManager({
                    cancelEditOnDrag: true
                });
            }
            this.getEl().setHeight((options.getHeight() || this.defaultHeight) + 'px');
            this.getEl().setWidth((options.getWidth() || this.defaultWidth) + 'px');
            this.dataView = dataView;
            this.slickGrid = new Slick.Grid<T>(this.getHTMLElement(), dataView.slick(), columns, options);
            if (options.isAutoRenderGridOnDataChanges() ||
                (options.isAutoRenderGridOnDataChanges() == null && this.defaultAutoRenderGridOnDataChanges)) {
                this.autoRenderGridOnDataChanges(this.dataView);
            }
            if (this.checkboxSelectorPlugin != null) {
                this.slickGrid.registerPlugin(<Slick.Plugin<T>>this.checkboxSelectorPlugin);
            }
            if (this.rowManagerPlugin != null) {
                this.slickGrid.registerPlugin(<Slick.Plugin<T>>this.rowManagerPlugin);
            }

            this.onRemoved((event) => {
                ResponsiveManager.unAvailableSizeChanged(this);
            });

            // The only way to dataIdProperty before adding items
            this.dataView.setItems([], options.getDataIdProperty());
        }

        protected createOptions(): api.ui.grid.GridOptions<any> {
            return new GridOptionsBuilder<T>().build();
        }

        protected createColumns(): api.ui.grid.GridColumn<any>[] {
            throw 'Must be implemented by inheritors';
        }

        setItemMetadata(metadataHandler: () => void) {
            this.dataView.setItemMetadataHandler(metadataHandler);
        }

        mask() {
            if (this.isVisible()) {
                if (this.loadMask) {
                    this.loadMask.show();
                } else { //lazy mask init
                    if (this.getParentElement()) {
                        this.createLoadMask();
                        this.loadMask.show();
                    } else {
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
                this.renderGrid();
            });

            dataView.onRowsChanged((eventData: Slick.EventData, args) => {
                this.invalidateRows(args.rows);
                this.renderGrid();
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

        setColumns(columns: GridColumn<T>[], toBegin: boolean = false) {
            if (this.checkboxSelectorPlugin) {
                let pluginColumn = this.checkboxSelectorPlugin.getColumnDefinition();
                toBegin ? columns.push(<GridColumn<T>>pluginColumn) : columns.unshift(<GridColumn<T>>pluginColumn);
            }
            this.slickGrid.setColumns(columns);
        }

        getColumns(): GridColumn<T>[] {
            return <GridColumn<T>[]>this.slickGrid.getColumns();
        }

        getColumnIndex(id: string): number {
            return this.slickGrid.getColumnIndex(id);
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

        getCheckboxSelectorPlugin(): Slick.CheckboxSelectColumn<T> {
            return this.checkboxSelectorPlugin;
        }

        registerPlugin(plugin: Slick.Plugin<T>) {
            this.slickGrid.registerPlugin(plugin);
        }

        unregisterPlugin(plugin: Slick.Plugin<T>) {
            this.slickGrid.unregisterPlugin(plugin);
        }

        doRender() {
            if (Grid.debug) {
                console.debug('Grid.doRender');
            }
            return super.doRender().then((rendered) => {
                this.renderGrid();
                return rendered;
            });
        }

        renderGrid() {
            if (Grid.debug) {
                console.debug('Grid.renderGrid');
            }
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

        setOnClick(callback: (event: any, data: GridOnClickData) => void) {
            this.slickGrid.onClick.subscribe((event, data) => {
                event.stopPropagation();
                callback(event, <GridOnClickData>data);
            });
        }

        setOnKeyDown(callback: (event: any) => void) {
            this.slickGrid.onKeyDown.subscribe((event) => {
                event.stopPropagation();
                callback(event);
            });
        }

        getSelectedRows(): number[] {
            return this.slickGrid.getSelectedRows();
        }

        getSelectedRowItems(): T[] {
            let rowItems: T[] = [];
            let rows = this.getSelectedRows();
            rows.forEach((rowIndex: number) => {
                rowItems.push(this.dataView.getItem(rowIndex));
            });
            return rowItems;
        }

        setSelectedRows(rows: number[], debounce?: boolean) {
            this.debounceSelectionChange = debounce;
            this.slickGrid.setSelectedRows(rows);
        }

        selectRow(row: number, debounce?: boolean): number {
            // Prevent unnecessary render on the same row
            let rows = this.getSelectedRows();
            if (rows.length > 1 || (rows.length < 2 && rows.indexOf(row) < 0)) {
                this.setSelectedRows([row], debounce);
                return row;
            }
            return -1;
        }

        addSelectedRow(row: number, debounce?: boolean) {
            let rows = this.getSelectedRows();
            if (rows.indexOf(row) < 0) {
                rows.push(row);
                this.setSelectedRows(rows, debounce);
            }
        }

        addSelectedRows(rowsToAdd: number[], debounce?: boolean) {
            let rows = this.getSelectedRows();
            rowsToAdd.forEach((row) => {
                if (rows.indexOf(row) < 0) {
                    rows.push(row);
                }
            });

            this.setSelectedRows(rows, debounce);
        }

        toggleRow(row: number, debounce?: boolean): number {
            // Prevent unnecessary render on the same row
            let rows = this.getSelectedRows();
            let index = rows.indexOf(row);
            if (index < 0) {
                rows.push(row);
                rows.sort((a, b) => {
                    return a - b;
                });
            } else {
                rows.splice(index, 1);
            }
            this.setSelectedRows(rows, debounce);

            return index;
        }

        isRowSelected(row: number): boolean {
            let rows = this.getSelectedRows();
            let index = rows.indexOf(row);

            return index >= 0;
        }

        clearSelection(debounce?: boolean) {
            this.setSelectedRows([], debounce);
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

        getCellNode(row: number, cell: number): HTMLElement {
            return this.slickGrid.getCellNode(row, cell);
        }

        moveSelectedUp() {
            if (this.slickGrid.getDataLength() > 0) {
                let selected: number[] = this.getSelectedRows().sort((a, b) => {
                    return a - b;
                });
                let row = selected.length >= 1
                    ? selected[0] - 1
                    : -1;

                if (selected.length === 1) {
                    if (row >= 0) {
                        this.selectRow(row, true);
                        return row;
                    } else {
                        this.clearSelection(true);
                        return 0;
                    }
                } else if (selected.length > 1) {
                    row = Math.max(row, 0);
                    this.selectRow(row, true);
                    return row;
                }
            }

            return -1;
        }

        moveSelectedDown() {
            if (this.slickGrid.getDataLength() > 0) {
                let selected: number[] = this.getSelectedRows().sort((a, b) => {
                    return a - b;
                });
                let row = selected.length >= 1
                    ? Math.min(selected[selected.length - 1] + 1, this.slickGrid.getDataLength() - 1)
                    : 0;

                this.selectRow(row, true);

                return row;
            }

            return -1;
        }

        addSelectedUp(startIndex?: number) {
            let row = -1;
            if (this.slickGrid.getDataLength() > 0) {
                let selected: number[] = this.getSelectedRows().sort((a, b) => {
                    return a - b;
                });

                if (selected.length > 0) {
                    let firstSelected = selected[0];
                    if (selected.length > 1 && !isNaN(startIndex) && firstSelected === startIndex) {
                        row = startIndex;
                        selected.pop();
                    }
                    else if (firstSelected - 1 >= 0) {
                        row = selected[0] - 1;
                        selected.push(row);
                        selected = selected.sort((a, b) => {
                            return a - b;
                        });
                    }

                    this.setSelectedRows(selected, true);
                }
            }
            return row;
        }

        addSelectedDown(startIndex?: number): number {
            let row = -1;
            if (this.slickGrid.getDataLength() > 0) {
                let selected: number[] = this.getSelectedRows().sort((a, b) => {
                    return a - b;
                });

                if (selected.length > 0) {
                    let lastSelected = selected[selected.length - 1];
                    if (selected.length > 1 && !isNaN(startIndex) && lastSelected === startIndex) {
                        row = startIndex;
                        selected.shift();
                    }
                    else if (lastSelected + 1 < this.slickGrid.getDataLength()) {
                        row = lastSelected + 1;
                        selected.push(row);
                    }

                    this.setSelectedRows(selected, true);
                } else {
                    row = 0;
                    this.moveSelectedDown();
                }
            }

            return row;
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
        getRenderedRange(viewportTop?: number, viewportLeft?: number): Slick.Viewport {
            return this.slickGrid.getRenderedRange(viewportTop, viewportLeft);
        }

        /*
         Returns an object telling you which rows are currently being displayed on the screen,
         and also the pixel offsets for left/right scrolling.
         */
        getViewport(viewportTop?: number, viewportLeft?: number): Slick.Viewport {
            return this.slickGrid.getViewport(viewportTop, viewportLeft);
        }

        updateCell(row: number, cell: number) {
            return this.slickGrid.updateCell(row, cell);
        }

        updateRow(row: number) {
            return this.slickGrid.updateRow(row);
        }

        subscribeOnSelectedRowsChanged(callback: (e: any, args: any) => void) {
            let debouncedCallback = api.util.AppHelper.debounce(callback, 500, false);
            this.slickGrid.onSelectedRowsChanged.subscribe((e, args) => {
                if (this.debounceSelectionChange) {
                    debouncedCallback(e, args);
                } else {
                    callback(e, args);
                }
            });
        }

        subscribeOnClick(callback: (e: any, args: any) => void) {
            this.slickGrid.onClick.subscribe(callback);
        }

        unsubscribeOnClick(callback: (e: any, args: any) => void) {
            this.slickGrid.onClick.unsubscribe(callback);
        }

        subscribeOnDblClick(callback: (e: any, args: any) => void) {
            this.slickGrid.onDblClick.subscribe(callback);
        }

        unsubscribeOnDblClick(callback: (e: any, args: any) => void) {
            this.slickGrid.onDblClick.unsubscribe(callback);
        }

        subscribeOnContextMenu(callback: (e: any, args: any) => void) {
            this.slickGrid.onContextMenu.subscribe(callback);
        }

        subscribeOnDrag(callback: (e: any, args: any) => void) {
            this.slickGrid.onDrag.subscribe(callback);
        }

        subscribeOnDragInit(callback: (e: any, args: any) => void) {
            this.slickGrid.onDragInit.subscribe(callback);
        }

        subscribeOnDragEnd(callback: (e: any, args: any) => void) {
            this.slickGrid.onDragEnd.subscribe(callback);
        }

        subscribeBeforeMoveRows(callback: (e: any, args: any) => void) {
            if (this.rowManagerPlugin) {
                (<Slick.Event<Slick.OnMoveRowsEventData>>this.rowManagerPlugin.onBeforeMoveRows).subscribe(callback);
            }
        }

        subscribeMoveRows(callback: (e: any, args: any) => void) {
            if (this.rowManagerPlugin) {
                (<Slick.Event<Slick.OnMoveRowsEventData>>this.rowManagerPlugin.onMoveRows).subscribe(callback);
            }
        }

        subscribeOnScroll(callback: (e: any) => void) {
            this.slickGrid.onScroll.subscribe(callback);
        }

        // scrolled event is for the mouse wheel only
        subscribeOnScrolled(callback: (e: Event) => void) {
            if (this.getHTMLElement().addEventListener) {
                this.getHTMLElement().addEventListener('DOMMouseScroll', callback, false); // firefox
                this.getHTMLElement().addEventListener('mousewheel', callback, false);     // chrome
            }
        }

        subscribeOnMouseEnter(callback: (e: any, args: any) => void) {
            this.slickGrid.onMouseEnter.subscribe(callback);
        }

        subscribeOnMouseLeave(callback: (e: any, args: any) => void) {
            this.slickGrid.onMouseLeave.subscribe(callback);
        }
    }
}
