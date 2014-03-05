module api.ui.selector.combobox {

    export class ComboBox<T> extends api.dom.FormInputEl {

        private icon: api.dom.ImgEl;

        private input: ComboBoxInput;

        private dropdownData: api.ui.grid.DataView<Option<T>>;

        private dropdown: api.ui.grid.Grid<Option<T>>;

        private emptyDropdown: api.dom.DivEl;

        private optionFormatter: (row: number, cell: number, value: T, columnDef: any, dataContext: Option<T>) => string;

        private filter: (item: api.ui.selector.combobox.Option<T>, args: any) => boolean;

        private multipleSelections: boolean = false;

        private selectedOptionsCtrl: SelectedOptionsCtrl<T>;

        private maxHeight: number = 200;

        private rowHeight;

        private hideComboBoxWhenMaxReached: boolean;

        private optionSelectedListeners: {(event: ComboBoxOptionSelectedEvent<T>):void}[] = [];

        private valueChangedListeners: {(event: ComboBoxValueChangedEvent<T>):void}[] = [];

        /**
         * Indicates if combobox is currently has focus
         * @type {boolean}
         */
        private active: boolean = false;

        constructor(name: string, config: ComboBoxConfig<T>) {
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

            this.input = new ComboBoxInput();
            this.appendChild(this.input);

            this.emptyDropdown = new api.dom.DivEl("empty-options");
            this.emptyDropdown.getEl().setInnerHtml("No matching items");
            this.emptyDropdown.hide();
            this.appendChild(this.emptyDropdown);

            var columns: api.ui.grid.GridColumn<Option<T>>[] = [
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

            this.dropdownData = new api.ui.grid.DataView<Option<T>>();
            this.dropdown = new api.ui.grid.Grid<Option<T>>(this.dropdownData, columns, options);
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

        countSelected(): number {
            if (this.multipleSelections) {
                return this.selectedOptionsCtrl.count();
            }
            else {
                throw new Error("Not supported yet");
            }
        }

        getSelectedData(): Option<T>[] {
            if (this.multipleSelections) {
                return this.selectedOptionsCtrl.getOptions();
                ;
            }
            else {
                throw new Error("Not supported yet");
            }
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
            this.emptyDropdown.hide();
            this.dropdown.hide();
        }

        setOptions(options: Option<T>[], optionIdProperty?: string) {
            this.dropdownData.setItems(options, optionIdProperty);
            if (this.dropdown.isVisible() || this.emptyDropdown.isVisible()) {
                this.showDropdown();
            }
        }

        getValues(): Option<T>[] {
            return this.dropdownData.getItems();
        }

        addOption(option: Option<T>) {
            this.dropdownData.addItem(option);
        }

        setValue(value: string) {
            var item = <Option<T>>this.dropdownData.getItemById(value);
            this.selectOption(item);
        }

        setValues(values: string[]) {
            values.forEach((value: string) => {
                var item = <Option<T>>this.dropdownData.getItemById(value);
                this.selectOption(item);
            });
        }

        getValue(): string {
            if (this.multipleSelections) {
                var values = [];
                this.selectedOptionsCtrl.getOptions().forEach((item: Option<T>) => {
                    values.push(item.value);
                });
                return values.join(';');
            }
            else {
                throw new Error("Not supported yet");
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
            this.getEl().addEventListener('click', () => {
                this.setOnBlurListener();
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
                    (removedOption: SelectedOption<T>) => {
                        this.handleSelectedOptionRemoved(removedOption);
                    });
            }
        }

        private handleSelectedOptionRemoved(removedSelectedOption: SelectedOption<T>) {
            this.updateDropdownStyles();
            this.input.openForTypingAndFocus();

            if (this.hideComboBoxWhenMaxReached) {
                this.show();
            }

            if (this.countSelected() == 0) {
                this.removeClass("followed-by-options");
            }
        }

        private selectRow(index: number) {
            var item = <Option<T>>this.dropdownData.getItem(index);

            this.selectOption(item);
        }

        selectOption(option: Option<T>, silent: boolean = false) {

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

        maximumOccurrencesReached(): boolean {
            api.util.assert(this.multipleSelections,
                "No point of calling maximumOccurrencesReached when no multiple selections are enabled");

            return this.selectedOptionsCtrl.maximumOccurrencesReached();
        }

        private updateDropdownStyles() {
            var stylesHash: Slick.CellCssStylesHash = {};
            this.selectedOptionsCtrl.getOptions().forEach((option: Option<T>) => {
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

        removeSelectedItem(optionToRemove: Option<T>, silent: boolean = false) {
            api.util.assertNotNull(optionToRemove, "optionToRemove cannot be null");

            this.selectedOptionsCtrl.removeOption(optionToRemove, silent);

            this.updateDropdownStyles();

            this.input.openForTypingAndFocus();
        }

        clearSelection() {
            var allOptions: Option<T>[] = this.selectedOptionsCtrl.getOptions();
            allOptions.forEach((option: Option<T>) => {
                this.selectedOptionsCtrl.removeOption(option, true);
            });

            this.updateDropdownStyles();

            this.input.openForTypingAndFocus();
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

        onOptionSelected(listener: (event: ComboBoxOptionSelectedEvent<T>)=>void) {
            this.optionSelectedListeners.push(listener);
        }

        unOptionSelected(listener: (event: ComboBoxOptionSelectedEvent<T>)=>void) {
            this.optionSelectedListeners.filter((currentListener: (event: ComboBoxOptionSelectedEvent<T>)=>void) => {
                return listener != currentListener;
            });
        }

        onValueChanged(listener: (event: ComboBoxValueChangedEvent<T>)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: ComboBoxValueChangedEvent<T>)=>void) {
            this.valueChangedListeners.filter((currentListener: (event: ComboBoxValueChangedEvent<T>)=>void) => {
                return listener != currentListener;
            })
        }

        private notifyValueChanged(oldValue: string, newValue: string) {
            this.valueChangedListeners.forEach((listener: (event: ComboBoxValueChangedEvent<T>)=>void) => {
                listener.call(this, new ComboBoxValueChangedEvent(oldValue, newValue, this.dropdown));
            });
        }

        private notifyOptionSelected(item: Option<T>) {
            this.optionSelectedListeners.forEach((listener: (event: ComboBoxOptionSelectedEvent<T>)=>void) => {
                listener.call(this, new ComboBoxOptionSelectedEvent<T>(item));
            });
        }

        addSelectedOptionRemovedListener(listener: {(removed: SelectedOption<T>): void;}) {
            this.selectedOptionsCtrl.addSelectedOptionRemovedListener(listener);
        }

        removeSelectedOptionRemovedListener(listener: {(removed: SelectedOption<T>): void;}) {
            this.selectedOptionsCtrl.removeSelectedOptionRemovedListener(listener);
        }
    }

}