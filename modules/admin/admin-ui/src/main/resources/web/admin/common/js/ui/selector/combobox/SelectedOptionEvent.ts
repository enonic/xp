module api.ui.selector.combobox {

    export class SelectedOptionEvent<T> {

        private selectedOption: SelectedOption<T>;

        private keyCode: number;

        constructor(selectedOption: SelectedOption<T>, keyCode: number = -1) {
            this.selectedOption = selectedOption;
            this.keyCode = keyCode;
        }

        getSelectedOption(): SelectedOption<T> {
            return this.selectedOption;
        }

        getKeyCode(): number {
            return this.keyCode;
        }
    }
}
