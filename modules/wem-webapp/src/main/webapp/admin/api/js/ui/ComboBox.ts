module api_ui {

    export interface OptionData extends Slick.SlickData {

        id:string;

        value:any;

    }

    export interface ComboBoxConfig {

        iconUrl?: string;

        rowHeight?: number;

        optionFormatter?: (row: number, cell: number, value: any, columnDef: any, dataContext: Slick.SlickData) => string;

        selectedOptionFormatter?: (value:any) => string;

        maximumOccurrences?: number;

        filter?: (item: any, args: any) => boolean;

    }

    export class ComboBox extends api_dom.FormInputEl implements api_event.Observable {

        private icon:api_dom.ImgEl;

        private input:TextInput;

        private dropdown:api_grid.Grid;

        private emptyDropdown: api_dom.DivEl;

        private selectedOptions: api_dom.DivEl;

        private initData: any;

        private optionFormatter: (row: number, cell: number, value: any, columnDef: any, dataContext: Slick.SlickData) => string;

        private selectedOptionFormatter: (value:any) => string;

        private maximumOccurrences:number;

        private filter: (item: any, args: any) => boolean;

        private selectedData: OptionData[] = [];

        private maxHeight: number = 200;

        private rowHeight;

        /**
         * Indicates if combobox is currently has focus
         * @type {boolean}
         */
        private active: boolean = false;

        private listeners: ComboBoxListener[] = [];

        constructor(name: string, data:any, config:ComboBoxConfig) {
            super("div", null, "combobox");
            this.getEl().setAttribute("name", name);

            this.initData = data;
            this.optionFormatter = config.optionFormatter;
            this.selectedOptionFormatter = config.selectedOptionFormatter;
            this.maximumOccurrences = config.maximumOccurrences || 1;
            this.filter = config.filter;
            this.rowHeight = config.rowHeight || 50;

            if (config.iconUrl) {
                this.icon = new api_dom.ImgEl(config.iconUrl, null, "input-icon");
                this.appendChild(this.icon);
            }

            this.input = new TextInput();
            this.appendChild(this.input);

            this.emptyDropdown = new api_dom.DivEl(null, "empty-options");
            this.emptyDropdown.getEl().setInnerHtml("No matching items");
            this.emptyDropdown.hide();
            this.appendChild(this.emptyDropdown);

            this.selectedOptions = new api_dom.DivEl(null, "selected-options");
            this.selectedOptions.hide();
            this.appendChild(this.selectedOptions);

            var data = this.initData;
            var columns = [
                {id: "option", name: "Options", field: "value", formatter: this.optionFormatter}
            ];
            var options = {
                width: this.input.getEl().getWidth(),
                height: this.maxHeight,
                hideColumnHeaders: true,
                enableColumnReorder: false,
                fullWidthRows: true,
                forceFitColumns: true,
                rowHeight: this.rowHeight
            };

            this.dropdown = new api_grid.Grid(data, columns, options);
            this.dropdown.addClass("options-container");
            this.dropdown.getEl().setPosition("absolute");
            this.dropdown.hide();

            if (this.filter) {
                this.dropdown.setFilter(this.filter);
            }

            this.appendChild(this.dropdown);

            this.setupListeners();
        }

        isDropdownShown(): boolean {
            return this.emptyDropdown.isVisible() || this.dropdown.isVisible();
        }

        showDropdown() {
            var rowsLength = this.dropdown.getDataView().getLength();
            if (rowsLength > 0) {
                this.adjustDropdownSize();
                this.updateDropdownStyles();

                this.emptyDropdown.hide();
                this.dropdown.show();
            } else {
                this.adjustEmptyDropdownSize();

                this.dropdown.hide();
                this.emptyDropdown.show();
            }
        }

        hideDropdown() {
            this.emptyDropdown.hide();
            this.dropdown.hide();
        }

        addOption(id:string, value:string) {
            this.dropdown.addItem({id: id, value: value});
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
                if (!this.dropdown.getActiveCell()) {
                    this.dropdown.setActiveCell(0, 0);
                }
                if (!this.isDropdownShown()) {
                    this.showDropdown();
                    return;
                }

                var rowsLength = this.dropdown.getDataLength();
                var activeCell = this.dropdown.getActiveCell();

                if (event.which == 38) { // up
                    this.dropdown.setActiveCell((activeCell.row || rowsLength) - 1, 0);
                } else if (event.which == 40) { // down
                    this.dropdown.setActiveCell((activeCell.row + 1) % rowsLength, 0);
                } else if (event.which == 13) { // enter
                    this.selectRow(activeCell.row);
                }

                this.input.getHTMLElement().focus();
            });

            this.dropdown.subscribeOnClick((e, args) => {
                this.selectRow(args.row);

                event.preventDefault();
                event.stopPropagation();
                return false;
            });

            this.dropdown.subscribeOnRowsChanged((e, args) => {
                this.updateDropdownStyles();
            });

            this.dropdown.subscribeOnRowCountChanged((e, args) => {
                this.updateDropdownStyles();
            });
        }

        private selectRow(index:number) {
            if (!this.canRowBeSelected(index)) {
                return;
            }

            var item = <OptionData>this.dropdown.getDataItem(index);

            this.selectedData.push(item);
            this.showSelectedItem(item);

            this.updateDropdownStyles();
            this.hideDropdown();
            this.input.setValue("");

            if (this.selectedData.length == this.maximumOccurrences) {
                this.input.getEl().setDisabled(true);
            }
        }

        private canRowBeSelected(index:number):boolean {
            if (this.selectedData.length == this.maximumOccurrences) {
                return false;
            }

            var item = <OptionData>this.dropdown.getDataItem(index);

            for (var i = 0 ; i < this.selectedData.length ; i++) {
                if (this.selectedData[i].id == item.id) {
                    return false;
                }
            }

            return true;
        }

        private updateDropdownStyles() {
            var stylesHash = {};
            var dataView = this.dropdown.getDataView();
            this.selectedData.forEach((item: OptionData) => {
                var row = dataView.getRowById(item.id);
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
                for ( var element = event.target ; element ; element = (<any>element).parentNode ) {
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

        private showSelectedItem(item:OptionData) {
            var option = new api_dom.DivEl(null, 'selected-option');
            var removeButton = new api_dom.AEl(null, "remove");
            var optionValue = new api_dom.DivEl(null, 'option-value');

            option.appendChild(removeButton);
            option.appendChild(optionValue);
            optionValue.getEl().setInnerHtml(this.selectedOptionFormatter ? this.selectedOptionFormatter(item.value) : item.value.toString());

            this.selectedOptions.appendChild(option);
            this.selectedOptions.show();

            removeButton.getEl().addEventListener('click', (event:Event) => {
                option.remove();
                this.removeSelectedItem(item);

                event.stopPropagation();
                event.preventDefault();
                return false;
            });
        }

        private removeSelectedItem(item:OptionData) {
            this.selectedData = this.selectedData.filter((element:OptionData, index:number) => {
                return element != item;
            });

            this.updateDropdownStyles();

            this.input.getEl().setDisabled(false);
            this.input.getHTMLElement().focus();
        }

        private adjustDropdownSize() {
            var dropdownEl = this.dropdown.getEl();
            var inputEl = this.input.getEl();

            dropdownEl.setTopPx(inputEl.getHeight() - inputEl.getBorderBottomWidth());

            if (dropdownEl.getWidth() != inputEl.getWidth()) {
                dropdownEl.setWidth(inputEl.getWidth() + "px");
            }

            var rowsHeight =  this.dropdown.getDataView().getLength() * this.rowHeight;
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

        addListener(listener:ComboBoxListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:ComboBoxListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyInputValueChanged(oldValue:string, newValue:string) {
            this.listeners.forEach((listener:ComboBoxListener) => {
                listener.onInputValueChanged(oldValue, newValue, this.dropdown);
            });
        }
    }
}