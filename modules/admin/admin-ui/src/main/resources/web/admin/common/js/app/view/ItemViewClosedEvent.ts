module api.app.view {

    export class ItemViewClosedEvent<M extends api.Equitable> {

        private view: ItemViewPanel<M>;

        constructor(view: ItemViewPanel<M>) {
            this.view = view;
        }

        getView(): ItemViewPanel<M> {
            return this.view;
        }
    }
}