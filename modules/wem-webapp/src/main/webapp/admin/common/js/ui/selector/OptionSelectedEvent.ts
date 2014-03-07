module api.ui.selector {

    export class OptionSelectedEvent<T> {

        private item: Option<T>;

        constructor(item: Option<T>) {
            this.item = item;
        }

        getItem(): Option<T> {
            return this.item;
        }
    }
}