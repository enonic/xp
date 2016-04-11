module api.ui.selector.combobox {

    import Option = api.ui.selector.Option;
    import OptionFilterInputValueChangedEvent = api.ui.selector.OptionFilterInputValueChangedEvent;
    import DropdownHandle = api.ui.selector.DropdownHandle;
    import Viewer = api.ui.Viewer;
    import DelayedFunctionCall = api.util.DelayedFunctionCall;
    import Button = api.ui.button.Button;

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

        minWidth?: number;

        value?: string;

    }

    export class ComboBox<OPTION_DISPLAY_VALUE> extends api.dom.FormInputEl {

        private icon: api.dom.ImgEl;

        private dropdownHandle: DropdownHandle;

        private applySelectionsButton: Button;

        private input: ComboBoxOptionFilterInput;

        private delayedInputValueChangedHandling;

        private delayedHandleInputValueChangedFnCall: DelayedFunctionCall;

        private preservedInputValueChangedEvent: api.ValueChangedEvent;

        private selectedOptionsView: SelectedOptionsView<OPTION_DISPLAY_VALUE>;

        private comboBoxDropdown: ComboBoxDropdown<OPTION_DISPLAY_VALUE>;

        private hideComboBoxWhenMaxReached: boolean;

        private setNextInputFocusWhenMaxReached: boolean = true;

        private ignoreNextFocus: boolean = false;

        private minWidth: number = -1;

        private optionFilterInputValueChangedListeners: {(event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>):void}[] = [];

        private expandedListeners: {(event: api.ui.selector.DropdownExpandedEvent): void}[] = [];

        private selectiondDelta = [];

        public static debug: boolean = false;

        /**
         * Indicates if combobox is currently has focus
         * @type {boolean}
         */
        private active: boolean = false;

        constructor(name: string, config: ComboBoxConfig<OPTION_DISPLAY_VALUE>) {
            super("div", "combobox", api.StyleHelper.COMMON_PREFIX, config.value);
            this.getEl().setAttribute("name", name);

            this.hideComboBoxWhenMaxReached = config.hideComboBoxWhenMaxReached;
            if (config.setNextInputFocusWhenMaxReached !== undefined) {
                this.setNextInputFocusWhenMaxReached = config.setNextInputFocusWhenMaxReached;
            }
            if (config.selectedOptionsView != null) {
                this.selectedOptionsView = config.selectedOptionsView;
                this.selectedOptionsView.setMaximumOccurrences(config.maximumOccurrences != null ? config.maximumOccurrences : 0);
            }
            if (config.iconUrl) {
                this.icon = new api.dom.ImgEl(config.iconUrl, "input-icon");
                this.appendChild(this.icon);
            }

            if (config.minWidth) {
                this.minWidth = config.minWidth;
            }

            this.input = new ComboBoxOptionFilterInput();
            this.appendChild(this.input);

            this.delayedInputValueChangedHandling = config.delayedInputValueChangedHandling || 0;
            this.delayedHandleInputValueChangedFnCall = new DelayedFunctionCall(this.handleInputValueChanged, this,
                this.delayedInputValueChangedHandling);

            this.dropdownHandle = new DropdownHandle();
            this.appendChild(this.dropdownHandle);

            if (this.selectedOptionsView && (config.maximumOccurrences != 1)) {
                this.applySelectionsButton = new Button("Apply");
                this.applySelectionsButton.addClass('small apply-button');
                this.applySelectionsButton.hide();
                this.appendChild(this.applySelectionsButton);
            }

            this.comboBoxDropdown = new ComboBoxDropdown(<ComboBoxDropdownConfig<OPTION_DISPLAY_VALUE>>{
                maxHeight: 200,
                width: this.input.getWidth(),
                optionDisplayValueViewer: config.optionDisplayValueViewer,
                filter: config.filter,
                dataIdProperty: config.dataIdProperty,
                multipleSelections: (this.selectedOptionsView && (config.maximumOccurrences != 1))
            });

            this.appendChild(this.comboBoxDropdown.getEmptyDropdown());
            this.appendChild(this.comboBoxDropdown.getDropdownGrid().getElement());

            this.setupListeners();

            this.onRendered((event: api.dom.ElementRenderedEvent) => {

                this.doUpdateDropdownTopPositionAndWidth();
            });
        }

        private doUpdateDropdownTopPositionAndWidth() {
            var inputEl = this.input.getEl();
            this.comboBoxDropdown.setTopPx(inputEl.getHeightWithBorder() - inputEl.getBorderBottomWidth());
            this.comboBoxDropdown.setWidth(Math.max(inputEl.getWidthWithBorder(), this.minWidth));
        }

        giveFocus(): boolean {
            return this.input.giveFocus();
        }

        giveInputFocus() {
            this.input.setReadOnly(false);
            this.input.giveFocus();
        }

        getComboBoxDropdownGrid() {
            return this.comboBoxDropdown.getDropdownGrid();
        }

        isDropdownShown(): boolean {
            return this.comboBoxDropdown.isDropdownShown();
        }

        showDropdown() {

            this.doUpdateDropdownTopPositionAndWidth();
            this.notifyExpanded(true);
            this.comboBoxDropdown.showDropdown(this.getSelectedOptions());
            this.dropdownHandle.down();

            this.comboBoxDropdown.renderDropdownGrid();

            this.input.setReadOnly(true);

            this.setOnBlurListener();
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

            this.input.setReadOnly(false);
        }

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[], saveSelection?: boolean) {
            this.comboBoxDropdown.setOptions(options, this.getSelectedOptions(), saveSelection);
        }

        addOption(option: Option<OPTION_DISPLAY_VALUE>) {
            this.comboBoxDropdown.addOption(option);
        }

        setIgnoreNextFocus(value: boolean): ComboBox<OPTION_DISPLAY_VALUE> {
            this.ignoreNextFocus = value;
            return this;
        }

        isIgnoreNextFocus(): boolean {
            return this.ignoreNextFocus;
        }

        /**
         * Invoked after
         */
        loadOptionsAfterShowDropdown(): wemQ.Promise<void> {

            return api.util.PromiseHelper.newResolvedVoidPromise();
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

        protected doGetValue(): string {
            if (this.selectedOptionsView) {
                return this.getSelectedOptions().map((item: Option<OPTION_DISPLAY_VALUE>) => item.value).join(';');
            } else {
                throw new Error("Not supported yet");
            }
        }

        protected doSetValue(value: string, silent?: boolean) {
            if (ComboBox.debug) {
                console.debug('ComboBox.doSetValue:', value);
            }
            if (this.countSelectedOptions() > 0) {
                this.clearSelection(false, false, true);
            }

            this.splitValues(value).forEach((val) => {
                var option = this.getOptionByValue(val);
                if (option != null) {
                    this.selectOption(option, true);
                }
            });
        }

        protected splitValues(value: string): string[] {
            return value.split(';');
        }

        handleRowSelected(index: number) {
            var option = this.getOptionByRow(index);
            if (option != null && !option.readOnly) {
                if (!this.isOptionSelected(option)) {
                    this.selectOption(option);
                } else {
                    this.deselectOption(option);
                }
            }
            this.refreshDirtyState();
            this.refreshValueChanged();
        }

        isSelectionChanged(): boolean {
            var optionsMap = this.getDisplayedOptions().map((x) => {
                return x.value;
            }).join();
            var selectedOptions: Option<OPTION_DISPLAY_VALUE>[] = this.getSelectedOptions();
            var filteredOption = [],
                gridOptions = [];
            for (var k in selectedOptions) {
                if (optionsMap.search(selectedOptions[k].value) >= 0) {
                    filteredOption.push(selectedOptions[k].value);
                }
            }
            this.comboBoxDropdown.getDropdownGrid().getElement().getSelectedRows().forEach((row: number) => {
                gridOptions.push(this.comboBoxDropdown.getDropdownGrid().getOptionByRow(row).value);
            });

            return (filteredOption.length !== gridOptions.length) ||
                   (filteredOption.sort().join() !== gridOptions.sort().join());
        }

        selectRowOrApplySelection(index: number) {

            // fast alternative to isSelectionChanged()
            if (this.applySelectionsButton && this.applySelectionsButton.isVisible()) {
                this.selectiondDelta.forEach((value: string) => {
                    var row = this.comboBoxDropdown.getDropdownGrid().getRowByValue(value);
                    this.handleRowSelected(row);
                });
                this.input.setValue("");
                this.hideDropdown();
            } else {
                this.handleRowSelected(index);
                this.input.setValue("");
            }
        }

        selectOption(option: Option<OPTION_DISPLAY_VALUE>, silent: boolean = false) {
            api.util.assertNotNull(option, "option cannot be null");
            if (this.isOptionSelected(option)) {
                return;
            }

            var added = this.selectedOptionsView.addOption(option, silent);
            if (!added) {
                return;
            }

            this.comboBoxDropdown.markSelections(this.getSelectedOptions());
            this.hideDropdown();
            this.addClass("followed-by-options");

            if (this.maximumOccurrencesReached()) {
                this.input.setMaximumReached();
                if (this.setNextInputFocusWhenMaxReached && !this.ignoreNextFocus) {
                    api.dom.FormEl.moveFocusToNextFocusable(this.input);
                }
                this.dropdownHandle.setEnabled(false);
            }

            if (this.maximumOccurrencesReached() && this.hideComboBoxWhenMaxReached) {
                this.hide();
            }
            this.ignoreNextFocus = false;
        }

        isOptionSelected(option: Option<OPTION_DISPLAY_VALUE>): boolean {
            return this.selectedOptionsView.isSelected(option);
        }

        deselectOption(option: Option<OPTION_DISPLAY_VALUE>, silent: boolean = false) {
            api.util.assertNotNull(option, "option cannot be null");
            if (!this.isOptionSelected(option)) {
                return;
            }

            this.selectedOptionsView.removeOption(option, silent);

            this.comboBoxDropdown.markSelections(this.getSelectedOptions());
            this.hideDropdown();

            this.input.openForTypingAndFocus();

            this.dropdownHandle.setEnabled(true);

            if (this.hideComboBoxWhenMaxReached) {
                if (this.isVisible() && this.maximumOccurrencesReached()) {
                    this.hide();
                }

                if (!this.isVisible() && !this.maximumOccurrencesReached()) {
                    this.show();
                }

            }
        }

        clearSelection(ignoreEmpty: boolean = false, giveInputFocus: boolean = true, forceClear: boolean = false) {
            var optionsMap = this.getDisplayedOptions().map((x) => x.value).join();

            var selectedOptions: Option<OPTION_DISPLAY_VALUE>[] = this.getSelectedOptions();
            selectedOptions.forEach((option: Option<OPTION_DISPLAY_VALUE>) => {
                if (forceClear) {
                    this.selectedOptionsView.removeOption(option, true);
                } else {
                    // removing selection only from filtered options
                    var filteredOption = optionsMap.search(option.value) >= 0 ? option : undefined;
                    if (filteredOption && !filteredOption.readOnly) {
                        this.selectedOptionsView.removeOption(option, true);
                    }
                }
            });

            this.comboBoxDropdown.markSelections([], ignoreEmpty);

            if (giveInputFocus) {
                this.input.openForTypingAndFocus();
            }
            else {
                this.input.openForTyping();
            }

            this.dropdownHandle.setEnabled(true);

            if (this.hideComboBoxWhenMaxReached) {
                this.show();
            }
        }

        getSelectedOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            if (this.selectedOptionsView) {
                return this.selectedOptionsView.getSelectedOptions().map((selectedOption: SelectedOption<OPTION_DISPLAY_VALUE>) => {
                    return selectedOption.getOption();
                });
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

        countSelectedOptions(): number {
            if (this.selectedOptionsView) {
                return this.selectedOptionsView.count();
            }
            else {
                throw new Error("Not supported yet");
            }
        }

        // Checks added occurrences
        maximumOccurrencesReached(): boolean {
            api.util.assert(this.selectedOptionsView != null,
                "No point of calling maximumOccurrencesReached when no multiple selections are enabled");

            return this.selectedOptionsView.maximumOccurrencesReached();
        }

        // Checks selected and added occurrences (with filtering)
        maximumSelectionsReached(): boolean {
            if (this.selectedOptionsView && this.selectedOptionsView.getMaximumOccurrences() !== 0) {

                var totalSelected: number = this.comboBoxDropdown.getSelectedOptionCount();
                var optionsMap = this.getDisplayedOptions().map((x) => x.value).join();
                totalSelected += this.getSelectedOptions().
                    filter((option: Option<OPTION_DISPLAY_VALUE>) => (optionsMap.search(option.value) < 0)).length;

                return this.selectedOptionsView.getMaximumOccurrences() <= totalSelected;
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

        getInput(): ComboBoxOptionFilterInput {
            return this.input;
        }

        private setupListeners() {

            this.onClicked((event: MouseEvent) => {
                this.setOnBlurListener();
            });

            this.onScrolled((event: WheelEvent) => {
                event.stopPropagation();
            });

            this.input.onClicked((event: MouseEvent) => {
                this.giveInputFocus();
                event.stopPropagation();
            });

            this.comboBoxDropdown.onRowSelection((event: DropdownGridRowSelectedEvent) => {
                this.handleRowSelected(event.getRow());
            });

            this.dropdownHandle.onClicked((event: MouseEvent) => {
                event.preventDefault();
                event.stopPropagation();

                if (!this.maximumOccurrencesReached()) {
                    if (this.isDropdownShown()) {
                        this.hideDropdown();
                        this.giveInputFocus();
                    }
                    else {
                        this.showDropdown();
                        this.loadOptionsAfterShowDropdown().then(() => {
                            this.giveInputFocus();
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        }).done();

                    }
                }
            });

            if (this.applySelectionsButton) {
                this.applySelectionsButton.onClicked(this.selectRowOrApplySelection.bind(this, -1));
                this.comboBoxDropdown.onMultipleSelection(this.handleMultipleSelectionChanged.bind(this));
            }

            this.input.onValueChanged((event: api.ValueChangedEvent) => {

                this.preservedInputValueChangedEvent = event;
                if (this.delayedInputValueChangedHandling == 0) {
                    this.handleInputValueChanged();
                }
                else {
                    this.setEmptyDropdownText("Just keep on typing...");
                    this.delayedHandleInputValueChangedFnCall.delayCall();
                }
            });

            this.input.onDblClicked((event: MouseEvent) => {
                event.preventDefault();
                event.stopPropagation();

                if (!this.isDropdownShown()) {
                    this.showDropdown();
                    this.loadOptionsAfterShowDropdown().then(() => {
                        this.comboBoxDropdown.navigateToRowIfNotActive();
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();
                    this.input.setReadOnly(false);
                }
            });

            this.onKeyDown(this.handleKeyDown.bind(this));

            if (this.selectedOptionsView) {
                this.selectedOptionsView.onOptionDeselected((removedOption: SelectedOption<OPTION_DISPLAY_VALUE>) => {
                    this.handleSelectedOptionRemoved(removedOption);
                });
                this.selectedOptionsView.onOptionSelected((addedOption: SelectedOption<OPTION_DISPLAY_VALUE>) => {
                    this.handleSelectedOptionAdded(addedOption);
                });
                this.selectedOptionsView.onOptionMoved((movedOption: SelectedOption<OPTION_DISPLAY_VALUE>) => {
                    this.handleSelectedOptionMoved(movedOption);
                });
            }
        }

        private handleInputValueChanged() {

            if (this.preservedInputValueChangedEvent) {

                this.notifyOptionFilterInputValueChanged(this.preservedInputValueChangedEvent.getOldValue(),
                    this.preservedInputValueChangedEvent.getNewValue());

                this.comboBoxDropdown.resetActiveSelection();
                this.showDropdown();

                this.input.setReadOnly(false);
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

                if (event.which === 40) { // down

                    this.showDropdown();
                    this.loadOptionsAfterShowDropdown().then(() => {

                        this.comboBoxDropdown.navigateToRowIfNotActive();
                        this.input.setReadOnly(true);

                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();

                } else {
                    this.showDropdown();
                    this.input.setReadOnly(false);
                }
                return;
            }

            switch (event.which) {
            case 38: // up
                if (this.comboBoxDropdown.hasActiveRow()) {
                    if (this.comboBoxDropdown.getActiveRow() === 0) {
                        this.comboBoxDropdown.resetActiveSelection();
                        this.input.setReadOnly(false);
                        this.input.giveFocus();
                    } else {
                        this.comboBoxDropdown.navigateToPreviousRow();
                        this.input.setReadOnly(true);
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
                this.input.setReadOnly(true);
                event.stopPropagation();
                event.preventDefault();
                break;
            case 13: // Enter
                this.selectRowOrApplySelection(this.comboBoxDropdown.getActiveRow());
                event.stopPropagation();
                event.preventDefault();
                break;
            case 32: // Spacebar
                if (this.input.isReadOnly() && this.applySelectionsButton) {

                    if (!this.isSelectedRowReadOnly()) {
                        this.comboBoxDropdown.toggleRowSelection(this.comboBoxDropdown.getActiveRow(), this.maximumSelectionsReached());
                    }

                    event.stopPropagation();
                    event.preventDefault();
                }
                break;
            case 8:
                if (this.input.isReadOnly()) {
                    event.stopPropagation();
                    event.preventDefault();
                }
                break;
            case 27: // Esc
                this.hideDropdown();
                break;
            }

            this.input.giveFocus()
        }

        private isSelectedRowReadOnly(): boolean {
            return this.getOptionByRow(this.comboBoxDropdown.getActiveRow()).readOnly;
        }

        private handleSelectedOptionRemoved(removedSelectedOption: SelectedOption<OPTION_DISPLAY_VALUE>) {
            this.comboBoxDropdown.markSelections(this.getSelectedOptions());

            this.dropdownHandle.setEnabled(true);

            if (this.hideComboBoxWhenMaxReached) {
                this.show();
            }

            if (this.countSelectedOptions() == 0) {
                this.removeClass("followed-by-options");
            }
            this.input.openForTypingAndFocus();

            this.refreshDirtyState();
            this.refreshValueChanged();
        }

        private handleSelectedOptionAdded(addedSelectedOption: SelectedOption<OPTION_DISPLAY_VALUE>) {

            this.refreshDirtyState();
            this.refreshValueChanged();
        }

        private handleSelectedOptionMoved(movedSelectedOption: SelectedOption<OPTION_DISPLAY_VALUE>) {

            this.refreshDirtyState();
            this.refreshValueChanged();
        }

        private handleMultipleSelectionChanged(event: DropdownGridMultipleSelectionEvent) {
            this.input.giveFocus();
            if (this.isSelectionChanged()) {
                this.applySelectionsButton.show();
                this.updateSelectionDelta();
            } else {
                this.applySelectionsButton.hide();
            }
        }

        private updateSelectionDelta() {

            var selectedValues = this.getSelectedOptions().map((x) => {
                return x.value;
            });

            var gridOptions = [];

            this.comboBoxDropdown.getDropdownGrid().getElement().getSelectedRows().forEach((row: number) => {
                gridOptions.push(this.comboBoxDropdown.getDropdownGrid().getOptionByRow(row).value);
            });

            this.selectiondDelta = gridOptions
                .filter(x => selectedValues.indexOf(x) == -1)
                .concat(selectedValues.filter(x => gridOptions.indexOf(x) == -1));

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

        onOptionSelected(listener: (event: SelectedOption<OPTION_DISPLAY_VALUE>)=>void) {
            this.selectedOptionsView.onOptionSelected(listener);
        }

        unOptionSelected(listener: (event: SelectedOption<OPTION_DISPLAY_VALUE>)=>void) {
            this.selectedOptionsView.unOptionSelected(listener);
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
                this.comboBoxDropdown.getDropdownGrid().getElement());
            this.optionFilterInputValueChangedListeners.forEach((listener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                listener(event);
            });
        }

        onExpanded(listener: (event: api.ui.selector.DropdownExpandedEvent)=>void) {
            this.expandedListeners.push(listener);
        }

        private notifyExpanded(expanded: boolean) {
            var event = new api.ui.selector.DropdownExpandedEvent(this.comboBoxDropdown.getDropdownGrid().getElement(), expanded);
            this.expandedListeners.forEach((listener: (event: api.ui.selector.DropdownExpandedEvent)=>void) => {
                listener(event);
            });
        }

        onOptionDeselected(listener: {(removed: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
            this.selectedOptionsView.onOptionDeselected(listener);
        }

        unOptionDeselected(listener: {(removed: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
            this.selectedOptionsView.unOptionDeselected(listener);
        }

        onOptionMoved(listener: {(moved: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
            this.selectedOptionsView.onOptionMoved(listener);
        }

        unOptionMoved(listener: {(moved: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
            this.selectedOptionsView.unOptionMoved(listener);
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

        onScrolled(listener: (event: WheelEvent) => void) {
            this.comboBoxDropdown.getDropdownGrid().getElement().subscribeOnScrolled(listener);
        }

        onScroll(listener: (event: Event) => void) {
            this.comboBoxDropdown.getDropdownGrid().getElement().subscribeOnScroll(listener);
        }
    }

}