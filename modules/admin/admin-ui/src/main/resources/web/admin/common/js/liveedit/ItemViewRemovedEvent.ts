module api.liveedit {

    export class ItemViewRemovedEvent {

        private view: ItemView;

        constructor(view: ItemView) {
            this.view = view;
        }

        getView(): ItemView {
            return this.view;
        }
    }
}