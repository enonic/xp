module api.ui.selector {

    export interface DropdownGridConfig<OPTION_DISPLAY_VALUE> {

        maxHeight?: number;

        width: number;

        optionFormatter: (row: number, cell: number, value: OPTION_DISPLAY_VALUE, columnDef: any,
                          dataContext: Option<OPTION_DISPLAY_VALUE>) => string;

        filter: (item: Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        rowHeight?: number;

        dataIdProperty?:string;

        multipleSelections: boolean;
    }

    export class DropdownGrid<OPTION_DISPLAY_VALUE> {

        private maxHeight: number;

        private width: number;

        private grid: api.ui.grid.Grid<Option<OPTION_DISPLAY_VALUE>>;

        private gridData: api.ui.grid.DataView<Option<OPTION_DISPLAY_VALUE>>;

        private dataIdProperty: string;

        private optionFormatter: (row: number, cell: number, value: OPTION_DISPLAY_VALUE, columnDef: any,
                                  dataContext: Option<OPTION_DISPLAY_VALUE>) => string;

        private filter: (item: Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        private rowHeight: number;

        private rowSelectionListeners: {(event: DropdownGridRowSelectedEvent):void}[];

        private multipleSelections: boolean;

        constructor(config: DropdownGridConfig<OPTION_DISPLAY_VALUE>) {
            this.rowSelectionListeners = [];
            this.maxHeight = config.maxHeight || 200;
            this.optionFormatter = config.optionFormatter;
            this.filter = config.filter;
            this.rowHeight = config.rowHeight || 24;
            this.dataIdProperty = config.dataIdProperty;
            this.maxHeight = config.maxHeight;
            this.width = config.width;
            this.multipleSelections = config.multipleSelections || false;

            var columns: api.ui.grid.GridColumn<Option<OPTION_DISPLAY_VALUE>>[] = [
                {
                    id: "option",
                    name: "Options",
                    field: "displayValue",
                    formatter: this.optionFormatter}
            ];
            var options: api.ui.grid.GridOptions = {
                width: this.width,
                height: this.maxHeight,
                hideColumnHeaders: true,
                enableColumnReorder: false,
                fullWidthRows: true,
                forceFitColumns: true,
                rowHeight: this.rowHeight,
                checkableRows: this.multipleSelections,
                multiSelect: this.multipleSelections,
                dataIdProperty: config.dataIdProperty ? config.dataIdProperty : "value"
            };

            this.gridData = new api.ui.grid.DataView<Option<OPTION_DISPLAY_VALUE>>();
            this.grid = new api.ui.grid.Grid<Option<OPTION_DISPLAY_VALUE>>(this.gridData, columns, options);

            this.grid.addClass("options-container");
            this.grid.getEl().setPosition("absolute");
            this.grid.hide();
            this.grid.setSelectionModel(new Slick.RowSelectionModel({selectActiveRow: false}));

            if (this.filter) {
                this.gridData.setFilter(this.filter);
            }

            // Listen to click in grid and issue selection
            this.grid.subscribeOnClick((e, args) => {

                this.notifyRowSelection(args.row);

                e.preventDefault();
                e.stopPropagation();
                return false;
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

        isVisible() : boolean {
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

        addOption(option: Option<OPTION_DISPLAY_VALUE>) {
            this.gridData.addItem(option);
        }

        hasOptions(): boolean {
            return this.gridData.getLength() > 0;
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

        setTopPx(value: number) {
            this.grid.getEl().setTopPx(value);
        }

        setWidthPx(value: number) {
            this.grid.getEl().setWidthPx(value);
        }

        adjustGridHeight() {

            var gridEl = this.grid.getEl();
            var rowsHeight = this.getOptionCount() * this.rowHeight;

            if (rowsHeight < this.maxHeight) {
                var borderWidth = gridEl.getBorderTopWidth() + gridEl.getBorderBottomWidth();
                gridEl.setHeightPx(rowsHeight + borderWidth);
                this.grid.setOptions({autoHeight: true});
            }
            else if (gridEl.getHeight() < this.maxHeight) {
                gridEl.setHeightPx(this.maxHeight);
                this.grid.setOptions({autoHeight: false});
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

        onRowSelection(listener: (event: DropdownGridRowSelectedEvent) => void) {
            this.rowSelectionListeners.push(listener);
        }

        unRowSelection(listener: (event: DropdownGridRowSelectedEvent) => void) {
            this.rowSelectionListeners.filter((currentListener: (event: DropdownGridRowSelectedEvent) => void) => {
                return listener != currentListener;
            });
        }

        applyMultiselection() {
            var rows:number[] = this.grid.getSelectedRows();
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
    }
}
