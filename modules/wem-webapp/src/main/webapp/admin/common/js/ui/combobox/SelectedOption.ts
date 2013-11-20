module api_ui_combobox {

    export class SelectedOption<T> {

        private optionView:SelectedOptionView<T>;

        private item:Option<T>;

        private index:number;

        constructor(optionView:SelectedOptionView<T>, option:Option<T>, index:number) {
            api_util.assertNotNull(optionView, "optionView cannot be null");
            api_util.assertNotNull(option, "option cannot be null");

            this.optionView = optionView;
            this.item = option;
            this.index = index;
        }

        getOption():Option<T> {
            return this.item;
        }

        getOptionView():SelectedOptionView<T> {
            return this.optionView;
        }

        getIndex():number {
            return this.index;
        }

        setIndex(value:number) {
            this.index = value;
        }
    }
}