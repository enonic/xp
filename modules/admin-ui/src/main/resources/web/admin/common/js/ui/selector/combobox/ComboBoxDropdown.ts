module api.ui.selector.combobox {

    import Option = api.ui.selector.Option;
    import DropdownGridConfig = api.ui.selector.DropdownGridConfig;
    import DropdownGrid = api.ui.selector.DropdownGrid;
    import DropdownGridRowSelectedEvent = api.ui.selector.DropdownGridRowSelectedEvent;

    export interface ComboBoxDropdownConfig<OPTION_DISPLAY_VALUE> {

        maxHeight?: number;

        width: number;

        optionDisplayValueViewer?: Viewer<OPTION_DISPLAY_VALUE>;

        filter: (item: Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        dataIdProperty?:string;

        multipleSelections: boolean;
    }

    export class ComboBoxDropdown<OPTION_DISPLAY_VALUE> {

        private emptyDropdown: api.dom.DivEl;

        private dropdownGridConfig: DropdownGridConfig<OPTION_DISPLAY_VALUE>;
        
        private dropdownGrid: DropdownGrid<OPTION_DISPLAY_VALUE>;

        constructor(config: ComboBoxDropdownConfig<OPTION_DISPLAY_VALUE>) {

            this.emptyDropdown = new api.dom.DivEl("empty-options");
            this.emptyDropdown.getEl().setInnerHtml("No matching items");
            this.emptyDropdown.hide();

            this.dropdownGridConfig = {
                maxHeight: config.maxHeight,
                width: Math.max(config.width, 240),
                optionDisplayValueViewer: config.optionDisplayValueViewer,
                filter: config.filter,
                dataIdProperty: config.dataIdProperty,
                multipleSelections: config.multipleSelections
            };

            this.dropdownGrid = new DropdownGrid<OPTION_DISPLAY_VALUE>(this.dropdownGridConfig);
        }

        getGrid(): DropdownGrid<OPTION_DISPLAY_VALUE> {
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

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[], selectedOptions: Option<OPTION_DISPLAY_VALUE>[] = []) {

            this.dropdownGrid.setOptions(options);

            if (this.isDropdownShown()) {
                this.showDropdown(selectedOptions);
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

        getSelectedOptionCount(): number {
            return this.dropdownGrid.getSelectedOptionCount();
        }

        getOptionCount(): number {
            return this.dropdownGrid.getOptionCount();
        }

        getOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            return this.dropdownGrid.getOptions();
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

        showDropdown(selectedOptions: Option<OPTION_DISPLAY_VALUE>[]) {

            if (this.hasOptions()) {
                this.emptyDropdown.hide();
                this.dropdownGrid.show();
                this.dropdownGrid.adjustGridHeight();
                this.dropdownGrid.markSelections(selectedOptions);
            } else {
                this.dropdownGrid.hide();
                this.emptyDropdown.getEl().setInnerHtml("No matching items");
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
            this.dropdownGrid.nagivateToFirstRow();
        }

        navigateToFirstRowIfNotActive() {
            this.dropdownGrid.navigateToFirstRowIfNotActive();
        }

        navigateToNextRow() {
            this.dropdownGrid.navigateToNextRow();
        }

        navigateToPreviousRow() {
            this.dropdownGrid.navigateToPreviousRow();
        }

        toggleRowSelection(row: number, isMaximumReached: boolean = false) {
            this.dropdownGrid.toggleRowSelection(row, isMaximumReached);
        }

        resetActiveSelection() {
            this.dropdownGrid.resetActiveSelection();
        }

        applyMultipleSelection() {
            this.dropdownGrid.applyMultipleSelection();
        }

        markSelections(selectedOptions: Option<OPTION_DISPLAY_VALUE>[], ignoreEmpty: boolean = false) {
            this.dropdownGrid.markSelections(selectedOptions, ignoreEmpty);
        }

        onRowSelection(listener: (event: DropdownGridRowSelectedEvent) => void) {
            this.dropdownGrid.onRowSelection(listener);
        }

        unRowSelection(listener: (event: DropdownGridRowSelectedEvent) => void) {
            this.dropdownGrid.unRowSelection(listener);
        }

        onMultipleSelection(listener: (event: DropdownGridMultipleSelectionEvent) => void) {
            this.dropdownGrid.onMultipleSelection(listener);
        }

        unMultipleSelection(listener: (event: DropdownGridMultipleSelectionEvent) => void) {
            this.dropdownGrid.unMultipleSelection(listener);
        }
    }
}
