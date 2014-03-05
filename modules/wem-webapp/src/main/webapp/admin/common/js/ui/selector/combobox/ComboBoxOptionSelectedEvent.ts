module api.ui.selector.combobox {

    export class ComboBoxOptionSelectedEvent<T> {

        private item: api.ui.selector.Option<T>;

        constructor(item: api.ui.selector.Option<T>) {
            this.item = item;
        }

        getItem(): api.ui.selector.Option<T> {
            return this.item;
        }
    }
}