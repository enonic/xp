module api.ui.selector.dropdown {

    import Option = api.ui.selector.Option;
    import DropdownGridConfig = api.ui.selector.DropdownGridConfig;
    import DropdownGrid = api.ui.selector.DropdownGrid;
    import DropdownGridRowSelectedEvent = api.ui.selector.DropdownGridRowSelectedEvent;

    export interface DropdownDropdownConfig<OPTION_DISPLAY_VALUE> {

        maxHeight?: number;

        width: number;

        optionDisplayValueViewer?: Viewer<OPTION_DISPLAY_VALUE>;

        filter: (item: Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        dataIdProperty?:string;
    }

    export class DropdownDropdown<OPTION_DISPLAY_VALUE> {

        private dropdownGrid: DropdownGrid<OPTION_DISPLAY_VALUE>;

        constructor(config: DropdownDropdownConfig<OPTION_DISPLAY_VALUE>) {

            this.dropdownGrid = new DropdownGrid<OPTION_DISPLAY_VALUE>(<DropdownGridConfig<OPTION_DISPLAY_VALUE>>{
                maxHeight: config.maxHeight,
                width: config.width,
                optionDisplayValueViewer: config.optionDisplayValueViewer,
                filter: config.filter,
                dataIdProperty: config.dataIdProperty
            });
        }

        getGrid(): DropdownGrid<OPTION_DISPLAY_VALUE> {
            return this.dropdownGrid;
        }

        renderDropdownGrid() {
            this.dropdownGrid.renderGrid();
        }


        isDropdownShown(): boolean {
            return this.dropdownGrid.isVisible();
        }

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[]) {

            this.dropdownGrid.setOptions(options);

            if (this.dropdownGrid.isVisible()) {
                this.showDropdown(null);
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

        getOptionByValue(value: string): Option<OPTION_DISPLAY_VALUE> {
            return this.dropdownGrid.getOptionByValue(value);
        }

        getOptionByRow(rowIndex: number): Option<OPTION_DISPLAY_VALUE> {
            return this.dropdownGrid.getOptionByRow(rowIndex);
        }

        setFilterArgs(args: any) {
            this.dropdownGrid.setFilterArgs(args);
        }

        showDropdown(selectedOption: Option<OPTION_DISPLAY_VALUE>) {

            if (this.hasOptions()) {
                this.dropdownGrid.show();
                this.dropdownGrid.adjustGridHeight();
                if (selectedOption) {
                    this.dropdownGrid.markSelections([selectedOption]);
                }
            } else {
                this.dropdownGrid.hide();
            }
        }

        hideDropdown() {

            this.dropdownGrid.hide();
        }

        setTopPx(value: number) {
            this.dropdownGrid.setTopPx(value);
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

        markSelections(selectedOptions: Option<OPTION_DISPLAY_VALUE>[]) {
            this.dropdownGrid.markSelections(selectedOptions);
        }

        onRowSelection(listener: (event: DropdownGridRowSelectedEvent) => void) {
            this.dropdownGrid.onRowSelection(listener);
        }

        unRowSelection(listener: (event: DropdownGridRowSelectedEvent) => void) {
            this.dropdownGrid.unRowSelection(listener);
        }
    }
}
