module api.ui.selector {

    import Viewer = api.ui.Viewer;

    export interface DropdownGridConfig<OPTION_DISPLAY_VALUE> {

        maxHeight?: number;

        width: number;

        optionDisplayValueViewer?: Viewer<OPTION_DISPLAY_VALUE>;

        filter: (item: Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        dataIdProperty?:string;

        multipleSelections?: boolean;
    }

    export class DropdownGrid<OPTION_DISPLAY_VALUE> {

        private maxHeight: number;

        private width: number;

        private grid: api.ui.grid.Grid<Option<OPTION_DISPLAY_VALUE>>;

        private gridData: api.ui.grid.DataView<Option<OPTION_DISPLAY_VALUE>>;

        private dataIdProperty: string;

        private optionDisplayValueViewer: Viewer<OPTION_DISPLAY_VALUE>;

        private filter: (item: Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        private rowSelectionListeners: {(event: DropdownGridRowSelectedEvent):void}[];

        private multipleSelectionListeners: {(event: DropdownGridMultipleSelectionEvent):void}[];

        private multipleSelections: boolean;

        constructor(config: DropdownGridConfig<OPTION_DISPLAY_VALUE>) {
            this.rowSelectionListeners = [];
            this.multipleSelectionListeners = [];
            this.maxHeight = config.maxHeight || 200;
            this.optionDisplayValueViewer = config.optionDisplayValueViewer ?
                new (<any>config.optionDisplayValueViewer['constructor'])() : new DefaultOptionDisplayValueViewer();
            this.filter = config.filter;
            this.dataIdProperty = config.dataIdProperty || "value";
            this.maxHeight = config.maxHeight;
            this.width = config.width;
            this.multipleSelections = config.multipleSelections || false;

            var columnFormatter =
                (row: number, cell: number, value: OPTION_DISPLAY_VALUE, columnDef: any, dataContext: Option<OPTION_DISPLAY_VALUE>) => {
                    this.optionDisplayValueViewer.setObject(value);
                    return this.optionDisplayValueViewer.toString();
                };

            var columns: api.ui.grid.GridColumn<Option<OPTION_DISPLAY_VALUE>>[] = [
                new api.ui.grid.GridColumnBuilder().
                    setId("option").
                    setName("Options").
                    setField("displayValue").
                    setFormatter(columnFormatter).
                build()
            ];

            var options: api.ui.grid.GridOptions<Option<OPTION_DISPLAY_VALUE>> =
                new api.ui.grid.GridOptionsBuilder().
                    setWidth(this.width).
                    setHeight(this.maxHeight).
                    setHideColumnHeaders(true).
                    setEnableColumnReorder(false).
                    setFullWidthRows(true).
                    setForceFitColumns(true).
                    setRowHeight(this.optionDisplayValueViewer.getPreferredHeight()).
                    setCheckableRows(this.multipleSelections).
                    setMultiSelect(this.multipleSelections).
                    setDataIdProperty(this.dataIdProperty).
                build();

            this.gridData = new api.ui.grid.DataView<Option<OPTION_DISPLAY_VALUE>>();
            if (this.filter) {
                this.gridData.setFilter(this.filter);
            }
            this.grid = new api.ui.grid.Grid<Option<OPTION_DISPLAY_VALUE>>(this.gridData, columns, options);

            this.grid.addClass("options-container");
            this.grid.getEl().setPosition("absolute");
            this.grid.hide();
            this.grid.setSelectionModel(new Slick.RowSelectionModel({selectActiveRow: false}));


            // Listen to click in grid and issue selection
            this.grid.subscribeOnClick((e, args) => {

                this.notifyRowSelection(args.row);

                e.preventDefault();
                e.stopPropagation();
                return false;
            });

            this.grid.subscribeOnSelectedRowsChanged((e, args) => {
                this.notifyMultipleSelection(args.rows);
            });

            this.gridData.onRowsChanged((e, args) => {
                // this.markSelections();
                // TODO: After refactoring during task CMS-3104, this does not seem to be necessary
                // TODO: Remove this when sure or re-implement
            });

            this.gridData.onRowCountChanged((e, args) => {
                // this.markSelections();
                // TODO: After refactoring during task CMS-3104, this does not seem to be necessary
                // TODO: Remove this when sure or re-implement
            });
        }

        getElement(): api.ui.grid.Grid<Option<OPTION_DISPLAY_VALUE>> {
            return this.grid;
        }

        renderGrid() {
            this.grid.render();
        }

        isVisible(): boolean {
            return this.grid.isVisible();
        }

        show() {
            this.grid.show();
        }

        hide() {
            this.grid.hide();
        }

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[]) {
            this.gridData.setItems(options, this.dataIdProperty);
        }

        removeAllOptions() {
            this.gridData.setItems([]);
        }

        addOption(option: Option<OPTION_DISPLAY_VALUE>) {
            this.gridData.addItem(option);
        }

        hasOptions(): boolean {
            return this.gridData.getLength() > 0;
        }

        getSelectedOptionCount(): number {
            return this.grid.getSelectedRows().length;
        }

