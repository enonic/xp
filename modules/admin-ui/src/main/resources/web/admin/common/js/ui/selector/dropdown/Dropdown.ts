module api.ui.selector.dropdown {

    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import OptionFilterInputValueChangedEvent = api.ui.selector.OptionFilterInputValueChangedEvent;
    import DropdownHandle = api.ui.selector.DropdownHandle;
    import Viewer = api.ui.Viewer;
    import DefaultOptionDisplayValueViewer = api.ui.selector.DefaultOptionDisplayValueViewer;

    export interface DropdownConfig<OPTION_DISPLAY_VALUE> {

        iconUrl?: string;

        optionDisplayValueViewer?: Viewer<OPTION_DISPLAY_VALUE>;

        filter?: (item: Option<OPTION_DISPLAY_VALUE>, args: any) => boolean;

        dataIdProperty?:string;

    }

    export class Dropdown<OPTION_DISPLAY_VALUE> extends api.dom.FormInputEl {

        private icon: api.dom.ImgEl;

        private dropdownHandle: DropdownHandle;

        private input: DropdownOptionFilterInput;

        private dropdownList: DropdownList<OPTION_DISPLAY_VALUE>;

        private optionDisplayValueViewer: Viewer<OPTION_DISPLAY_VALUE>;

        private selectedOptionView: SelectedOptionView<OPTION_DISPLAY_VALUE>;

        private optionSelectedListeners: {(event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>):void}[] = [];

        private optionFilterInputValueChangedListeners: {(event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>):void}[] = [];

        private expandedListeners: {(event: api.ui.selector.DropdownExpandedEvent): void}[] = [];

        /**
         * Indicates if Dropdown currently has focus
         * @type {boolean}
         */
        private active: boolean = false;

        constructor(name: string, config: DropdownConfig<OPTION_DISPLAY_VALUE>) {
            super("div", "dropdown");
            this.getEl().setAttribute("name", name);

            this.optionDisplayValueViewer = config.optionDisplayValueViewer || new DefaultOptionDisplayValueViewer();

            if (config.iconUrl) {
                this.icon = new api.dom.ImgEl(config.iconUrl, "input-icon");
                this.appendChild(this.icon);
            }

            this.input = new DropdownOptionFilterInput();
            this.appendChild(this.input);


            this.dropdownHandle = new DropdownHandle();
            this.appendChild(this.dropdownHandle);
            var filter = this.defaultFilter;
            if (config.filter) {
                filter = config.filter;
            }
            this.dropdownList = new DropdownList(<DropdownListConfig<OPTION_DISPLAY_VALUE>>{
                maxHeight: 200,
                width: this.input.getEl().getWidth(),
                optionDisplayValueViewer: config.optionDisplayValueViewer,
                filter: filter,
                dataIdProperty: config.dataIdProperty
            });
            if (filter) {
                this.dropdownList.setFilterArgs({searchString: ""});
            }

            this.dropdownList.onRowSelection((event: DropdownGridRowSelectedEvent) => {
                this.selectRow(event.getRow());
            });

            this.appendChild(this.dropdownList.getEmptyDropdown());
            this.appendChild(this.dropdownList.getDropdownGrid().getElement());
            this.selectedOptionView = new SelectedOptionView<OPTION_DISPLAY_VALUE>(this.optionDisplayValueViewer);
            this.selectedOptionView.hide();
            this.appendChild(this.selectedOptionView);

            this.selectedOptionView.onOpenDropdown(() => {
                this.showDropdown();
                this.dropdownList.navigateToFirstRowIfNotActive();
                this.input.giveFocus();
            });

            this.setupListeners();

            this.onRendered((event: api.dom.ElementRenderedEvent) => {

                this.doUpdateDropdownTopPositionAndWidth();
            });
        }

        getInput(): DropdownOptionFilterInput {
            return this.input;
        }

        reset() {
            this.input.show();
            this.selectedOptionView.hide();
            this.selectedOptionView.resetOption();
            this.dropdownHandle.show();
        }

        private defaultFilter(option: Option<OPTION_DISPLAY_VALUE>, args: any) {

            if (!args.searchString || api.util.StringHelper.isEmpty(args.searchString)) {
                return true;
            }

            var lowerCasedSearchString = args.searchString.toLowerCase();
            if (option.value.toLowerCase().indexOf(lowerCasedSearchString) > -1) {
                return true;
            }

            var displayVaueAsString = option.displayValue.toString();
            if (displayVaueAsString.toLowerCase().indexOf(lowerCasedSearchString) > -1) {
                return true;
            }

            var indices = option.indices;
            if (indices && indices.length > 0) {
                for (var i = 0; i < indices.length; i++) {
                    var index = indices[i];
                    if (index) {
                        if (index.toLocaleLowerCase().indexOf(lowerCasedSearchString) > -1) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        private doUpdateDropdownTopPositionAndWidth() {
            var inputEl = this.input.getEl();
            this.dropdownList.setTopPx(inputEl.getHeightWithBorder() - inputEl.getBorderBottomWidth());
            this.dropdownList.setWidth(inputEl.getWidthWithBorder());
        }

        giveFocus(): boolean {
            return this.input.giveFocus();
        }

        isDropdownShown(): boolean {
            return this.dropdownList.isDropdownShown();
        }

        showDropdown() {

            this.selectedOptionView.hide();
            this.input.show();
            this.dropdownHandle.show();

            this.doUpdateDropdownTopPositionAndWidth();
            this.dropdownList.showDropdown([this.getSelectedOption()]);
            this.dropdownHandle.down();

            this.dropdownList.renderDropdownGrid();

            this.notifyExpanded();
        }

        hideDropdown() {

            if (this.selectedOptionView.getOption()) {
                this.input.hide();
                this.dropdownHandle.hide();
                this.selectedOptionView.show();
            } else {
                this.selectedOptionView.hide();
                this.input.show();
                this.dropdownHandle.up();
                this.dropdownHandle.show();
            }

            this.dropdownList.hideDropdown();
        }

        setOptions(options: Option<OPTION_DISPLAY_VALUE>[]) {
            this.dropdownList.setOptions(options);
        }

        removeAllOptions() {
            this.dropdownList.removeAllOptions();
        }

        addOption(option: Option<OPTION_DISPLAY_VALUE>) {
            this.dropdownList.addOption(option);
        }

        hasOptions(): boolean {
            return this.dropdownList.hasOptions();
        }

        getOptionCount(): number {
            return this.dropdownList.getOptionCount();
        }

        getOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            return this.dropdownList.getOptions();
        }

        getOptionByValue(value: string): Option<OPTION_DISPLAY_VALUE> {
            return this.dropdownList.getOptionByValue(value);
        }

        getOptionByRow(rowIndex: number): Option<OPTION_DISPLAY_VALUE> {
            return this.dropdownList.getOptionByRow(rowIndex);
        }

        setValue(value: string): Dropdown<OPTION_DISPLAY_VALUE> {
            var option = this.getOptionByValue(value);
            if (option != null) {
                this.selectOption(option);
            }
            return this;
        }

        selectRow(index: number, silent: boolean = false) {
            var option = this.getOptionByRow(index);
            if (option != null) {
                this.selectOption(option, silent);
                api.dom.FormEl.moveFocusToNextFocusable(this.input);
            }
        }

        selectOption(option: Option<OPTION_DISPLAY_VALUE>, silent: boolean = false) {

            this.dropdownList.markSelections([option]);
            if (!silent) {
                this.notifyOptionSelected(option);
            }

            this.selectedOptionView.setOption(option);

            this.hideDropdown();
        }

        getSelectedOption(): Option<OPTION_DISPLAY_VALUE> {
            return this.selectedOptionView.getOption();
        }

        getValue(): string {
            var selectedOption = this.getSelectedOption();
            if (!selectedOption) {
                return null;
            }
            return selectedOption.value;
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

            this.onClicked(() => {
                this.setOnBlurListener();
            });

            this.dropdownHandle.onClicked((event: any) => {

                this.dropdownList.navigateToFirstRowIfNotActive();

                if (this.isDropdownShown()) {
                    this.hideDropdown();
                    this.giveFocus();
                } else {
                    this.showDropdown();
                    this.giveFocus();
                }
            });

            this.input.onValueChanged((event: api.ui.ValueChangedEvent) => {

                this.notifyOptionFilterInputValueChanged(event.getOldValue(), event.getNewValue());

                this.dropdownList.setFilterArgs({searchString: event.getNewValue()});
                this.showDropdown();

                this.dropdownList.nagivateToFirstRow();

            });

            this.input.onDblClicked((event: MouseEvent) => {

                this.dropdownList.navigateToFirstRowIfNotActive();

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

                this.dropdownList.navigateToFirstRowIfNotActive();

                if (!this.isDropdownShown()) {
                    this.showDropdown();
                    return;
                }

                if (event.which == 38) { // up
                    this.dropdownList.navigateToPreviousRow();
                }
                else if (event.which == 40) { // down
                    this.dropdownList.navigateToNextRow();
                }
                else if (event.which == 13) { // enter
                    this.selectRow(this.dropdownList.getActiveRow());
                    this.input.getEl().setValue("");
                    event.preventDefault();
                    event.stopPropagation();
                }
                else if (event.which == 27) { // esc
                    this.hideDropdown();
                }

                this.input.getHTMLElement().focus();
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

        onValueChanged(listener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.optionFilterInputValueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) {
            this.optionFilterInputValueChangedListeners.filter((currentListener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                return listener != currentListener;
            })
        }

        private notifyOptionFilterInputValueChanged(oldValue: string, newValue: string) {
            var event = new OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>(oldValue, newValue,
                this.dropdownList.getDropdownGrid().getElement());
            this.optionFilterInputValueChangedListeners.forEach((listener: (event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>)=>void) => {
                listener(event);
            });
        }

        onExpanded(listener: (event: api.ui.selector.DropdownExpandedEvent)=>void) {
            this.expandedListeners.push(listener);
        }

        private notifyExpanded() {
            var event = new api.ui.selector.DropdownExpandedEvent(this.dropdownList.getDropdownGrid().getElement(), true);
            this.expandedListeners.forEach((listener: (event: api.ui.selector.DropdownExpandedEvent)=>void) => {
                listener(event);
            });
        }
    }

}