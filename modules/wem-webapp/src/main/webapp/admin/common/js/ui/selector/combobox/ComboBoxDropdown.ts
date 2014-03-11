module api.ui.selector.combobox {

    export interface ComboBoxDropdownConfig<OPTION_DISPLAY_VALUE> {

        comboBox: ComboBox<OPTION_DISPLAY_VALUE>;

        input: ComboBoxOptionFilterInput;

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

        private input: ComboBoxOptionFilterInput;

        private maxHeight: number = 200;

        private width: number;

        private emptyDropdown: api.dom.DivEl;

        private dropdown: api.ui.grid.Grid<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>;

        private dropdownData: api.ui.grid.DataView<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>;

        private dataIdProperty: string;

        private optionFormatter: (row: number, cell: number, value: OPTION_DISPLAY_VALUE, columnDef: any,
                                  dataContext: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) => string;

        private filter: (item: api.ui.selector.Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        private rowHeight: number;

        private rowSelectionListeners: {(event: ComboBoxDropdownRowSelectedEvent):void}[] = [];

        constructor(config: ComboBoxDropdownConfig<OPTION_DISPLAY_VALUE>) {

            this.comboBox = config.comboBox;
            this.input = config.input;
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

            this.dropdownData = new api.ui.grid.DataView<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>();
            this.dropdown = new api.ui.grid.Grid<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>(this.dropdownData, columns, options);
            this.dropdown.addClass("options-container");
            this.dropdown.getEl().setPosition("absolute");
            this.dropdown.hide();

            if (this.filter) {
                this.dropdownData.setFilter(this.filter);
            }

            // Listen to click in grid and issue selection
            this.dropdown.subscribeOnClick((e, args) => {

                this.notifyRowSelection(args.row);

                e.preventDefault();
                e.stopPropagation();
                return false;
            });

            this.dropdownData.onRowsChanged((e, args) => {
                // this.markSelections();
                // TODO: After refactoring during task CMS-3104, this does not seem to be necessary
                // TODO: Remove this when sure or re-implement
            });

            this.dropdownData.onRowCountChanged((e, args) => {
                // this.markSelections();
                // TODO: After refactoring during task CMS-3104, this does not seem to be necessary
                // TODO: Remove this when sure or re-implement
            });
        }

        getGrid() : api.ui.grid.Grid<api.ui.selector.Option<OPTION_DISPLAY_VALUE>> {
            return this.dropdown;
        }

        getDropdownElement(): api.dom.Element {
            return this.dropdown;
        }

        isDropdownShown(): boolean {
            return this.emptyDropdown.isVisible() || this.dropdown.isVisible();
        }

        setOptions(options: api.ui.selector.Option<OPTION_DISPLAY_VALUE>[]) {
            this.dropdownData.setItems(options, this.dataIdProperty);
            if (this.dropdown.isVisible() || this.emptyDropdown.isVisible()) {
                this.showDropdown([]);
            }
        }

        addOption(option: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) {
            this.dropdownData.addItem(option);
        }

        hasOptions(): boolean {
            return this.dropdownData.getLength() > 0;
        }

        getOptionCount(): number {
            return this.dropdownData.getLength();
        }

        getOptions(): api.ui.selector.Option<OPTION_DISPLAY_VALUE>[] {
            return this.dropdownData.getItems();
        }

        getOptionByValue(value: string): api.ui.selector.Option<OPTION_DISPLAY_VALUE> {
            return <api.ui.selector.Option<OPTION_DISPLAY_VALUE>>this.dropdownData.getItemById(value);
        }

        getOptionByRow(rowIndex: number): api.ui.selector.Option<OPTION_DISPLAY_VALUE> {
            return <api.ui.selector.Option<OPTION_DISPLAY_VALUE>>this.dropdownData.getItem(rowIndex);
        }

        showDropdown(selectedOptions: api.ui.selector.Option<OPTION_DISPLAY_VALUE>[]) {

            if (this.hasOptions()) {
                this.emptyDropdown.hide();
                this.dropdown.show();
                this.adjustDropdownSize();
                this.markSelections(selectedOptions);
            } else {
                this.dropdown.hide();
                this.adjustEmptyDropdownSize();
                this.emptyDropdown.getEl().setInnerHtml("No matching items");
                this.emptyDropdown.show();
            }
        }

        hideDropdown() {

            this.emptyDropdown.hide();
            this.dropdown.hide();
        }

        setLabel(label: string) {

            if (this.isDropdownShown()) {
                this.dropdown.hide();
                this.adjustEmptyDropdownSize();
                this.emptyDropdown.getEl().setInnerHtml(label);
                this.emptyDropdown.show();
            }
        }

        private adjustDropdownSize() {
            var dropdownEl = this.dropdown.getEl();
            var inputEl: api.dom.ElementHelper = this.input.getEl();

            dropdownEl.setTopPx(inputEl.getHeight() - inputEl.getBorderBottomWidth());

            if (dropdownEl.getWidth() != inputEl.getWidth()) {
                dropdownEl.setWidth(inputEl.getWidth() + "px");
            }

            var rowsHeight = this.getOptionCount() * this.rowHeight;
            if (rowsHeight < this.maxHeight) {
                var borderWidth = dropdownEl.getBorderTopWidth() + dropdownEl.getBorderBottomWidth();
                dropdownEl.setHeight(rowsHeight + borderWidth + "px");
                this.dropdown.setOptions({autoHeight: true});
            } else if (dropdownEl.getHeight() < this.maxHeight) {
                dropdownEl.setHeight(this.maxHeight + "px");
                this.dropdown.setOptions({autoHeight: false});
            }

            this.dropdown.resizeCanvas();
        }

        private adjustEmptyDropdownSize() {
            this.emptyDropdown.getEl().setTopPx(this.input.getEl().getHeight() - this.input.getEl().getBorderBottomWidth());
        }

        markSelections(selectedOptions: api.ui.selector.Option<OPTION_DISPLAY_VALUE>[]) {

            var stylesHash: Slick.CellCssStylesHash = {};
            selectedOptions.forEach((selectedOption: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) => {
                var row = this.dropdownData.getRowById(selectedOption.value);
                stylesHash[row] = {option: "selected"};
            });
            this.dropdown.setCellCssStyles("selected", stylesHash);
        }

        hasActiveRow(): boolean {
            var activeCell = this.dropdown.getActiveCell();
            if (activeCell) {
                return true;
            }
            else {
                return false;

            }
        }

        getActiveRow(): number {
            var activeCell = this.dropdown.getActiveCell();
            if (activeCell) {
                return activeCell.row;
            }
            else {
                return -1;
            }
        }

        makeFirstRowActive() {

            this.dropdown.setActiveCell(0, 0);
        }

        makeFirstRowActiveIfNoRowIsActive() {

            if (!this.dropdown.getActiveCell()) {
                this.dropdown.setActiveCell(0, 0);
            }
        }

        navigateToNextRow() {
            this.dropdown.navigateDown();
        }

        navigateToPreviousRow() {
            this.dropdown.navigateUp();
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
