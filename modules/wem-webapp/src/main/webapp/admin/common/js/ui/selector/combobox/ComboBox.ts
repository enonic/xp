module api.ui.selector.combobox {

    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class ComboBox<OPTION_DISPLAY_VALUE> extends api.dom.FormInputEl {

        private icon: api.dom.ImgEl;

        private arrow: api.dom.DivEl;

        private input: ComboBoxOptionFilterInput;

        private dropdownData: api.ui.grid.DataView<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>;

        private dropdown: api.ui.grid.Grid<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>;

        private emptyDropdown: api.dom.DivEl;

        private optionFormatter: (row: number, cell: number, value: OPTION_DISPLAY_VALUE, columnDef: any, dataContext: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) => string;

        private filter: (item: api.ui.selector.Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        private multipleSelections: boolean = false;

        private selectedOptionsCtrl: SelectedOptionsCtrl<OPTION_DISPLAY_VALUE>;

        private maxHeight: number = 200;

        private rowHeight;

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
            this.optionFormatter = config.optionFormatter;
            if (config.selectedOptionsView != null) {
                this.selectedOptionsCtrl = new SelectedOptionsCtrl(config.selectedOptionsView,
                    config.maximumOccurrences != null ? config.maximumOccurrences : 0);
                this.multipleSelections = true;
            }
            this.filter = config.filter;
            this.rowHeight = config.rowHeight || 24;

            if (config.iconUrl) {
                this.icon = new api.dom.ImgEl(config.iconUrl, "input-icon");
                this.appendChild(this.icon);
            }

            this.input = new ComboBoxOptionFilterInput();
            this.appendChild(this.input);

            this.arrow = new api.dom.DivEl("dropdown-arrow");
            this.appendChild(this.arrow);

            this.emptyDropdown = new api.dom.DivEl("empty-options");
            this.emptyDropdown.getEl().setInnerHtml("No matching items");
            this.emptyDropdown.hide();
            this.appendChild(this.emptyDropdown);

            var columns: api.ui.grid.GridColumn<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>[] = [
                {
                    id: "option",
                    name: "Options",
                    field: "displayValue",
                    formatter: this.optionFormatter}
            ];
            var options: api.ui.grid.GridOptions = {
                width: this.input.getEl().getWidth(),
                height: this.maxHeight,
                hideColumnHeaders: true,
                enableColumnReorder: false,
                fullWidthRows: true,
                forceFitColumns: true,
                rowHeight: this.rowHeight,
                dataIdProperty: config.dataIdProperty ? config.dataIdProperty : "value"
            };

            this.dropdownData = new api.ui.grid.DataView<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>();
            this.dropdown = new api.ui.grid.Grid<api.ui.selector.Option<OPTION_DISPLAY_VALUE>>(this.dropdownData, columns, options);
            this.dropdown.addClass("options-container");
            this.dropdown.getEl().setPosition("absolute");
            this.dropdown.hide();

            if (this.filter) {
                this.dropdownData.setFilter(this.filter);
            }

            this.appendChild(this.dropdown);

            this.setupListeners();
        }

        giveFocus(): boolean {
            return this.input.giveFocus();
        }

        isDropdownShown(): boolean {
            return this.emptyDropdown.isVisible() || this.dropdown.isVisible();
        }

        showDropdown() {
            var rowsLength = this.dropdownData.getLength();
            if (rowsLength > 0) {
                this.emptyDropdown.hide();
                this.dropdown.show();
                this.adjustDropdownSize();
                this.updateDropdownStyles();
            } else {
                this.dropdown.hide();
                this.adjustEmptyDropdownSize();
                this.emptyDropdown.getEl().setInnerHtml("No matching items");
                this.emptyDropdown.show();
            }
            this.arrow.hide();

            this.dropdown.render();
        }

        setLabel(label: string) {
            if (this.dropdown.isVisible() || this.emptyDropdown.isVisible()) {
                this.dropdown.hide();
                this.adjustEmptyDropdownSize();
                this.emptyDropdown.getEl().setInnerHtml(label);
                this.emptyDropdown.show();
            }
        }

        hideDropdown() {
            this.arrow.show();
            this.emptyDropdown.hide();
            this.dropdown.hide();
        }

        setOptions(options: api.ui.selector.Option<OPTION_DISPLAY_VALUE>[], optionIdProperty?: string) {
            this.dropdownData.setItems(options, optionIdProperty);
            if (this.dropdown.isVisible() || this.emptyDropdown.isVisible()) {
                this.showDropdown();
            }
        }

        addOption(option: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) {
            this.dropdownData.addItem(option);
        }

        getOptions(): api.ui.selector.Option<OPTION_DISPLAY_VALUE>[] {
            return this.dropdownData.getItems();
        }

        setValue(value: string) {
            var item = <api.ui.selector.Option<OPTION_DISPLAY_VALUE>>this.dropdownData.getItemById(value);
            this.selectOption(item);
        }

        setValues(values: string[]) {
            values.forEach((value: string) => {
                var item = <api.ui.selector.Option<OPTION_DISPLAY_VALUE>>this.dropdownData.getItemById(value);
                this.selectOption(item);
            });
        }

        selectRow(index: number) {
            var item = <api.ui.selector.Option<OPTION_DISPLAY_VALUE>>this.dropdownData.getItem(index);

            this.selectOption(item);
        }

        selectOption(option: api.ui.selector.Option<OPTION_DISPLAY_VALUE>, silent: boolean = false) {

            var added = this.selectedOptionsCtrl.addOption(option);
            if (!added) {
                return;
            }

            this.updateDropdownStyles();
            this.hideDropdown();
            this.addClass("followed-by-options");

            if (this.maximumOccurrencesReached()) {
                this.input.setMaximumReached();
                this.moveFocuseToNextInput();
            }
            if (!silent) {
                this.notifyOptionSelected(option);
            }
            if (this.maximumOccurrencesReached() && this.hideComboBoxWhenMaxReached) {
                this.hide();
            }
        }

        removeSelectedOption(optionToRemove: api.ui.selector.Option<OPTION_DISPLAY_VALUE>, silent: boolean = false) {
            api.util.assertNotNull(optionToRemove, "optionToRemove cannot be null");

            this.selectedOptionsCtrl.removeOption(optionToRemove, silent);

            this.updateDropdownStyles();

            this.input.openForTypingAndFocus();
        }

        clearSelection() {
            var allOptions: api.ui.selector.Option<OPTION_DISPLAY_VALUE>[] = this.selectedOptionsCtrl.getOptions();
            allOptions.forEach((option: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) => {
                this.selectedOptionsCtrl.removeOption(option, true);
            });

            this.updateDropdownStyles();

            this.input.openForTypingAndFocus();
        }

        getSelectedOptions(): api.ui.selector.Option<OPTION_DISPLAY_VALUE>[] {
            if (this.multipleSelections) {
                return this.selectedOptionsCtrl.getOptions();
                ;
            }
            else {
                throw new Error("Not supported yet");
            }
        }

        getValue(): string {
            if (this.multipleSelections) {
                var values = [];
                this.selectedOptionsCtrl.getOptions().forEach((item: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) => {
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
            this.getEl().addEventListener('click', () => {
                this.setOnBlurListener();
            });

            this.arrow.getEl().addEventListener('click', (event: any) => {
                if (!this.dropdown.getActiveCell()) {
                    this.dropdown.setActiveCell(0, 0);
                }
                if (!this.isDropdownShown()) {
                    this.showDropdown();
                    this.giveFocus();
                }
            });

            this.input.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.notifyValueChanged(event.getOldValue(), event.getNewValue());
                this.showDropdown();
                this.dropdown.setActiveCell(0, 0);
            });

            this.input.getEl().addEventListener('dblclick', (event: any) => {
                if (!this.dropdown.getActiveCell()) {
                    this.dropdown.setActiveCell(0, 0);
                }
                if (!this.isDropdownShown()) {
                    this.showDropdown();
                }
            });

            this.input.getEl().addEventListener('keydown', (event: any) => {
                if (event.which == 9) { // tab
                    this.hideDropdown();
                    return;
                } else if (event.which == 16 || event.which == 17 || event.which == 18) {  // shift or ctrl or alt
                    return;
                }

                if (!this.dropdown.getActiveCell()) {
                    this.dropdown.setActiveCell(0, 0);
                }
                if (!this.isDropdownShown()) {
                    this.showDropdown();
                    return;
                }

                var rowsLength = this.dropdownData.getLength();
                var activeCell = this.dropdown.getActiveCell();

                if (event.which == 38) { // up
                    this.dropdown.setActiveCell((activeCell.row || rowsLength) - 1, 0);
                } else if (event.which == 40) { // down
                    this.dropdown.setActiveCell((activeCell.row + 1) % rowsLength, 0);
                } else if (event.which == 13) { // enter
                    this.selectRow(activeCell.row);
                    this.input.getEl().setValue("");
                } else if (event.which == 27) { // esc
                    this.hideDropdown();
                }

                this.input.getHTMLElement().focus();
            });

            this.dropdown.subscribeOnClick((e, args) => {
                this.selectRow(args.row);

                e.preventDefault();
                e.stopPropagation();
                return false;
            });

            this.dropdownData.subscribeOnRowsChanged((e, args) => {
                this.updateDropdownStyles();
            });

            this.dropdownData.subscribeOnRowCountChanged((e, args) => {
                this.updateDropdownStyles();
            });

            if (this.multipleSelections) {
                this.selectedOptionsCtrl.addSelectedOptionRemovedListener(
                    (removedOption: SelectedOption<OPTION_DISPLAY_VALUE>) => {
                        this.handleSelectedOptionRemoved(removedOption);
                    });
            }
        }

        private handleSelectedOptionRemoved(removedSelectedOption: SelectedOption<OPTION_DISPLAY_VALUE>) {
            this.updateDropdownStyles();
            this.input.openForTypingAndFocus();

            if (this.hideComboBoxWhenMaxReached) {
                this.show();
            }

            if (this.countSelectedOptions() == 0) {
                this.removeClass("followed-by-options");
            }
        }

        private moveFocuseToNextInput() {
            // get all inputs from this FormView
            var focusableElements = document.querySelectorAll("input");

            // find index of current input
            var index = -1;
            var inputEl = this.input.getHTMLElement();
            for (var i = 0; i < focusableElements.length; i++) {
                if (inputEl == focusableElements.item(i)) {
                    index = i;
                    break;
                }
            }
            if (index < 0) {
                return;
            }

            // set focus to the next visible input
            for (var i = index + 1; i < focusableElements.length; i++) {
                var nextFocusable = api.dom.Element.fromHtmlElement(<HTMLElement>focusableElements.item(i));
                if (nextFocusable.isVisible()) {
                    nextFocusable.getEl().focuse();
                    return;
                }
            }
        }

        private updateDropdownStyles() {
            var stylesHash: Slick.CellCssStylesHash = {};
            this.selectedOptionsCtrl.getOptions().forEach((option: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) => {
                var row = this.dropdownData.getRowById(option.value);
                stylesHash[row] = {option: "selected"};
            });
            this.dropdown.setCellCssStyles("selected", stylesHash);
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
                api.dom.Body.get().getEl().addEventListener('click', hideDropdownOnBlur);
            }
        }

        private adjustDropdownSize() {
            var dropdownEl = this.dropdown.getEl();
            var inputEl = this.input.getEl();

            dropdownEl.setTopPx(inputEl.getHeight() - inputEl.getBorderBottomWidth());

            if (dropdownEl.getWidth() != inputEl.getWidth()) {
                dropdownEl.setWidth(inputEl.getWidth() + "px");
            }

            var rowsHeight = this.dropdownData.getLength() * this.rowHeight;
            if (rowsHeight < this.maxHeight) {
                var borderWidth = dropdownEl.getBorderTopWidth() + dropdownEl.getBorderBottomWidth();
                dropdownEl.setHeight(rowsHeight + borderWidth + "px");
                this.dropdown.setOptions({autoHeight: true});
            } else if (dropdownEl.getHeight() < this.maxHeight) {
                dropdownEl.setHeight(this.maxHeight + "px");
                this.dropdown.setOptions({autoHeight: false});
            }

            this.dropdown.resizeCanvas();
        }

        private adjustEmptyDropdownSize() {
            this.emptyDropdown.getEl().setTopPx(this.input.getEl().getHeight() - this.input.getEl().getBorderBottomWidth());
        }

        onOptionSelected(listener: (event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.optionSelectedListeners.push(listener);
        }

        unOptionSelected(listener: (event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.optionSelectedListeners.filter((currentListener: (event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                return listener != currentListener;
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
            this.valueChangedListeners.forEach((listener: (event: ComboBoxValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                listener.call(this, new ComboBoxValueChangedEvent(oldValue, newValue, this.dropdown));
            });
        }

        private notifyOptionSelected(item: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) {
            this.optionSelectedListeners.forEach((listener: (event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                listener.call(this, new OptionSelectedEvent<OPTION_DISPLAY_VALUE>(item));
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