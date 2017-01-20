module api.ui.selector.combobox {

    import Option = api.ui.selector.Option;
    import DropdownGridConfig = api.ui.selector.DropdownGridConfig;
    import DropdownGrid = api.ui.selector.DropdownGrid;
    import DropdownGridRowSelectedEvent = api.ui.selector.DropdownGridRowSelectedEvent;

    export interface ComboBoxDropdownConfig<OPTION_DISPLAY_VALUE> extends DropdownListConfig<OPTION_DISPLAY_VALUE> {
        multipleSelections: boolean;
    }

    export class ComboBoxDropdown<OPTION_DISPLAY_VALUE> extends DropdownList<OPTION_DISPLAY_VALUE> {

        assembleGridConfig(config: ComboBoxDropdownConfig<OPTION_DISPLAY_VALUE>): DropdownGridConfig<OPTION_DISPLAY_VALUE> {
            return <DropdownGridConfig<OPTION_DISPLAY_VALUE>> {
                maxHeight: config.maxHeight,
                width: config.width,
                optionDisplayValueViewer: config.optionDisplayValueViewer,
                filter: config.filter,
                dataIdProperty: config.dataIdProperty,
                multipleSelections: config.multipleSelections
            };
        }

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[], noOptionsText: string, selectedOptions: Option<OPTION_DISPLAY_VALUE>[] = [],
                   saveSelection?: boolean) {

            selectedOptions.forEach((selectedOption: Option<OPTION_DISPLAY_VALUE>) => {
                if (selectedOption.readOnly) {
                    for (let i = 0; i < options.length; i++) {
                        if (selectedOption.value === options[i].value) {
                            options[i].readOnly = true;
                            break;
                        }
                    }
                }
            });

            // `from` is used to determine, from which point should selection be updated
            let from = this.getDropdownGrid().getOptionCount();

            this.getDropdownGrid().setOptions(options);

            if (this.isDropdownShown()) {
                let selected = selectedOptions;

                // Save the current grid selection and restore the selection for the new items,
                // according to the selected options
                if (saveSelection) {
                    let gridSelection = this.getDropdownGrid().getSelectedOptions();
                    let newSelection = selectedOptions.filter((option) => {
                        return this.getDropdownGrid().getRowByValue(option.value) >= from;
                    });

                    selected = gridSelection.concat(newSelection);
                }

                this.showDropdown(selected, noOptionsText);
            }
        }

        getSelectedOptionCount(): number {
            return this.getDropdownGrid().getSelectedOptionCount();
        }

        toggleRowSelection(row: number, isMaximumReached: boolean = false) {
            this.getDropdownGrid().toggleRowSelection(row, isMaximumReached);
        }

        resetActiveSelection() {
            this.getDropdownGrid().resetActiveSelection();
        }

        applyMultipleSelection() {
            this.getDropdownGrid().applyMultipleSelection();
        }

        onMultipleSelection(listener: (event: DropdownGridMultipleSelectionEvent) => void) {
            this.getDropdownGrid().onMultipleSelection(listener);
        }

        unMultipleSelection(listener: (event: DropdownGridMultipleSelectionEvent) => void) {
            this.getDropdownGrid().unMultipleSelection(listener);
        }
    }
}
