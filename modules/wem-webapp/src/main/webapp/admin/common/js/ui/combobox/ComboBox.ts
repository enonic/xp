module api_ui_combobox {

    export class ComboBox<T> extends api_dom.FormInputEl implements api_event.Observable {

        private icon:api_dom.ImgEl;

        private input:ComboBoxInput;

        private dropdownData:api_ui_grid.DataView<Option<T>>;

        private dropdown:api_ui_grid.Grid<Option<T>>;

        private emptyDropdown:api_dom.DivEl;

        private optionFormatter:(row:number, cell:number, value:T, columnDef:any, dataContext:Option<T>) => string;

        private filter:(item:T, args:any) => boolean;

        private multipleSelections:boolean = false;

        private selectedOptionsCtrl:SelectedOptionsCtrl<T>;

        private maxHeight:number = 200;

        private rowHeight;

        /**
         * Indicates if combobox is currently has focus
         * @type {boolean}
         */
        private active:boolean = false;

        private listeners:ComboBoxListener<T>[] = [];

        constructor(name:string, config:ComboBoxConfig<T>) {
            super("div", "ComboBox", "combobox");
            this.getEl().setAttribute("name", name);

            this.optionFormatter = config.optionFormatter;
            if( config.selectedOptionsView != null ) {
                this.selectedOptionsCtrl = new SelectedOptionsCtrl(config.selectedOptionsView,
                                                                   config.maximumOccurrences != null ? config.maximumOccurrences : 0);
                this.multipleSelections = true;
            }
            this.filter = config.filter;
            this.rowHeight = config.rowHeight || 24;

            if (config.iconUrl) {
                this.icon = new api_dom.ImgEl(config.iconUrl, null, "input-icon");
                this.appendChild(this.icon);
            }

            this.input = new ComboBoxInput();
            this.appendChild(this.input);

            this.emptyDropdown = new api_dom.DivEl(null, "empty-options");
            this.emptyDropdown.getEl().setInnerHtml("No matching items");
            this.emptyDropdown.hide();
            this.appendChild(this.emptyDropdown);

            var columns:api_ui_grid.GridColumn<Option<T>>[] = [
                {
                    id: "option",
                    name: "Options",
                    field: "displayValue",
                    formatter: this.optionFormatter}
            ];
            var options:api_ui_grid.GridOptions = {
                width: this.input.getEl().getWidth(),
                height: this.maxHeight,
                hideColumnHeaders: true,
                enableColumnReorder: false,
                fullWidthRows: true,
                forceFitColumns: true,
                rowHeight: this.rowHeight,
                dataIdProperty: "value"
            };

            this.dropdownData = new api_ui_grid.DataView<Option<T>>();
            this.dropdown = new api_ui_grid.Grid<Option<T>>(this.dropdownData, columns, options);
            this.dropdown.addClass("options-container");
            this.dropdown.getEl().setPosition("absolute");
            this.dropdown.hide();

            if (this.filter) {
                this.dropdownData.setFilter(this.filter);
            }

            this.appendChild(this.dropdown);

            this.setupListeners();
        }

        countSelected():number {
            if( this.multipleSelections ) {
                return this.selectedOptionsCtrl.count();
            }
            else {
                throw new Error("Not supported yet");
            }
        }

        getSelectedData():Option<T>[] {
            if( this.multipleSelections ) {
                return this.selectedOptionsCtrl.getOptions();;
            }
            else {
                throw new Error("Not supported yet");
            }
        }

        isDropdownShown():boolean {
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

        setLabel(label:string) {
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

        setOptions(options:Option<T>[]) {
            this.dropdownData.setItems(options);
            if (this.dropdown.isVisible() || this.emptyDropdown.isVisible()) {
                this.showDropdown();
            }
        }

        addOption(option:Option<T>) {
            this.dropdownData.addItem( option );
        }

        setValue(value:string) {
            var item = <Option<T>>this.dropdownData.getItemById(value);
            this.selectOption(item);
        }

        setValues(values:string[]) {
            values.forEach((value:string) => {
                var item = <Option<T>>this.dropdownData.getItemById(value);
                this.selectOption(item);
            });
        }

        getValue():string {
            if( this.multipleSelections ) {
                var values = [];
                this.selectedOptionsCtrl.getOptions().forEach((item:Option<T>) => {
                    values.push(item.value);
                });
                return values.join(';');
            }
            else {
                throw new Error("Not supported yet");
            }
        }

        setInputIconUrl(iconUrl:string) {
            if (!this.icon) {
                this.icon = new api_dom.ImgEl();
                this.icon.addClass("input-icon");
                this.icon.insertBeforeEl(this.input);
            }

            this.icon.getEl().setSrc(iconUrl);
        }

        private setupListeners() {
            this.getEl().addEventListener('click', () => {
                this.setOnBlurListener();
            });

            this.input.addListener({
                onValueChanged: (oldValue:string, newValue:string) => {
                    this.notifyInputValueChanged(oldValue, newValue);
                    this.showDropdown();
                    this.dropdown.setActiveCell(0, 0);
                }
            });

            this.input.getEl().addEventListener('dblclick', (event:any) => {
                if (!this.dropdown.getActiveCell()) {
                    this.dropdown.setActiveCell(0, 0);
                }
                if (!this.isDropdownShown()) {
                    this.showDropdown();
                }
            });

            this.input.getEl().addEventListener('keydown', (event:any) => {
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

                event.preventDefault();
                event.stopPropagation();
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
                    (removedOption:SelectedOption<T>) => {
                        this.handleSelectedOptionRemoved(removedOption);
                    });
            }
        }

        private handleSelectedOptionRemoved(removedSelectedOption:SelectedOption<T>) {
            this.updateDropdownStyles();
            this.input.openForTypingAndFocus();
        }

        private selectRow(index:number) {
            var item = <Option<T>>this.dropdownData.getItem(index);

            this.selectOption(item);
        }

        selectOption(option:Option<T>, silent:boolean = false) {

            var added = this.selectedOptionsCtrl.addOption(option);
            if(!added) {
                return;
            }

            this.updateDropdownStyles();
            this.hideDropdown();

            if (this.maximumOccurrencesReached()) {
                this.input.setMaximumReached();
                this.moveFocuseToNextInput();
            }
            if (!silent) {
                this.notifyOptionSelected(option);
            }
        }

        private moveFocuseToNextInput() {
            // get all inputs from this FormView
            var focusableElements = document.querySelectorAll("input");

            // find index of current input
            var index = -1;
            var inputEl = this.input.getHTMLElement();
            for (var i = 0 ; i < focusableElements.length ; i++) {
                if (inputEl == focusableElements.item(i)) {
                    index = i;
                    break;
                }
            }
            if (index < 0) {
                return;
            }

            // set focus to the next visible input
            for (var i = index + 1 ; i < focusableElements.length ; i++) {
                var nextFocusable = api_dom.Element.fromHtmlElement(<HTMLElement>focusableElements.item(i));
                if (nextFocusable.isVisible()) {
                    nextFocusable.getEl().focuse();
                    return;
                }
            }
        }

        maximumOccurrencesReached():boolean {
            api_util.assert(this.multipleSelections, "No point of calling maximumOccurrencesReached when no multiple selections are enabled");

            return this.selectedOptionsCtrl.maximumOccurrencesReached();
        }

        private updateDropdownStyles() {
            var stylesHash = {};
            this.selectedOptionsCtrl.getOptions().forEach((option:Option<T>) => {
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
            var hideDropdownOnBlur = function (event:Event) {

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
                api_dom.Body.get().getEl().removeEventListener('click', hideDropdownOnBlur);
            }

            // set callback function on document body if combobox wasn't marked as active
            if (!this.active) {
                this.active = true;
                api_dom.Body.get().getEl().addEventListener('click', hideDropdownOnBlur);
            }
        }

        removeSelectedItem(optionToRemove:Option<T>, silent:boolean = false) {
            api_util.assertNotNull(optionToRemove, "optionToRemove cannot be null");

            this.selectedOptionsCtrl.removeOption(optionToRemove, silent);

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

        addListener(listener:ComboBoxListener<T>) {
            this.listeners.push(listener);
        }

        removeListener(listener:ComboBoxListener<T>) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyInputValueChanged(oldValue:string, newValue:string) {
            this.listeners.forEach((listener:ComboBoxListener<T>) => {
                listener.onInputValueChanged(oldValue, newValue, this.dropdown);
            });
        }

        private notifyOptionSelected(item:Option<T>) {
            this.listeners.forEach((listener:ComboBoxListener<T>) => {
                if (listener.onOptionSelected) {
                    listener.onOptionSelected(item);
                }
            });
        }

        addSelectedOptionRemovedListener(listener:{(removed:SelectedOption<T>): void;}) {
            this.selectedOptionsCtrl.addSelectedOptionRemovedListener(listener);
        }

        removeSelectedOptionRemovedListener(listener:{(removed:SelectedOption<T>): void;}) {
            this.selectedOptionsCtrl.removeSelectedOptionRemovedListener(listener);
        }
    }

}