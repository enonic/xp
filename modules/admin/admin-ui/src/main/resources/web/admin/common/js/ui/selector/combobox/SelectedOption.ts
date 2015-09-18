module api.ui.selector.combobox {

    export class SelectedOption<T> {

        private optionView:SelectedOptionView<T>;

        private item:api.ui.selector.Option<T>;

        private index:number;

        constructor(optionView:SelectedOptionView<T>, index:number) {
            api.util.assertNotNull(optionView, "optionView cannot be null");

            this.optionView = optionView;
            this.index = index;
        }

        getOption():api.ui.selector.Option<T> {
            return this.optionView.getOption();
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