        getOptionCount(): number {
            return this.gridData.getLength();
        }

        getOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            return this.gridData.getItems();
        }

        getOptionByValue(value: string): Option<OPTION_DISPLAY_VALUE> {
            return <Option<OPTION_DISPLAY_VALUE>>this.gridData.getItemById(value);
        }

        getOptionByRow(rowIndex: number): Option<OPTION_DISPLAY_VALUE> {
            return <Option<OPTION_DISPLAY_VALUE>>this.gridData.getItem(rowIndex);
        }

        setFilterArgs(args: any) {
            this.gridData.setFilterArgs(args);
            this.gridData.refresh();
        }

        setTopPx(value: number) {
            this.grid.getEl().setTopPx(value);
        }

        setWidthPx(value: number) {
            this.grid.getEl().setWidthPx(value);
        }

        adjustGridHeight() {

            var gridEl = this.grid.getEl();
            var rowsHeight = this.getOptionCount() * this.optionDisplayValueViewer.getPreferredHeight();

            if (rowsHeight < this.maxHeight) {
                var borderWidth = gridEl.getBorderTopWidth() + gridEl.getBorderBottomWidth();
                gridEl.setHeightPx(rowsHeight + borderWidth);
                this.grid.getOptions().setAutoHeight(true);
            } else if (gridEl.getHeight() < this.maxHeight) {
                gridEl.setHeightPx(this.maxHeight);
                this.grid.getOptions().setAutoHeight(false);
            }

            this.grid.resizeCanvas();
        }

        markSelections(selectedOptions: Option<OPTION_DISPLAY_VALUE>[], ignoreEmpty: boolean = false) {

            var stylesHash: Slick.CellCssStylesHash = {};
            var rows: number[] = [];
            selectedOptions.forEach((selectedOption: Option<OPTION_DISPLAY_VALUE>) => {
                var row = this.gridData.getRowById(selectedOption.value);
                rows.push(row);
                stylesHash[row] = {option: "selected"};
            });
            this.grid.setCellCssStyles("selected", stylesHash);
            if (!(rows.length === 0 && ignoreEmpty)) {
                this.grid.setSelectedRows(rows);
            }
        }

        hasActiveRow(): boolean {
            var activeCell = this.grid.getActiveCell();
            if (activeCell) {
                return true;
            }
            else {
                return false;
            }
        }

        getActiveRow(): number {
            var activeCell = this.grid.getActiveCell();
            if (activeCell) {
                return activeCell.row;
            }
            else {
                return -1;
            }
        }

        nagivateToFirstRow() {

            this.grid.setActiveCell(0, 0);
        }

        navigateToFirstRowIfNotActive() {

            if (!this.grid.getActiveCell()) {
                this.grid.setActiveCell(0, 0);
            }
        }

        navigateToNextRow() {
            this.grid.navigateDown();
        }

        navigateToPreviousRow() {
            this.grid.navigateUp();
        }

        resetActiveSelection() {

            if (this.grid.getActiveCell()) {
                this.grid.resetActiveCell();
            }
        }

        toggleRowSelection(row: number, isMaximumReached: boolean = false) {
            var rows = this.grid.getSelectedRows();
            var oldRows = rows.join();
            var index = rows.indexOf(row);

            if (index >= 0) {
                rows.splice(index, 1);
            } else if (!isMaximumReached) {
                rows.push(row);
            }

            // update on changes only
            if (oldRows !== rows.join()) {
                this.grid.setSelectedRows(rows);
            }
        }

        onRowSelection(listener: (event: DropdownGridRowSelectedEvent) => void) {
            this.rowSelectionListeners.push(listener);
        }

        unRowSelection(listener: (event: DropdownGridRowSelectedEvent) => void) {
            this.rowSelectionListeners.filter((currentListener: (event: DropdownGridRowSelectedEvent) => void) => {
                return listener != currentListener;
            });
        }

        onMultipleSelection(listener: (event: DropdownGridMultipleSelectionEvent) => void) {
            this.multipleSelectionListeners.push(listener);
        }

        unMultipleSelection(listener: (event: DropdownGridMultipleSelectionEvent) => void) {
            this.multipleSelectionListeners.filter((currentListener: (event: DropdownGridMultipleSelectionEvent) => void) => {
                return listener != currentListener;
            });
        }

        applyMultipleSelection() {
            var rows: number[] = this.grid.getSelectedRows();
            for (var key in rows) {
                this.notifyRowSelection(rows[key]);
            }
        }

        private notifyRowSelection(rowSelected: number) {
            var event = new DropdownGridRowSelectedEvent(rowSelected);
            this.rowSelectionListeners.forEach((listener: (event: DropdownGridRowSelectedEvent)=>void) => {
                listener(event);
            });
        }

        private notifyMultipleSelection(rowsSelected: number[]) {
            var event = new DropdownGridMultipleSelectionEvent(rowsSelected);
            this.multipleSelectionListeners.forEach((listener: (event: DropdownGridMultipleSelectionEvent)=>void) => {
                listener(event);
            });
        }
    }
}
