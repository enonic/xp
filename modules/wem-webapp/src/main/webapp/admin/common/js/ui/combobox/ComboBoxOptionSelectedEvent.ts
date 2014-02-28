module api.ui.combobox {

    export class ComboBoxOptionSelectedEvent<T> {

        private item: Option<T>;

        constructor(item: Option<T>) {
            this.item = item;
        }

        getItem(): Option<T> {
            return this.item;
        }
    }
}