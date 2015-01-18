module api.app.view {

    export class ItemViewPanel<M extends api.Equitable> extends api.ui.panel.Panel implements api.ui.Closeable {

        private toolbar: api.ui.toolbar.Toolbar;

        private panel: api.ui.panel.Panel;

        private browseItem: ViewItem<M>;

        private closedListeners: {(event: ItemViewClosedEvent<M>):void}[] = [];

        constructor(toolbar: api.ui.toolbar.Toolbar, panel: api.ui.panel.Panel) {
            super("item-view-panel");
            this.toolbar = toolbar;
            this.panel = panel;
            this.appendChild(this.toolbar);
            this.appendChild(this.panel);
        }

        setItem(item: ViewItem<M>) {
            this.browseItem = item;
        }

        getItem(): ViewItem<M> {
            return this.browseItem;
        }

        close(checkCanClose: boolean = false) {
            if (!checkCanClose || this.canClose()) {
                this.notifyClosed();
            }
        }

        canClose(): boolean {
            return true;
        }


        onClosed(listener: (event: ItemViewClosedEvent<M>)=>void) {
            this.closedListeners.push(listener);
        }

        unClosed(listener: (event: ItemViewClosedEvent<M>)=>void) {
            this.closedListeners = this.closedListeners.filter((currentListener: (event: ItemViewClosedEvent<M>)=>void) => {
                return currentListener != listener;
            })
        }

        private notifyClosed() {
            this.closedListeners.forEach((listener: (event: ItemViewClosedEvent<M>)=>void) => {
                listener.call(this, new ItemViewClosedEvent(this));
            });
        }

    }

}