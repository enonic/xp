module api.ui.selector.combobox {

    import Option = api.ui.selector.Option;
    import OptionFilterInputValueChangedEvent = api.ui.selector.OptionFilterInputValueChangedEvent;
    import DropdownHandle = api.ui.button.DropdownHandle;
    import Viewer = api.ui.Viewer;
    import DelayedFunctionCall = api.util.DelayedFunctionCall;
    import Button = api.ui.button.Button;
    import ElementHelper = api.dom.ElementHelper;

    export interface ComboBoxConfig<T> {

        iconUrl?: string;

        optionDisplayValueViewer?: Viewer<T>;

        selectedOptionsView: SelectedOptionsView<T>;

        maximumOccurrences?: number;

        filter?: (item: any, args: any) => boolean;

        hideComboBoxWhenMaxReached?: boolean;

        setNextInputFocusWhenMaxReached?: boolean;

        dataIdProperty?: string;

        delayedInputValueChangedHandling?: number;

        minWidth?: number;

        maxHeight?: number;

        value?: string;

        noOptionsText?: string;

        displayMissingSelectedOptions?: boolean;

        removeMissingSelectedOptions?: boolean;

        skipAutoDropShowOnValueChange?: boolean;
    }

    export enum PositionType {
        BELOW,
        ABOVE,
        FLEXIBLE_BELOW,
        FLEXIBLE_ABOVE
    }

    export interface DropdownPosition {

        position: PositionType;

        height: number;
    }

    export class ComboBox<OPTION_DISPLAY_VALUE> extends api.dom.FormInputEl {

        private icon: api.dom.ImgEl;

        private dropdownHandle: DropdownHandle;

        private applySelectionsButton: Button;

        private input: ComboBoxOptionFilterInput;

        private delayedInputValueChangedHandling: number;

        private delayedHandleInputValueChangedFnCall: DelayedFunctionCall;

        private preservedInputValueChangedEvent: api.ValueChangedEvent;

        private selectedOptionsView: SelectedOptionsView<OPTION_DISPLAY_VALUE>;

        private comboBoxDropdown: ComboBoxDropdown<OPTION_DISPLAY_VALUE>;

        private hideComboBoxWhenMaxReached: boolean;

        private setNextInputFocusWhenMaxReached: boolean = true;

        private ignoreNextFocus: boolean = false;

        private minWidth: number = -1;

        private optionFilterInputValueChangedListeners: {(event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>): void}[] = [];

        private expandedListeners: {(event: api.ui.selector.DropdownExpandedEvent): void}[] = [];

        private valueLoadedListeners: {(options: Option<OPTION_DISPLAY_VALUE>[]): void}[] = [];

        private contentMissingListeners: {(ids: string[]): void}[] = [];

        private selectiondDelta: string[] = [];

        private noOptionsText: string;

        private displayMissingSelectedOptions: boolean = false;

        private removeMissingSelectedOptions: boolean = false;

        private skipAutoDropShowOnValueChange: boolean = false;

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

            if (config.displayMissingSelectedOptions) {
                this.displayMissingSelectedOptions = config.displayMissingSelectedOptions;
            }

            if (config.removeMissingSelectedOptions) {
                this.removeMissingSelectedOptions = config.removeMissingSelectedOptions;
            }

            if (config.skipAutoDropShowOnValueChange) {
                this.skipAutoDropShowOnValueChange = config.skipAutoDropShowOnValueChange;
            }

            this.noOptionsText = config.noOptionsText;

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
                maxHeight: config.maxHeight ? config.maxHeight : 200,
                width: this.input.getWidth(),
                optionDisplayValueViewer: config.optionDisplayValueViewer,
                filter: config.filter,
                dataIdProperty: config.dataIdProperty,
                multipleSelections: (this.selectedOptionsView && (config.maximumOccurrences != 1))
            });

            this.appendChild(this.comboBoxDropdown.getEmptyDropdown());
            this.appendChild(<api.dom.Element>this.comboBoxDropdown.getDropdownGrid().getElement());

            this.setupListeners();
        }

        setReadOnly(readOnly: boolean) {
            super.setReadOnly(readOnly);

            this.input.setReadOnly(readOnly);
            this.selectedOptionsView.setEditable(!readOnly);

            this.toggleClass('readonly', readOnly);
        }

        private doUpdateDropdownTopPositionAndWidth() {
            const dropdownPosition = this.dropdownOverflowsBottom();

            switch (dropdownPosition.position) {
            case PositionType.BELOW:
                this.placeDropdownBelow();
                break;
            case PositionType.ABOVE:
                this.placeDropdownAbove();
                break;
            case PositionType.FLEXIBLE_BELOW:
                // change dd height
                this.comboBoxDropdown.resizeDropdownTo(dropdownPosition.height);
                this.placeDropdownBelow();
                break;
            case PositionType.FLEXIBLE_ABOVE:
                // change dd height
                this.comboBoxDropdown.resizeDropdownTo(dropdownPosition.height);
                this.placeDropdownAbove();
            }

            // reset the custom height, after dropdown is shown
            this.comboBoxDropdown.resetDropdownSize();

            this.comboBoxDropdown.setWidth(Math.max(this.input.getEl().getWidthWithBorder(), this.minWidth));
        }

        private dropdownOverflowsBottom(): DropdownPosition {
            const inputEl = this.input.getEl();
            const parent = this.getScrollableParent(inputEl);
            const dropdown = this.comboBoxDropdown.getDropdownGrid().getElement().getEl();

            // distance is measured from the top of the viewport
            const distanceToParentsTop = parent.getOffsetTop();
            const distanceToInputsTop = inputEl.getOffsetTop();

            const distanceToParentsBottom = distanceToParentsTop + parent.getHeight();
            const distanceToInputsBottom = distanceToInputsTop + inputEl.getHeight();

            const sizeAboveInput = distanceToInputsTop - distanceToParentsTop;
            const sizeBelowInput = distanceToParentsBottom - distanceToInputsBottom;

            const dropdownHeight = dropdown.getHeightWithBorder();

            let position;
            let height;

            if (sizeBelowInput > dropdownHeight) {
                position = PositionType.BELOW;
                height = dropdownHeight;
            } else if (sizeAboveInput > dropdownHeight) {
                position = PositionType.ABOVE;
                height = dropdownHeight;
            } else if (sizeBelowInput > sizeAboveInput) {
                position = PositionType.FLEXIBLE_BELOW;
                height = sizeBelowInput;
            } else { //sizeBelowInput < sizeAboveInput
                position = PositionType.FLEXIBLE_ABOVE;
                height = sizeAboveInput;
            }

            return {position, height};
        }

        private placeDropdownBelow() {
            let dropdown = this.comboBoxDropdown.getDropdownGrid().getElement().getEl();
            dropdown.removeClass("reverted");

            let inputEl = this.input.getEl();
            this.comboBoxDropdown.setTopPx(inputEl.getHeightWithBorder() - inputEl.getBorderBottomWidth());
        }

        private placeDropdownAbove() {
            let dropdown = this.comboBoxDropdown.getDropdownGrid().getElement().getEl(),
                placeholder = this.comboBoxDropdown.getEmptyDropdown().getEl();

            dropdown.setTopPx(-dropdown.getHeightWithBorder()).addClass("reverted");
            placeholder.setTopPx(-placeholder.getHeightWithBorder());
        }

        private getScrollableParent(el: ElementHelper): ElementHelper {
            let parent = el.getParent();

            if (!parent) {
                return el;
            }

            if (parent.isScrollable()) {
                return parent;
            }

            return this.getScrollableParent(parent);
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

            this.comboBoxDropdown.showDropdown(this.getSelectedOptions(), this.isInputEmpty() ? this.noOptionsText : null);

            this.doUpdateDropdownTopPositionAndWidth();

            this.notifyExpanded(true);

            this.dropdownHandle.down();

            this.comboBoxDropdown.renderDropdownGrid();

            this.input.setReadOnly(true);

            this.addClass("expanded");
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
            this.removeClass("expanded");
        }

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[], saveSelection?: boolean) {
            this.comboBoxDropdown.setOptions(options, this.isInputEmpty() ? this.noOptionsText : null, this.getSelectedOptions(),
                saveSelection);

            this.doUpdateDropdownTopPositionAndWidth();
        }

        private isInputEmpty(): boolean {
            return this.input.getValue() === "";
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

            let valueSetPromise;
            let optionIds = this.splitValues(value),
                missingOptionIds = this.getMissingOptionsIds(optionIds);

            if (this.displayMissingSelectedOptions || this.removeMissingSelectedOptions && missingOptionIds.length > 0) {
                valueSetPromise = this.selectExistingAndHandleMissing(optionIds, missingOptionIds);
            } else {
                valueSetPromise = wemQ(this.selectExistingOptions(value));
            }
            valueSetPromise.done((options) => this.notifyValueLoaded(options));
        }

        private selectExistingOptions(value: string) {
            let selectedOptions = [];
            this.splitValues(value).forEach((val) => {
                let option = this.getOptionByValue(val);
                if (option != null) {
                    selectedOptions.push(option);
                    this.selectOption(option, true);
                }
            });
            return selectedOptions;
        }

        // tslint:disable-next-line:max-line-length
        private selectExistingAndHandleMissing(optionIds: string[], missingOptionIds: string[]): wemQ.Promise<Option<OPTION_DISPLAY_VALUE>[]> {
            let nonExistingIds: string[] = [],
                selectedOptions = [];

            return new api.content.resource.ContentsExistRequest(missingOptionIds).sendAndParse()
                .then((result: api.content.resource.result.ContentsExistResult) => {

                    optionIds.forEach((val) => {
                        const option = this.getOptionByValue(val);
                        if (option != null) {
                            selectedOptions.push(option);
                            this.selectOption(option, true);
                        } else {
                            const contentExists = result.contentExists(val);
                            if (this.displayMissingSelectedOptions && (contentExists || !this.removeMissingSelectedOptions)) {
                                const selectedOption = (<BaseSelectedOptionsView<OPTION_DISPLAY_VALUE>> this.selectedOptionsView)
                                    .makeEmptyOption(val);
                                selectedOptions.push(selectedOption);
                                this.selectOption(selectedOption, true);
                            }
                            if (!contentExists) {
                                nonExistingIds.push(val);
                            }
                        }
                    });

                    if (this.removeMissingSelectedOptions) {
                        this.notifyContentMissing(nonExistingIds);
                    }

                    return selectedOptions;
                });
        }

        private getMissingOptionsIds(values: string[]): string[] {
            let result: string[] = [];
            values.forEach((val) => {
                let option = this.getOptionByValue(val);
                if (option == null && !api.util.StringHelper.isBlank(val)) {
                    result.push(val);
                }
            });
            return result;
        }

        protected splitValues(value: string): string[] {
            return value.split(';');
        }

        handleRowSelected(index: number, keyCode: number = -1) {
            let option = this.getOptionByRow(index);
            if (option != null && !option.readOnly) {
                if (!this.isOptionSelected(option)) {
                    this.selectOption(option, false, keyCode);
                } else {
                    this.deselectOption(option);
                }
            }
            this.refreshDirtyState();
            this.refreshValueChanged();
        }

        isSelectionChanged(): boolean {
            let optionsMap = this.getDisplayedOptions().map((x) => {
                return x.value;
            }).join();
            let selectedOptions: Option<OPTION_DISPLAY_VALUE>[] = this.getSelectedOptions();
            let filteredOption = [],
                gridOptions = [];
            for (let k in selectedOptions) {
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

        selectRowOrApplySelection(index: number, keyCode: number = -1) {

            // fast alternative to isSelectionChanged()
            if (this.applySelectionsButton && this.applySelectionsButton.isVisible()) {
                this.selectiondDelta.forEach((value: string) => {
                    let row = this.comboBoxDropdown.getDropdownGrid().getRowByValue(value);
                    this.handleRowSelected(row, keyCode);
                });
                this.input.setValue("");
                this.hideDropdown();
            } else {
                this.handleRowSelected(index, keyCode);
                this.input.setValue("");
            }
        }

        selectOption(option: Option<OPTION_DISPLAY_VALUE>, silent: boolean = false, keyCode: number = -1) {
            api.util.assertNotNull(option, "option cannot be null");
            if (this.isOptionSelected(option)) {
                return;
            }

            let added = this.selectedOptionsView.addOption(option, silent, keyCode);
            if (!added) {
                return;
            }

            this.comboBoxDropdown.markSelections(this.getSelectedOptions());
            this.hideDropdown();
            this.addClass("followed-by-options");

            if (this.maximumOccurrencesReached()) {
                this.input.setMaximumReached();
                if (this.setNextInputFocusWhenMaxReached && !this.ignoreNextFocus) {
                    api.dom.FormEl.moveFocusToNextFocusable(this.input, "input, select");
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
            let optionsMap = this.getDisplayedOptions().map((x) => x.value).join();

            let selectedOptions: Option<OPTION_DISPLAY_VALUE>[] = this.getSelectedOptions();
            selectedOptions.forEach((option: Option<OPTION_DISPLAY_VALUE>) => {
                if (forceClear) {
                    this.selectedOptionsView.removeOption(option, true);
                } else {
                    // removing selection only from filtered options
                    let filteredOption = optionsMap.search(option.value) >= 0 ? option : undefined;
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
            let displayedOptions: Option<OPTION_DISPLAY_VALUE>[] = [];

            for (let row = 0; row < this.comboBoxDropdown.getOptionCount(); row++) {
                let option: Option<OPTION_DISPLAY_VALUE> = this.getOptionByRow(row);
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

                let totalSelected: number = this.comboBoxDropdown.getSelectedOptionCount();
                let optionsMap = this.getDisplayedOptions().map((x) => x.value).join();
                totalSelected += this.getSelectedOptions().filter(
                    (option: Option<OPTION_DISPLAY_VALUE>) => (optionsMap.search(option.value) < 0)).length;

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

            api.util.AppHelper.focusInOut(this, () => {
                this.hideDropdown();
                this.active = false;
            });

            this.onScrolled((event: WheelEvent) => {
                event.stopPropagation();
            });

            this.getComboBoxDropdownGrid().onClick(() => {
                this.giveInputFocus();
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
                    } else {
                        this.showDropdown();
                        this.giveInputFocus();
                        this.loadOptionsAfterShowDropdown().catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                        });

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
                } else if (!event.valuesAreEqual()) {
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
                this.selectedOptionsView.onOptionDeselected((event: SelectedOptionEvent<OPTION_DISPLAY_VALUE>) => {
                    this.handleSelectedOptionRemoved();
                });
                this.selectedOptionsView.onOptionSelected((event: SelectedOptionEvent<OPTION_DISPLAY_VALUE>) => {
                    this.handleSelectedOptionAdded();
                });
                this.selectedOptionsView.onOptionMoved((movedOption: SelectedOption<OPTION_DISPLAY_VALUE>) => {
                    this.handleSelectedOptionMoved();
                });
            }
        }

        private handleInputValueChanged() {

            if (this.preservedInputValueChangedEvent) {

                this.notifyOptionFilterInputValueChanged(this.preservedInputValueChangedEvent.getOldValue(),
                    this.preservedInputValueChangedEvent.getNewValue());

                this.comboBoxDropdown.resetActiveSelection();
                if (!this.skipAutoDropShowOnValueChange) {
                    this.showDropdown();
                }

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
                this.showDropdown();

                if (event.which === 40) { // down

                    this.loadOptionsAfterShowDropdown().then(() => {

                        this.comboBoxDropdown.navigateToRowIfNotActive();
                        this.input.setReadOnly(true);

                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();

                } else {
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
                break;
            case 40: // down
                if (this.comboBoxDropdown.hasActiveRow()) {
                    this.comboBoxDropdown.navigateToNextRow();
                } else {
                    this.comboBoxDropdown.nagivateToFirstRow();
                }
                this.input.setReadOnly(true);
                break;
            case 13: // Enter
                this.selectRowOrApplySelection(this.comboBoxDropdown.getActiveRow(), 13);
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

            if (event.which !== 13) {
                this.input.giveFocus();
            }

            if (event.which == 38 || event.which == 40 || event.which == 13) {
                event.stopPropagation();
                event.preventDefault();
            }
        }

        private isSelectedRowReadOnly(): boolean {
            return this.getOptionByRow(this.comboBoxDropdown.getActiveRow()).readOnly;
        }

        private handleSelectedOptionRemoved() {
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

        private handleSelectedOptionAdded() {

            this.refreshDirtyState();
            this.refreshValueChanged();
        }

        private handleSelectedOptionMoved() {

            this.refreshDirtyState();
            this.refreshValueChanged();
        }

        private handleMultipleSelectionChanged(event: DropdownGridMultipleSelectionEvent) {
            if (this.isSelectionChanged()) {
                this.applySelectionsButton.show();
                this.updateSelectionDelta();
            } else {
                this.applySelectionsButton.hide();
            }
        }

        private updateSelectionDelta() {

            let selectedValues = this.getSelectedOptions().map((x) => {
                return x.value;
            });

            let gridOptions = [];

            this.comboBoxDropdown.getDropdownGrid().getElement().getSelectedRows().forEach((row: number) => {
                gridOptions.push(this.comboBoxDropdown.getDropdownGrid().getOptionByRow(row).value);
            });

            this.selectiondDelta = gridOptions
                .filter(x => selectedValues.indexOf(x) == -1)
                .concat(selectedValues.filter(x => gridOptions.indexOf(x) == -1));

        }

        onOptionSelected(listener: (event: SelectedOptionEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.selectedOptionsView.onOptionSelected(listener);
        }

        unOptionSelected(listener: (event: SelectedOptionEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.selectedOptionsView.unOptionSelected(listener);
        }

        onOptionFilterInputValueChanged(listener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.optionFilterInputValueChangedListeners.push(listener);
        }

        unOptionFilterInputValueChanged(listener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.optionFilterInputValueChangedListeners.filter(
                (currentListener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                    return listener != currentListener;
                });
        }

        private notifyOptionFilterInputValueChanged(oldValue: string, newValue: string) {
            let event = new OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>(oldValue, newValue,
                this.comboBoxDropdown.getDropdownGrid().getElement());
            this.optionFilterInputValueChangedListeners.forEach(
                (listener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                    listener(event);
                });
        }

        onExpanded(listener: (event: api.ui.selector.DropdownExpandedEvent)=>void) {
            this.expandedListeners.push(listener);
        }

        private notifyExpanded(expanded: boolean) {
            const grid: api.dom.Element = <api.dom.Element>this.comboBoxDropdown.getDropdownGrid().getElement();
            const event = new api.ui.selector.DropdownExpandedEvent(grid, expanded);
            this.expandedListeners.forEach((listener: (event: api.ui.selector.DropdownExpandedEvent)=>void) => {
                listener(event);
            });
        }

        onContentMissing(listener: (ids: string[])=>void) {
            this.contentMissingListeners.push(listener);
        }

        unContentMissing(listener: (ids: string[]) => void) {
            this.contentMissingListeners = this.contentMissingListeners.filter(function (curr: (ids: string[]) => void) {
                return curr !== listener;
            });
        }

        private notifyContentMissing(ids: string[]) {
            this.contentMissingListeners.forEach((listener: (ids: string[])=>void) => {
                listener(ids);
            });
        }

        onValueLoaded(listener: (options: Option<OPTION_DISPLAY_VALUE>[]) => void) {
            this.valueLoadedListeners.push(listener);
        }

        unValueLoaded(listener: (options: Option<OPTION_DISPLAY_VALUE>[]) => void) {
            this.valueLoadedListeners = this.valueLoadedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyValueLoaded(options: Option<OPTION_DISPLAY_VALUE>[]) {
            this.valueLoadedListeners.forEach((listener) => {
                listener(options);
            });
        }

        onOptionDeselected(listener: {(removed: SelectedOptionEvent<OPTION_DISPLAY_VALUE>): void;}) {
            this.selectedOptionsView.onOptionDeselected(listener);
        }

        unOptionDeselected(listener: {(removed: SelectedOptionEvent<OPTION_DISPLAY_VALUE>): void;}) {
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