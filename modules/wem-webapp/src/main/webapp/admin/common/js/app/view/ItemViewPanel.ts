module api.app.view {

    export class ItemViewPanel<M> extends api.ui.Panel implements api.ui.Closeable, api.event.Observable {

        private toolbar:api.ui.toolbar.Toolbar;

        private panel:api.ui.Panel;

        private browseItem:ViewItem<M>;

        private listeners:ItemViewPanelListener<M>[] = [];

        constructor(toolbar:api.ui.toolbar.Toolbar, panel:api.ui.Panel) {
            super(true, "item-view-panel");
            this.toolbar = toolbar;
            this.panel = panel;
            this.appendChild(this.toolbar);
            this.appendChild(this.panel)
        }

        afterRender() {
            super.afterRender();
            this.panel.afterRender();
        }

        setItem(item:ViewItem<M>) {
            this.browseItem = item;
        }

        getItem():ViewItem<M> {
            return this.browseItem;
        }

        close(checkCanClose:boolean = false) {
            if (checkCanClose && !this.canClose()) {
                return;
            }
            this.closing();
        }

        canClose():boolean {
            return true;
        }

        closing() {
            this.notifyClosedListeners();
        }

        addListener(listener:ItemViewPanelListener<M>) {
            this.listeners.push(listener);
        }

        removeListener(listener:ItemViewPanelListener<M>) {
            this.listeners = this.listeners.filter((elem) => {
                return elem != listener;
            });
        }

        private notifyClosedListeners() {
            this.listeners.forEach((listener:ItemViewPanelListener<M>) => {
                if (listener.onClosed) {
                    listener.onClosed(this);
                }
            });
        }

    }

}