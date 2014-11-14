module api.app.view {

    export class ItemStatisticsHeader<M extends api.Equitable> extends api.dom.DivEl {

        private browseItem: ViewItem<M>;

        private iconEl: api.dom.ImgEl;

        private iconDivEl: api.dom.DivEl;

        private headerTextEl = new api.dom.H1El();

        constructor() {
            super("header");
            this.appendChild(this.headerTextEl);
        }

        setItem1(item: ViewItem<M>) {
            this.browseItem = item;

            if (this.iconEl) {
                this.removeChild(this.iconEl);
            }
            if (this.iconDivEl) {
                this.removeChild(this.iconDivEl);
            }
            var icon: HTMLImageElement = api.util.loader.ImageLoader.get(this.browseItem.getIconUrl() + "?size=64", 64, 64);
            this.iconEl = <api.dom.ImgEl> new api.dom.Element(new api.dom.NewElementBuilder().
                setTagName("img").
                setHelper(new api.dom.ImgHelper(icon)));
            this.prependChild(this.iconEl);

            this.headerTextEl.getEl().setInnerHtml(this.browseItem.getDisplayName()).setAttribute('title',
                this.browseItem.getDisplayName());
        }

        setItem(item: ViewItem<M>) {
            this.browseItem = item;
            if (this.iconEl) {
                this.removeChild(this.iconEl);
            }
            if (this.iconDivEl) {
                this.removeChild(this.iconDivEl);
            }
            var icon: HTMLImageElement = null;
            if (this.browseItem.getIconUrl()) {
                icon = api.util.loader.ImageLoader.get(this.browseItem.getIconUrl() + "?size=64", 64, 64);
                this.iconEl = <api.dom.ImgEl> new api.dom.Element(new api.dom.NewElementBuilder().
                    setTagName("img").
                    setHelper(new api.dom.ImgHelper(icon)));
                this.prependChild(this.iconEl);

            } else {
                this.iconDivEl = new api.dom.DivEl(this.browseItem.getIconClass())
                this.prependChild(this.iconDivEl);
            }

            this.headerTextEl.getEl().setInnerHtml(this.browseItem.getDisplayName()).setAttribute('title',
                this.browseItem.getDisplayName());
        }
    }

}