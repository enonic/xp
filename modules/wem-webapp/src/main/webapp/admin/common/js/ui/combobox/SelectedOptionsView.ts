module api_ui_combobox {

    export class SelectedOptionsView<T> extends api_dom.DivEl {

        private selectedOptions:SelectedOptions<T>;

        constructor() {
            super("ComboBoxSelectedOptionsView", "selected-options");
        }

        setSelectedOptions(value:SelectedOptions<T>) {
            this.selectedOptions = value;
        }

        getSelectedOptionViews(): SelectedOptionView<T>[] {
            return this.selectedOptions.getOptionViews();
        }

        createSelectedOption(option:Option<T>, index:number):SelectedOption<T> {
            return new SelectedOption<T>(new SelectedOptionView(option), option, index);
        }

        addOptionView(selectedOption:SelectedOption<T>) {
            this.appendChild(selectedOption.getOptionView());
        }

        removeOptionView(selectedOption:SelectedOption<T>) {
            selectedOption.getOptionView().remove();
        }
    }
}