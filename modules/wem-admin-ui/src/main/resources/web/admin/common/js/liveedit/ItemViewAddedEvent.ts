module api.liveedit {

    export class ItemViewAddedEvent {

        private view: ItemView;

        constructor(view: ItemView) {
            this.view = view;
        }

        getView(): ItemView {
            return this.view;
        }
    }
}