module api_ui_combobox {
    export class ComboBoxSelectedOption<T> {

        private optionEl:api_dom.Element;

        private item:OptionData<T>;

        constructor(optionView:api_dom.Element, item:OptionData<T>) {
            this.optionEl = optionView;
            this.item = item;
        }

        getItem():OptionData<T> {
            return this.item;
        }

        getOptionEl():api_dom.Element {
            return this.optionEl;
        }
    }
}