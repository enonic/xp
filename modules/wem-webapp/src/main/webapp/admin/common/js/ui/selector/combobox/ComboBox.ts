module api.ui.selector.combobox {

    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import DropdownHandle = api.ui.selector.DropdownHandle;
    import Viewer = api.ui.Viewer;

    export interface ComboBoxConfig<T> {

        iconUrl?: string;

        rowHeight?: number;

        optionFormatter?: (row: number, cell: number, value: T, columnDef: any, dataContext: Slick.SlickData) => string;

        optionDisplayValueViewer?: Viewer<T>;

        selectedOptionsView: SelectedOptionsView<T>;

        maximumOccurrences?: number;

        filter?: (item: any, args: any) => boolean;

        hideComboBoxWhenMaxReached?:boolean;

        dataIdProperty?:string;

    }

    export class ComboBox<OPTION_DISPLAY_VALUE> extends api.dom.FormInputEl {

        private icon: api.dom.ImgEl;

        private dropdownHandle: DropdownHandle;

        private applySelectionsButton: api.ui.Button;

        private input: ComboBoxOptionFilterInput;

        private multipleSelections: boolean = false;

        private selectedOptionsCtrl: SelectedOptionsCtrl<OPTION_DISPLAY_VALUE>;

        private comboBoxDropdown: ComboBoxDropdown<OPTION_DISPLAY_VALUE>;

        private hideComboBoxWhenMaxReached: boolean;

        private optionSelectedListeners: {(event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>):void}[] = [];

        private valueChangedListeners: {(event: ComboBoxValueChangedEvent<OPTION_DISPLAY_VALUE>):void}[] = [];

        /**
         * Indicates if combobox is currently has focus
         * @type {boolean}
         */
        private active: boolean = false;

        constructor(name: string, config: ComboBoxConfig<OPTION_DISPLAY_VALUE>) {
            super("div", "combobox");
            this.getEl().setAttribute("name", name);

            this.hideComboBoxWhenMaxReached = config.hideComboBoxWhenMaxReached;
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
                optionFormatter: config.optionFormatter,
                optionDisplayValueViewer: config.optionDisplayValueViewer,
                filter: config.filter,
                rowHeight: config.rowHeight,
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
        }

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[]) {
            this.comboBoxDropdown.setOptions(options);
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

        selectOption(option: Option<OPTION_DISPLAY_VALUE>, silent: boolean = false) {

            var added = this.selectedOptionsCtrl.addOption(option);
            if (!added) {
                return;
            }

            this.comboBoxDropdown.markSelections(this.selectedOptionsCtrl.getOptions());
            this.hideDropdown();
            this.addClass("followed-by-options");

            if (this.maximumOccurrencesReached()) {
                this.input.setMaximumReached();
                api.dom.FormEl.moveFocuseToNextFocusable(this.input);
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
            var allOptions: Option<OPTION_DISPLAY_VALUE>[] = this.selectedOptionsCtrl.getOptions();
            allOptions.forEach((option: Option<OPTION_DISPLAY_VALUE>) => {
                this.selectedOptionsCtrl.removeOption(option, true);
            });

            this.comboBoxDropdown.markSelections(this.selectedOptionsCtrl.getOptions(), ignoreEmpty);

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

        maximumOccurrencesReached(): boolean {
            api.util.assert(this.multipleSelections,
                "No point of calling maximumOccurrencesReached when no multiple selections are enabled");

            return this.selectedOptionsCtrl.maximumOccurrencesReached();
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

            this.comboBoxDropdown.onRowSelection((event: DropdownGridRowSelectedEvent) => {
                this.selectRow(event.getRow());
            });

            this.dropdownHandle.onClicked((event: MouseEvent) => {

                this.comboBoxDropdown.navigateToFirstRowIfNotActive();

                if (!this.maximumOccurrencesReached()) {
                    if (this.isDropdownShown()) {
                        this.hideDropdown();
                        this.giveFocus();
                    } else {
                        this.showDropdown();
                        this.giveFocus();
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

                this.notifyValueChanged(event.getOldValue(), event.getNewValue());
                this.showDropdown();
                this.comboBoxDropdown.nagivateToFirstRow();

            });

            this.input.onDblClicked((event: MouseEvent) => {

                this.comboBoxDropdown.navigateToFirstRowIfNotActive();

                if (!this.isDropdownShown()) {
                    this.showDropdown();
                }
            });

            this.input.onKeyDown((event: KeyboardEvent) => {

                if (event.which == 9) { // tab
                    this.hideDropdown();
                    return;
                }
                else if (event.which == 16 || event.which == 17 || event.which == 18) {  // shift or ctrl or alt
                    return;
                }

                this.comboBoxDropdown.navigateToFirstRowIfNotActive();

                if (!this.isDropdownShown()) {
                    this.showDropdown();
                    return;
                }

                if (event.which == 38) { // up
                    this.comboBoxDropdown.navigateToPreviousRow();
                }
                else if (event.which == 40) { // down
                    this.comboBoxDropdown.navigateToNextRow();
                }
                else if (event.which == 13) { // enter
                    this.selectRow(this.comboBoxDropdown.getActiveRow());
                    this.input.getEl().setValue("");
                }
                else if (event.which == 27) { // esc
                    this.hideDropdown();
                }

                this.input.getHTMLElement().focus();
            });

            if (this.multipleSelections) {
                this.selectedOptionsCtrl.addSelectedOptionRemovedListener(
                    (removedOption: SelectedOption<OPTION_DISPLAY_VALUE>) => {
                        this.handleSelectedOptionRemoved(removedOption);
                    });
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

            if (this.selectedOptionsCtrl.getOptions().length === event.getRows().length) {
                var currentOptions = this.selectedOptionsCtrl.getOptions().map((x) => { return x.value; }).sort();
                var eventOptions = [];
                event.getRows().forEach((row: number) => {
                    eventOptions.push(this.comboBoxDropdown.getGrid().getOptionByRow(row));
                });
                eventOptions = eventOptions.map((x) => { return x.value; }).sort();
                if (currentOptions.join() === eventOptions.join()) {
                    this.applySelectionsButton.hide();
                    return;
                }
            }

            this.applySelectionsButton.show();
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
            }

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

        onValueChanged(listener: (event: ComboBoxValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: ComboBoxValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.valueChangedListeners.filter((currentListener: (event: ComboBoxValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                return listener != currentListener;
            })
        }

        private notifyValueChanged(oldValue: string, newValue: string) {
            var event = new ComboBoxValueChangedEvent<OPTION_DISPLAY_VALUE>(oldValue, newValue,
                this.comboBoxDropdown.getGrid().getElement());
            this.valueChangedListeners.forEach((listener: (event: ComboBoxValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                listener(event);
            });
        }

        addSelectedOptionRemovedListener(listener: {(removed: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
            this.selectedOptionsCtrl.addSelectedOptionRemovedListener(listener);
        }

        removeSelectedOptionRemovedListener(listener: {(removed: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
            this.selectedOptionsCtrl.removeSelectedOptionRemovedListener(listener);
        }
    }

}