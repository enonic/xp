module api_app_view {

    export class ItemViewPanel<M> extends api_ui.Panel implements api_ui.Closeable, api_event.Observable {

        private toolbar:api_ui_toolbar.Toolbar;

        private panel:api_ui.Panel;

        private browseItem:ViewItem<M>;

        private listeners:ItemViewPanelListener[] = [];

        constructor(toolbar:api_ui_toolbar.Toolbar, panel:api_ui.Panel) {
            super("ItemViewPanel");
            this.getEl().addClass("item-view-panel");
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