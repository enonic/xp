module api_ui {

    export interface OptionData extends Slick.SlickData {

        option:any;

    }

    export interface ComboBoxConfig {

        optionFormatter?: (row: number, cell: number, value: any, columnDef: any, dataContext: Slick.SlickData) => string;

        selectedOptionFormatter?: (value:any) => string;

    }

    export class ComboBox extends api_dom.FormInputEl implements api_event.Observable {

        private icon:api_dom.ImgEl;

        private input:TextInput;

        private dropdown:api_grid.Grid;

        private emptyDropdown: api_dom.DivEl;

        private initData: any;

        private optionFormatter: (row: number, cell: number, value: any, columnDef: any, dataContext: Slick.SlickData) => string;

        private selectedOptionFormatter: (value:any) => string;

        private selectionModel:any;

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

            this.icon = new api_dom.ImgEl(null, null, "input-icon");
            this.input = new TextInput();
            this.emptyDropdown = new api_dom.DivEl(null, "empty-options");
            this.emptyDropdown.getEl().setInnerHtml("No matching items");
            this.emptyDropdown.hide();

            this.appendChild(this.icon);
            this.appendChild(this.input);
            this.appendChild(this.emptyDropdown);


        }

        afterRender() {
            this.initDropdown();
            this.setupListeners();
        }

        isDropdownShown(): boolean {
            return this.emptyDropdown.isVisible() || this.dropdown.isVisible();
        }

        showDropdown() {
            var rowsLength = this.dropdown.getDataView().getLength();
            if (rowsLength > 0) {
                this.emptyDropdown.hide();
                this.dropdown.show();
            } else {
                this.dropdown.hide();
                this.emptyDropdown.show();
            }
        }

        hideDropdown() {
            this.emptyDropdown.hide();
            this.dropdown.hide();
        }

        private initDropdown() {
            var data = this.initData;
            var columns = [
                {id: "option", name: "Options", field: "option", formatter: this.optionFormatter}
            ];
            var options = {
                width: this.input.getEl().getWidth(),
                height: 200,
                hideColumnHeaders: true,
                enableColumnReorder: false,
                fullWidthRows: true,
                forceFitColumns: true,
                rowHeight: 50
            };

            this.dropdown = new api_grid.Grid(data, columns, options);
            this.dropdown.addClass("options-container");
            this.dropdown.getEl().setPosition("absolute");
            this.dropdown.hide();

            this.appendChild(this.dropdown);

            this.selectionModel = new (<any>Slick).RowSelectionModel({selectActiveRow: false});

            this.dropdown.setSelectionModel(this.selectionModel);
            this.dropdown.resizeCanvas();
        }

        private setupListeners() {
            this.input.addListener({
                onValueChanged: (oldValue:string, newValue:string) => {
                    this.notifyInputValueChanged(oldValue, newValue);
                    newValue ? this.showDropdown() : this.hideDropdown();
                }
            });

            this.getEl().addEventListener('click', () => {
                this.setOnBlurListener();
            });

            this.input.getEl().addEventListener('keydown', (event:any) => {
                if (!this.dropdown.getActiveCell()) {
                    this.dropdown.setActiveCell(0, 0);
                }
                if (!this.isDropdownShown()) {
                    this.showDropdown();
                    return;
                }
                if (event.which == 38) {
                    this.dropdown.navigateUp();
                } else if (event.which == 40) {
                    this.dropdown.navigateDown();
                } else if (event.which == 13) {
                    var activeRow = this.dropdown.getActiveCell().row;
                    this.selectionModel.setSelectedRows([activeRow]);
                }
                this.input.getHTMLElement().focus();
            });

            this.dropdown.subscribeOnClick((e, args) => {
                if (!this.getValue()) {
                    this.selectionModel.setSelectedRows([args.row]);
                }
            });

            this.dropdown.subscribeOnSelectedRowsChanged((e, args) => {
                if (args.rows.length > 0) {
                    var item = this.dropdown.getDataView().getItem(args.rows[0]);
                    this.setValue(item.id);
                    this.showSelectedItem(item.option);
                }
            });
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

        private showSelectedItem(value:any) {
            var selectedOption = new api_dom.DivEl(null, 'selected-option');
            var removeButton = new api_dom.AEl(null, "remove");
            var optionValue = new api_dom.DivEl(null, 'option-value');

            selectedOption.appendChild(removeButton);
            selectedOption.appendChild(optionValue);
            optionValue.getEl().setInnerHtml(this.selectedOptionFormatter ? this.selectedOptionFormatter(value) : value);

            this.appendChild(selectedOption);

            removeButton.getEl().addEventListener('click', (event:Event) => {
                this.setValue("");
                selectedOption.remove();
                this.input.getEl().setDisabled(false);
                this.input.getHTMLElement().focus();

                event.preventDefault();
                return false;
            });

            this.hideDropdown();
            this.input.getEl().setDisabled(true);
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