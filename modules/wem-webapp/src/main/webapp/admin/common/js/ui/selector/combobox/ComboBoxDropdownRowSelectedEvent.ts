module api.ui.selector.combobox {

    export class ComboBoxDropdownRowSelectedEvent {

        private row: number;

        constructor(row: number) {
            this.row = row;
        }

        getRow(): number {
            return this.row;
        }

    }
}
