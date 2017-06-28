module api.ui.selector {

    export class DropdownList<OPTION_DISPLAY_VALUE> {

        private emptyDropdown: api.dom.DivEl;

        private dropdownGrid: DropdownGrid<OPTION_DISPLAY_VALUE>;

        constructor(config: DropdownGridConfig<OPTION_DISPLAY_VALUE>) {

            this.emptyDropdown = new api.dom.DivEl('empty-options');
            this.emptyDropdown.getEl().setInnerHtml('No matching items');
            this.emptyDropdown.hide();

            this.initDropdownGrid(config);
        }

        private initDropdownGrid(config: DropdownGridConfig<OPTION_DISPLAY_VALUE>) {
            if (config.isDropdownGrid) {
                this.dropdownGrid = new DropdownTreeGrid<OPTION_DISPLAY_VALUE>(config);
            } else {
                this.dropdownGrid = new DropdownListGrid<OPTION_DISPLAY_VALUE>(config);
            }
        }

        getDropdownGrid(): DropdownGrid<OPTION_DISPLAY_VALUE> {
            return this.dropdownGrid;
        }

        renderDropdownGrid() {
            this.dropdownGrid.renderGrid();
        }

        getEmptyDropdown(): api.dom.DivEl {
            return this.emptyDropdown;
        }

        isDropdownShown(): boolean {
            return this.emptyDropdown.isVisible() || this.dropdownGrid.isVisible();
        }

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[], noOptionsText: string) {

            this.dropdownGrid.setOptions(options);

            if (this.isDropdownShown()) {
                this.showDropdown(null, noOptionsText);
            }
        }

        removeAllOptions() {
            this.dropdownGrid.removeAllOptions();
        }

        addOption(option: Option<OPTION_DISPLAY_VALUE>) {
            this.dropdownGrid.addOption(option);
        }

        hasOptions(): boolean {
            return this.dropdownGrid.hasOptions();
        }

        getOptionCount(): number {
            return this.dropdownGrid.getOptionCount();
        }

        getOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            return this.dropdownGrid.getOptions();
        }

        getSelectedOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            return this.dropdownGrid.getSelectedOptions();
        }

        getOptionByValue(value: string): Option<OPTION_DISPLAY_VALUE> {
            return this.dropdownGrid.getOptionByValue(value);
        }

        getOptionByRow(rowIndex: number): Option<OPTION_DISPLAY_VALUE> {
            return this.dropdownGrid.getOptionByRow(rowIndex);
        }

        setFilterArgs(args: any) {
            this.dropdownGrid.setFilterArgs(args);
        }

        resizeDropdownTo(height: number) {
            this.dropdownGrid.setCustomHeight(height);
            this.dropdownGrid.adjustGridHeight();
        }

        resetDropdownSize() {
            this.dropdownGrid.resetCustomHeight();
        }

        resetActiveSelection() {
            this.dropdownGrid.resetActiveSelection();
        }

        showDropdown(selectedOptions?: Option<OPTION_DISPLAY_VALUE>[], noOptionsText?: string) {

            if (this.hasOptions()) {
                this.emptyDropdown.hide();
                this.dropdownGrid.show();
                this.dropdownGrid.adjustGridHeight();
                if (selectedOptions) {
                    this.dropdownGrid.markSelections(selectedOptions);
                    if (selectedOptions.length > 0) {
                        this.dropdownGrid.markReadOnly(selectedOptions);
                        this.navigateToRowIfNotActive(selectedOptions[0]);
                    }
                }
            } else {
                this.dropdownGrid.hide();
                this.emptyDropdown.getEl().setInnerHtml(!!noOptionsText ? noOptionsText : 'No matching items');
                this.emptyDropdown.show();
            }
        }

        hideDropdown() {
            this.emptyDropdown.hide();
            this.dropdownGrid.hide();
        }

        setEmptyDropdownText(label: string) {

            if (this.isDropdownShown()) {
                this.dropdownGrid.hide();
                this.emptyDropdown.getEl().setInnerHtml(label);
                this.emptyDropdown.show();
            }
        }

        setTopPx(value: number) {
            this.dropdownGrid.setTopPx(value);
            this.emptyDropdown.getEl().setTopPx(value);
        }

        setWidth(value: number) {
            this.dropdownGrid.setWidthPx(value);
        }

        hasActiveRow(): boolean {
            return this.dropdownGrid.hasActiveRow();
        }

        getActiveRow(): number {
            return this.dropdownGrid.getActiveRow();
        }

        nagivateToFirstRow() {
            this.dropdownGrid.navigateToFirstRow();
        }

        navigateToRowIfNotActive(selectedOption?: Option<OPTION_DISPLAY_VALUE>) {
            if (this.getActiveRow() > -1) {
                return;
            }
            if (selectedOption) {
                let row = this.dropdownGrid.getRowByValue(selectedOption.value);
                if(row != undefined) {
                    this.dropdownGrid.navigateToRow(row);
                }
            } else {
                this.dropdownGrid.navigateToFirstRow();
            }
        }

        navigateToNextRow() {
            this.dropdownGrid.navigateToNextRow();
        }

        navigateToPreviousRow() {
            this.dropdownGrid.navigateToPreviousRow();
        }

        markSelections(selectedOptions: Option<OPTION_DISPLAY_VALUE>[], ignoreEmpty: boolean = false) {
            this.dropdownGrid.markSelections(selectedOptions, ignoreEmpty);
        }

        onRowCountChanged(listener: () => void) {
            this.dropdownGrid.onRowCountChanged(listener);
        }

        onRowSelection(listener: (event: DropdownGridRowSelectedEvent) => void) {
            this.dropdownGrid.onRowSelection(listener);
        }

        unRowSelection(listener: (event: DropdownGridRowSelectedEvent) => void) {
            this.dropdownGrid.unRowSelection(listener);
        }
    }
}
