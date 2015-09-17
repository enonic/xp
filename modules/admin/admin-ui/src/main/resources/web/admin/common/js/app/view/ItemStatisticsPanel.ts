module api.app.view {

    export class ItemStatisticsPanel<M extends api.Equitable> extends api.ui.panel.Panel {

        private browseItem: ViewItem<M>;

        private header: ItemStatisticsHeader<M>;

        constructor(className?: string) {
            super("item-statistics-panel" + (className ? " " + className : ""));

            this.header = new ItemStatisticsHeader<M>();
            this.appendChild(this.header);
        }

        getHeader(): ItemStatisticsHeader<M> {
            return this.header;
        }


        setItem(item: api.app.view.ViewItem<M>) {
            this.browseItem = item;
            this.header.setItem(item);
        }

        getItem(): ViewItem<M> {
            return this.browseItem;
        }
    }
}
