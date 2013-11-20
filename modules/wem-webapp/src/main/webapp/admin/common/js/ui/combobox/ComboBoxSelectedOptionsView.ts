module api_ui_combobox {

    export class ComboBoxSelectedOptionsView<T> extends api_dom.DivEl {

        private selectedOptions:SelectedOptions<T>;

        constructor() {
            super("ComboBoxSelectedOptionsView", "selected-options");
        }

        setSelectedOptions(value:SelectedOptions<T>) {
            this.selectedOptions = value;
        }

        getSelectedOptionViews(): ComboBoxSelectedOptionView<T>[] {
            return this.selectedOptions.getOptionViews();
        }

        createSelectedOption(option:Option<T>, index:number):SelectedOption<T> {
            return new SelectedOption<T>(new ComboBoxSelectedOptionView(option), option, index);
        }

        addOptionView(selectedOption:SelectedOption<T>) {
            this.appendChild(selectedOption.getOptionView());
        }

        removeOptionView(selectedOption:SelectedOption<T>) {
            selectedOption.getOptionView().remove();
        }
    }
}