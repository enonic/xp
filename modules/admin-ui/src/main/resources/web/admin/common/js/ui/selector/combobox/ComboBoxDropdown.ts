module api.ui.selector.combobox {

    import Option = api.ui.selector.Option;
    import DropdownGridConfig = api.ui.selector.DropdownGridConfig;
    import DropdownGrid = api.ui.selector.DropdownGrid;
    import DropdownGridRowSelectedEvent = api.ui.selector.DropdownGridRowSelectedEvent;

    export interface ComboBoxDropdownConfig<OPTION_DISPLAY_VALUE> extends DropdownListConfig<OPTION_DISPLAY_VALUE> {
        multipleSelections: boolean;
    }

    export class ComboBoxDropdown<OPTION_DISPLAY_VALUE> extends DropdownList<OPTION_DISPLAY_VALUE> {

        constructor(config: ComboBoxDropdownConfig<OPTION_DISPLAY_VALUE>) {
            super(config);
        }

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

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[], selectedOptions: Option<OPTION_DISPLAY_VALUE>[] = []) {

            selectedOptions.forEach((selectedOption: Option<OPTION_DISPLAY_VALUE>) => {
                if(selectedOption.readOnly)
                {
                    for(var i = 0; i < options.length; i++ )
                    {
                        if(selectedOption.value == options[i].value)
                        {
                            options[i].readOnly = true;
                            break;
                        }
                    }
                }
            });

            this.getDropdownGrid().setOptions(options);
            if (this.isDropdownShown()) {
                this.showDropdown(selectedOptions);
            }
        }

        showDropdown(selectedOptions: Option<OPTION_DISPLAY_VALUE>[]) {

            if (this.hasOptions()) {
                this.getEmptyDropdown().hide();
                this.getDropdownGrid().show();
                this.getDropdownGrid().adjustGridHeight();
                this.getDropdownGrid().markSelections(selectedOptions);
                this.getDropdownGrid().markReadOnly(selectedOptions);
            } else {
                this.getDropdownGrid().hide();
                this.getEmptyDropdown().getEl().setInnerHtml("No matching items");
                this.getEmptyDropdown().show();
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
