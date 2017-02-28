module api.ui.selector.combobox {

    import Option = api.ui.selector.Option;
    import DropdownGridConfig = api.ui.selector.DropdownGridConfig;

    export class ComboBoxDropdown<OPTION_DISPLAY_VALUE> extends DropdownList<OPTION_DISPLAY_VALUE> {

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

        onMultipleSelection(listener: (event: DropdownGridMultipleSelectionEvent) => void) {
            this.getDropdownGrid().onMultipleSelection(listener);
        }

        unMultipleSelection(listener: (event: DropdownGridMultipleSelectionEvent) => void) {
            this.getDropdownGrid().unMultipleSelection(listener);
        }
    }
}
