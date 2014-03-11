module api.ui.selector.combobox {

    export interface ComboBoxDropdownConfig<OPTION_DISPLAY_VALUE> {

        comboBox: ComboBox<OPTION_DISPLAY_VALUE>;

        maxHeight: number;

        width: number;

        optionFormatter: (row: number, cell: number, value: OPTION_DISPLAY_VALUE, columnDef: any,
                          dataContext: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) => string;

        filter: (item: api.ui.selector.Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        rowHeight: number;

        dataIdProperty?:string;
    }

    export class ComboBoxDropdown<OPTION_DISPLAY_VALUE> {

        private comboBox: ComboBox<OPTION_DISPLAY_VALUE>;

        private maxHeight: number = 200;

        private width: number;

        private emptyDropdown: api.dom.DivEl;

        private grid: api.ui.grid.Grid<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>;

        private gridData: api.ui.grid.DataView<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>;

        private dataIdProperty: string;

        private optionFormatter: (row: number, cell: number, value: OPTION_DISPLAY_VALUE, columnDef: any,
                                  dataContext: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) => string;

        private filter: (item: api.ui.selector.Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        private rowHeight: number;

        private rowSelectionListeners: {(event: ComboBoxDropdownRowSelectedEvent):void}[] = [];

        constructor(config: ComboBoxDropdownConfig<OPTION_DISPLAY_VALUE>) {

            this.comboBox = config.comboBox;
            this.optionFormatter = config.optionFormatter;
            this.filter = config.filter;
            this.rowHeight = config.rowHeight;
            this.dataIdProperty = config.dataIdProperty;
            this.maxHeight = config.maxHeight;
            this.width = config.width;

            this.emptyDropdown = new api.dom.DivEl("empty-options");
            this.emptyDropdown.getEl().setInnerHtml("No matching items");
            this.emptyDropdown.hide();
            this.comboBox.appendChild(this.emptyDropdown);

            var columns: api.ui.grid.GridColumn<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>[] = [
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
                dataIdProperty: config.dataIdProperty ? config.dataIdProperty : "value"
            };

            this.gridData = new api.ui.grid.DataView<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>();
            this.grid = new api.ui.grid.Grid<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>(this.gridData, columns, options);
            this.grid.addClass("options-container");
            this.grid.getEl().setPosition("absolute");
            this.grid.hide();

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

        getGrid() : api.ui.grid.Grid<api.ui.selector.Option<OPTION_DISPLAY_VALUE>> {
            return this.grid;
        }

        isDropdownShown(): boolean {
            return this.emptyDropdown.isVisible() || this.grid.isVisible();
        }

        setOptions(options: api.ui.selector.Option<OPTION_DISPLAY_VALUE>[]) {
            this.gridData.setItems(options, this.dataIdProperty);
            if (this.grid.isVisible() || this.emptyDropdown.isVisible()) {
                this.showDropdown([]);
            }
        }

        addOption(option: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) {
            this.gridData.addItem(option);
        }

        hasOptions(): boolean {
            return this.gridData.getLength() > 0;
        }

        getOptionCount(): number {
            return this.gridData.getLength();
        }

        getOptions(): api.ui.selector.Option<OPTION_DISPLAY_VALUE>[] {
            return this.gridData.getItems();
        }

        getOptionByValue(value: string): api.ui.selector.Option<OPTION_DISPLAY_VALUE> {
            return <api.ui.selector.Option<OPTION_DISPLAY_VALUE>>this.gridData.getItemById(value);
        }

        getOptionByRow(rowIndex: number): api.ui.selector.Option<OPTION_DISPLAY_VALUE> {
            return <api.ui.selector.Option<OPTION_DISPLAY_VALUE>>this.gridData.getItem(rowIndex);
        }

        showDropdown(selectedOptions: api.ui.selector.Option<OPTION_DISPLAY_VALUE>[]) {

            if (this.hasOptions()) {
                this.emptyDropdown.hide();
                this.grid.show();
                this.adjustDropdownSize();
                this.markSelections(selectedOptions);
            } else {
                this.grid.hide();
                this.emptyDropdown.getEl().setInnerHtml("No matching items");
                this.emptyDropdown.show();
            }
        }

        hideDropdown() {

            this.emptyDropdown.hide();
            this.grid.hide();
        }

        setLabel(label: string) {

            if (this.isDropdownShown()) {
                this.grid.hide();
                this.emptyDropdown.getEl().setInnerHtml(label);
                this.emptyDropdown.show();
            }
        }

        setTopPx(value:number) {
            this.grid.getEl().setTopPx(value);
            this.emptyDropdown.getEl().setTopPx(value);
        }

        setWidth(value:number) {
            this.grid.getEl().setWidthPx(value);
        }

        private adjustDropdownSize() {
            var dropdownEl = this.grid.getEl();

            var rowsHeight = this.getOptionCount() * this.rowHeight;
            if (rowsHeight < this.maxHeight) {
                var borderWidth = dropdownEl.getBorderTopWidth() + dropdownEl.getBorderBottomWidth();
                dropdownEl.setHeight(rowsHeight + borderWidth + "px");
                this.grid.setOptions({autoHeight: true});
            } else if (dropdownEl.getHeight() < this.maxHeight) {
                dropdownEl.setHeight(this.maxHeight + "px");
                this.grid.setOptions({autoHeight: false});
            }

            this.grid.resizeCanvas();
        }

        markSelections(selectedOptions: api.ui.selector.Option<OPTION_DISPLAY_VALUE>[]) {

            var stylesHash: Slick.CellCssStylesHash = {};
            selectedOptions.forEach((selectedOption: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) => {
                var row = this.gridData.getRowById(selectedOption.value);
                stylesHash[row] = {option: "selected"};
            });
            this.grid.setCellCssStyles("selected", stylesHash);
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

        makeFirstRowActive() {

            this.grid.setActiveCell(0, 0);
        }

        makeFirstRowActiveIfNoRowIsActive() {

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


        onRowSelection(listener: (event: ComboBoxDropdownRowSelectedEvent) => void) {
            this.rowSelectionListeners.push(listener);
        }

        unRowSelection(listener: (event: ComboBoxDropdownRowSelectedEvent) => void) {
            this.rowSelectionListeners.filter((currentListener: (event: ComboBoxDropdownRowSelectedEvent) => void) => {
                return listener != currentListener;
            })
        }

        private notifyRowSelection(rowSelected: number) {
            var event = new ComboBoxDropdownRowSelectedEvent(rowSelected);
            this.rowSelectionListeners.forEach((listener: (event: ComboBoxDropdownRowSelectedEvent)=>void) => {
                listener(event);
            });
        }
    }
}
