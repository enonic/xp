module api.ui.selector.combobox {

    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import OptionFilterInputValueChangedEvent = api.ui.selector.OptionFilterInputValueChangedEvent;
    import DropdownHandle = api.ui.selector.DropdownHandle;
    import Viewer = api.ui.Viewer;
    import DelayedFunctionCall = api.util.DelayedFunctionCall;

    export interface ComboBoxConfig<T> {

        iconUrl?: string;

        optionDisplayValueViewer?: Viewer<T>;

        selectedOptionsView: SelectedOptionsView<T>;

        maximumOccurrences?: number;

        filter?: (item: any, args: any) => boolean;

        hideComboBoxWhenMaxReached?:boolean;

        setNextInputFocusWhenMaxReached?: boolean;

        dataIdProperty?:string;

        delayedInputValueChangedHandling?: number;

    }

    export class ComboBox<OPTION_DISPLAY_VALUE> extends api.dom.FormInputEl {

        private icon: api.dom.ImgEl;

        private dropdownHandle: DropdownHandle;

        private applySelectionsButton: api.ui.Button;

        private input: ComboBoxOptionFilterInput;

        private delayedInputValueChangedHandling;

        private delayedHandleInputValueChangedFnCall: DelayedFunctionCall;

        private preservedInputValueChangedEvent: api.ui.ValueChangedEvent;

        private multipleSelections: boolean = false;

        private selectedOptionsCtrl: SelectedOptionsCtrl<OPTION_DISPLAY_VALUE>;

        private comboBoxDropdown: ComboBoxDropdown<OPTION_DISPLAY_VALUE>;

        private hideComboBoxWhenMaxReached: boolean;

        private setNextInputFocusWhenMaxReached: boolean = true;

        private optionSelectedListeners: {(event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>):void}[] = [];

        private optionFilterInputValueChangedListeners: {(event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>):void}[] = [];

        /**
         * Indicates if combobox is currently has focus
         * @type {boolean}
         */
        private active: boolean = false;

        constructor(name: string, config: ComboBoxConfig<OPTION_DISPLAY_VALUE>) {
            super("div", "combobox");
            this.getEl().setAttribute("name", name);

            this.hideComboBoxWhenMaxReached = config.hideComboBoxWhenMaxReached;
            if (config.setNextInputFocusWhenMaxReached !== undefined) {
                this.setNextInputFocusWhenMaxReached = config.setNextInputFocusWhenMaxReached;
            }
            if (config.selectedOptionsView != null) {
                this.selectedOptionsCtrl = new SelectedOptionsCtrl(config.selectedOptionsView,
                        config.maximumOccurrences != null ? config.maximumOccurrences : 0);
                this.multipleSelections = true;
            }
            if (config.iconUrl) {
                this.icon = new api.dom.ImgEl(config.iconUrl, "input-icon");
                this.appendChild(this.icon);
            }

            this.input = new ComboBoxOptionFilterInput();
            this.appendChild(this.input);

            this.delayedInputValueChangedHandling = config.delayedInputValueChangedHandling || 0;
            this.delayedHandleInputValueChangedFnCall = new DelayedFunctionCall(this.handleInputValueChanged, this,
                this.delayedInputValueChangedHandling);

            this.dropdownHandle = new DropdownHandle();
            this.appendChild(this.dropdownHandle);

            if (this.multipleSelections && (config.maximumOccurrences != 1)) {
                this.applySelectionsButton = new Button("Apply");
                this.applySelectionsButton.addClass('small apply-button');
                this.applySelectionsButton.hide();
                this.appendChild(this.applySelectionsButton);
            }

            this.comboBoxDropdown = new ComboBoxDropdown(<ComboBoxDropdownConfig<OPTION_DISPLAY_VALUE>>{
                maxHeight: 200,
                width: this.input.getEl().getWidth(),
                optionDisplayValueViewer: config.optionDisplayValueViewer,
                filter: config.filter,
                dataIdProperty: config.dataIdProperty,
                multipleSelections: (this.multipleSelections && (config.maximumOccurrences != 1))
            });

            this.appendChild(this.comboBoxDropdown.getEmptyDropdown());
            this.appendChild(this.comboBoxDropdown.getGrid().getElement());

            this.setupListeners();

            this.onRendered((event: api.dom.ElementRenderedEvent) => {

                this.doUpdateDropdownTopPositionAndWidth();
            });
        }

        private doUpdateDropdownTopPositionAndWidth() {
            var inputEl = this.input.getEl();
            this.comboBoxDropdown.setTopPx(inputEl.getHeightWithBorder() - inputEl.getBorderBottomWidth());
            this.comboBoxDropdown.setWidth(inputEl.getWidthWithBorder());
        }

        giveFocus(): boolean {
            return this.input.giveFocus();
        }

        isDropdownShown(): boolean {
            return this.comboBoxDropdown.isDropdownShown();
        }

        showDropdown() {

            this.doUpdateDropdownTopPositionAndWidth();
            this.comboBoxDropdown.showDropdown(this.selectedOptionsCtrl.getOptions());
            this.dropdownHandle.down();

            this.comboBoxDropdown.renderDropdownGrid();

            this.input.getEl().setAttribute('readonly', 'readonly');
        }

        setEmptyDropdownText(label: string) {
            this.comboBoxDropdown.setEmptyDropdownText(label);
        }

        hideDropdown() {
            this.dropdownHandle.up();
            this.comboBoxDropdown.hideDropdown();
            if (this.applySelectionsButton) {
                this.applySelectionsButton.hide();
            }

            this.input.getEl().removeAttribute('readonly');
        }

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[]) {
            this.comboBoxDropdown.setOptions(options, this.getSelectedOptions());
        }

        addOption(option: Option<OPTION_DISPLAY_VALUE>) {
            this.comboBoxDropdown.addOption(option);
        }

        hasOptions(): boolean {
            return this.comboBoxDropdown.hasOptions();
        }

        getOptionCount(): number {
            return this.comboBoxDropdown.getOptionCount();
        }

        getOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            return this.comboBoxDropdown.getOptions();
        }

        getOptionByValue(value: string): Option<OPTION_DISPLAY_VALUE> {
            return this.comboBoxDropdown.getOptionByValue(value);
        }

        getOptionByRow(rowIndex: number): Option<OPTION_DISPLAY_VALUE> {
            return this.comboBoxDropdown.getOptionByRow(rowIndex);
        }

        setFilterArgs(args: any) {
            this.comboBoxDropdown.setFilterArgs(args);
        }

        setValue(value: string) {
            var option = this.getOptionByValue(value);
            if (option != null) {
                this.selectOption(option);
            }
        }

        setValues(values: string[]) {
            values.forEach((value: string) => {
                var option = this.getOptionByValue(value);
                if (option != null) {
                    this.selectOption(option);
                }
            });
        }

        selectRow(index: number) {
            var option = this.getOptionByRow(index);
            if (option != null) {
                this.selectOption(option);
            }
        }

        isSelectionChanged(): boolean {
            var optionsMap = this.getDisplayedOptions().map((x) => {
                return x.value;
            }).join();
            var selectedOptions: Option<OPTION_DISPLAY_VALUE>[] = this.selectedOptionsCtrl.getOptions();
            var filteredOption = [],
                gridOptions = [];
            for (var k in selectedOptions) {
                if (optionsMap.search(selectedOptions[k].value) >= 0) {
                    filteredOption.push(selectedOptions[k].value);
                }
            }
            this.comboBoxDropdown.getGrid().getElement().getSelectedRows().forEach((row: number) => {
                gridOptions.push(this.comboBoxDropdown.getGrid().getOptionByRow(row).value);
            });

            return (filteredOption.length !== gridOptions.length) ||
                   (filteredOption.sort().join() !== gridOptions.sort().join());
        }

        selectRowOrApplySelection(index: number) {

            // fast alternative to isSelectionChanged()
            if (this.applySelectionsButton && this.applySelectionsButton.isVisible()) {
                this.clearSelection(true);
                this.comboBoxDropdown.applyMultipleSelection();
                this.hideDropdown();
            } else {
                this.selectRow(index);
                this.input.setValue("");
            }
        }

        selectOption(option: Option<OPTION_DISPLAY_VALUE>, silent: boolean = false) {

            // already exists
            for (var k in this.selectedOptionsCtrl.getOptions()) {
                if (this.selectedOptionsCtrl.getOptions()[k].value === option.value) {
                    return;
                }
            }

            var added = this.selectedOptionsCtrl.addOption(option);
            if (!added) {
                return;
            }

            this.comboBoxDropdown.markSelections(this.selectedOptionsCtrl.getOptions());
            this.hideDropdown();
            this.addClass("followed-by-options");

            if (this.maximumOccurrencesReached()) {
                this.input.setMaximumReached();
                if (this.setNextInputFocusWhenMaxReached) {
                    api.dom.FormEl.moveFocusToNextFocusable(this.input);
                }
                this.dropdownHandle.setEnabled(false);
            }
            if (!silent) {
                this.notifyOptionSelected(option);
            }
            if (this.maximumOccurrencesReached() && this.hideComboBoxWhenMaxReached) {
                this.hide();
            }
        }

        removeSelectedOption(optionToRemove: Option<OPTION_DISPLAY_VALUE>, silent: boolean = false) {
            api.util.assertNotNull(optionToRemove, "optionToRemove cannot be null");

            this.selectedOptionsCtrl.removeOption(optionToRemove, silent);

            this.comboBoxDropdown.markSelections(this.selectedOptionsCtrl.getOptions());

            this.input.openForTypingAndFocus();

            this.dropdownHandle.setEnabled(true);
        }

        clearSelection(ignoreEmpty: boolean = false) {
            var optionsMap = this.getDisplayedOptions().map((x) => {
                return x.value;
            }).join();

            var selectedOptions: Option<OPTION_DISPLAY_VALUE>[] = this.selectedOptionsCtrl.getOptions();
            selectedOptions.forEach((option: Option<OPTION_DISPLAY_VALUE>) => {
                // removing selection only from filtered options
                var filteredOption = optionsMap.search(option.value) >= 0 ? option : undefined;
                if (filteredOption) {
                    this.selectedOptionsCtrl.removeOption(option, true);
                }
            });

            this.comboBoxDropdown.markSelections([], ignoreEmpty);

            this.input.openForTypingAndFocus();

            this.dropdownHandle.setEnabled(true);
        }

        getSelectedOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            if (this.multipleSelections) {
                return this.selectedOptionsCtrl.getOptions();
            }
            else {
                throw new Error("Not supported yet");
            }
        }

        getDisplayedOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            var displayedOptions: Option<OPTION_DISPLAY_VALUE>[] = [];

            for (var row = 0; row < this.comboBoxDropdown.getOptionCount(); row++) {
                var option: Option<OPTION_DISPLAY_VALUE> = this.getOptionByRow(row);
                if (option) {
                    displayedOptions.push(option);
                }
            }

            return displayedOptions;
        }

        getValue(): string {
            if (this.multipleSelections) {
                var values = [];
                this.selectedOptionsCtrl.getOptions().forEach((item: Option<OPTION_DISPLAY_VALUE>) => {
                    values.push(item.value);
                });
                return values.join(';');
            }
            else {
                throw new Error("Not supported yet");
            }
        }

        countSelectedOptions(): number {
            if (this.multipleSelections) {
                return this.selectedOptionsCtrl.count();
            }
            else {
                throw new Error("Not supported yet");
            }
        }

        // Checks added occurrences
        maximumOccurrencesReached(): boolean {
            api.util.assert(this.multipleSelections,
                "No point of calling maximumOccurrencesReached when no multiple selections are enabled");

            return this.selectedOptionsCtrl.maximumOccurrencesReached();
        }

        // Checks selected and added occurrences (with filtering)
        maximumSelectionsReached(): boolean {
            if (this.selectedOptionsCtrl && this.selectedOptionsCtrl.getMaximumOccurrences() !== 0) {

                var totalSelected: number = this.comboBoxDropdown.getSelectedOptionCount();
                var optionsMap = this.getDisplayedOptions().map((x) => {
                    return x.value;
                }).join();
                var selectedOptions: Option<OPTION_DISPLAY_VALUE>[] = this.selectedOptionsCtrl.getOptions();
                for (var k in selectedOptions) {
                    if (optionsMap.search(selectedOptions[k].value) < 0) {
                        totalSelected++;
                    }
                }
                return this.selectedOptionsCtrl.getMaximumOccurrences() <= totalSelected;
            } else {
                return false;
            }
        }

        setInputIconUrl(iconUrl: string) {
            if (!this.icon) {
                this.icon = new api.dom.ImgEl();
                this.icon.addClass("input-icon");
                this.icon.insertBeforeEl(this.input);
            }

            this.icon.getEl().setSrc(iconUrl);
        }

        private setupListeners() {

            this.onClicked((event: MouseEvent) => {
                this.setOnBlurListener();
            });

            this.input.onClicked((event: MouseEvent) => {
                this.input.getEl().removeAttribute('readonly');
            });

            this.comboBoxDropdown.onRowSelection((event: DropdownGridRowSelectedEvent) => {
                this.selectRow(event.getRow());
            });

            this.dropdownHandle.onClicked((event: MouseEvent) => {
                event.preventDefault();
                event.stopPropagation();

                this.comboBoxDropdown.navigateToFirstRowIfNotActive();

                if (!this.maximumOccurrencesReached()) {
                    if (this.isDropdownShown()) {
                        this.hideDropdown();
                        this.giveFocus();
                    } else {
                        this.showDropdown();
                        this.giveFocus();
                        this.comboBoxDropdown.navigateToFirstRowIfNotActive();
                    }
                }
            });

            if (this.applySelectionsButton) {
                this.applySelectionsButton.onClicked((event: any) => {
                    this.clearSelection(true);
                    this.comboBoxDropdown.applyMultipleSelection();
                    this.hideDropdown();
                });
                this.comboBoxDropdown.onMultipleSelection(this.handleMultipleSelectionChanged.bind(this));
            }

            this.input.onValueChanged((event: api.ui.ValueChangedEvent) => {

                this.preservedInputValueChangedEvent = event;
                if (this.delayedInputValueChangedHandling == 0) {
                    this.handleInputValueChanged();
                }
                else {
                    this.delayedHandleInputValueChangedFnCall.delayCall();
                }
            });

            this.input.onDblClicked((event: MouseEvent) => {
                event.preventDefault();
                event.stopPropagation();

                if (!this.isDropdownShown()) {
                    this.showDropdown();
                    this.input.getEl().removeAttribute('readonly');
                }
            });

            this.onKeyDown(this.handleKeyDown.bind(this));

            if (this.multipleSelections) {
                this.selectedOptionsCtrl.onSelectedOptionRemoved(
                    (removedOption: SelectedOption<OPTION_DISPLAY_VALUE>) => {
                        this.handleSelectedOptionRemoved(removedOption);
                    });
            }
        }

        private handleInputValueChanged() {

            if (this.preservedInputValueChangedEvent) {

                this.notifyOptionFilterInputValueChanged(this.preservedInputValueChangedEvent.getOldValue(),
                    this.preservedInputValueChangedEvent.getNewValue());

                if (this.isDropdownShown()) {
                    this.showDropdown();
                }
                this.comboBoxDropdown.resetActiveSelection();

                this.input.getEl().removeAttribute('readonly');
            }
        }

        private handleKeyDown(event: KeyboardEvent) {

            if (event.which == 9) { // tab
                this.hideDropdown();
                return;
            } else if (event.which == 16 || event.which == 17 || event.which == 18 || event.which == 91) {  // shift or ctrl or alt or super
                return;
            }

            if (!this.isDropdownShown()) {
                this.showDropdown();
                if (event.which === 40) { // down
                    this.comboBoxDropdown.nagivateToFirstRow();
                    this.input.getEl().setAttribute('readonly', 'readonly');
                } else {
                    this.input.getEl().removeAttribute('readonly');
                }
                return;
            }

            switch (event.which) {
            case 38: // up
                if (this.comboBoxDropdown.hasActiveRow()) {
                    if (this.comboBoxDropdown.getActiveRow() === 0) {
                        this.comboBoxDropdown.resetActiveSelection();
                        this.input.getEl().removeAttribute('readonly');
                    } else {
                        this.comboBoxDropdown.navigateToPreviousRow();
                        this.input.getEl().setAttribute('readonly', 'readonly');
                    }
                }
                event.stopPropagation();
                event.preventDefault();
                break;
            case 40: // down
                if (this.comboBoxDropdown.hasActiveRow()) {
                    this.comboBoxDropdown.navigateToNextRow();
                } else {
                    this.comboBoxDropdown.nagivateToFirstRow();
                }
                this.input.getEl().setAttribute('readonly', 'readonly');
                event.stopPropagation();
                event.preventDefault();
                break;
            case 13: // Enter
                this.selectRowOrApplySelection(this.comboBoxDropdown.getActiveRow());
                break;
            case 32: // Spacebar
                if (this.input.getEl().getAttribute('readonly') && this.applySelectionsButton) {
                    this.comboBoxDropdown.toggleRowSelection(this.comboBoxDropdown.getActiveRow(), this.maximumSelectionsReached());
                    event.stopPropagation();
                    event.preventDefault();
                }
                break;
            case 8:
                if (this.input.getEl().getAttribute('readonly')) {
                    event.stopPropagation();
                    event.preventDefault();
                }
                break;
            case 27: // Esc
                this.hideDropdown();
                break;
            }

            this.focusOnInput();

        }

        private focusOnInput(focus?: boolean) {
            var isReadOnly = !!this.input.getEl().getAttribute('readonly');
            focus = focus || isReadOnly;
            this.input.giveFocus();
            if (focus) {
                if (isReadOnly) {
                    this.input.getEl().removeAttribute('readonly');
                    this.input.getEl().setAttribute('readonly', 'readonly');
                } else {
                    this.input.getEl().setAttribute('readonly', 'readonly');
                    this.input.getEl().removeAttribute('readonly');
                }
            }
        }

        private handleSelectedOptionRemoved(removedSelectedOption: SelectedOption<OPTION_DISPLAY_VALUE>) {
            this.comboBoxDropdown.markSelections(this.selectedOptionsCtrl.getOptions());
            this.input.openForTypingAndFocus();

            this.dropdownHandle.setEnabled(true);

            if (this.hideComboBoxWhenMaxReached) {
                this.show();
            }

            if (this.countSelectedOptions() == 0) {
                this.removeClass("followed-by-options");
            }
        }

        private handleMultipleSelectionChanged(event: DropdownGridMultipleSelectionEvent) {
            this.focusOnInput();
            if (this.isSelectionChanged()) {
                this.applySelectionsButton.show();
            } else {
                this.applySelectionsButton.hide();
            }
        }

        /**
         * Setup event listener that hides dropdown when combobox loses focus.
         * Listener is added to document body when combobox makes active and removed on click outside of combobox.
         */
        private setOnBlurListener() {
            // reference to this combobox to use it in closure
            var combobox = this;

            // function variable to be able to add and remove it as listener
            var hideDropdownOnBlur = function (event: Event) {

                var comboboxHtmlElement = combobox.getHTMLElement();

                // check if event occured inside combobox then do nothing and return
                for (var element = event.target; element; element = (<any>element).parentNode) {
                    if (element == comboboxHtmlElement) {
                        return;
                    }
                }

                // if combobox lost focus then hide dropdown options and remove unnecessary listener
                combobox.hideDropdown();
                combobox.active = false;
                api.dom.Body.get().getEl().removeEventListener('click', hideDropdownOnBlur);
            };

            // set callback function on document body if combobox wasn't marked as active
            if (!this.active) {
                this.active = true;
                api.dom.Body.get().onClicked(hideDropdownOnBlur);
            }
        }

        onOptionSelected(listener: (event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.optionSelectedListeners.push(listener);
        }

        unOptionSelected(listener: (event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.optionSelectedListeners.filter((currentListener: (event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                return listener != currentListener;
            });
        }

        private notifyOptionSelected(item: Option<OPTION_DISPLAY_VALUE>) {
            var event = new OptionSelectedEvent<OPTION_DISPLAY_VALUE>(item);
            this.optionSelectedListeners.forEach((listener: (event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                listener(event);
            });
        }

        onOptionFilterInputValueChanged(listener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.optionFilterInputValueChangedListeners.push(listener);
        }

        unOptionFilterInputValueChanged(listener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.optionFilterInputValueChangedListeners.filter((currentListener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                return listener != currentListener;
            });
        }

        private notifyOptionFilterInputValueChanged(oldValue: string, newValue: string) {
            var event = new OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>(oldValue, newValue,
                this.comboBoxDropdown.getGrid().getElement());
            this.optionFilterInputValueChangedListeners.forEach((listener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                listener(event);
            });
        }

        onSelectedOptionRemoved(listener: {(removed: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
            this.selectedOptionsCtrl.onSelectedOptionRemoved(listener);
        }

        unSelectedOptionRemoved(listener: {(removed: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
            this.selectedOptionsCtrl.unSelectedOptionRemoved(listener);
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.input.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.input.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.input.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.input.unBlur(listener);
        }
    }

}