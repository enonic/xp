module api.app.view {

    export class ItemStatisticsHeader<M extends api.Equitable> extends api.dom.DivEl {

        private browseItem: ViewItem<M>;

        private iconEl: api.dom.ImgEl;

        private iconDivEl: api.dom.DivEl;

        private headerTitleEl: api.dom.H1El;

        private headerPathEl: api.dom.H4El;

        constructor() {
            super("header");
            this.headerTitleEl = new api.dom.H1El("title");
            this.headerPathEl = new api.dom.H4El("path");
            this.appendChild(this.headerTitleEl);
            this.appendChild(this.headerPathEl);
        }

        setItem(item: ViewItem<M>) {

            if (this.iconEl) {
                this.iconEl.remove();
            }
            if (this.iconDivEl) {
                this.iconDivEl.remove();
            }
            if (item) {
                if (item.getIconUrl()) {
                    var size = item.getIconSize() || 64,
                        icon: HTMLImageElement = api.util.loader.ImageLoader.get(item.getIconUrl() + "?size=size", size, size);

                    this.iconEl = <api.dom.ImgEl> new api.dom.Element(new api.dom.NewElementBuilder().
                        setTagName("img").
                        setHelper(new api.dom.ImgHelper(icon)));

                    this.iconEl.addClass("icon");
                    this.prependChild(this.iconEl);
                } else {
                    this.iconDivEl = new api.dom.DivEl(item.getIconClass());
                    this.iconDivEl.addClass("icon");
                    this.prependChild(this.iconDivEl);
                }

                var displayName = item.getDisplayName() || '';
                this.headerTitleEl.getEl().setInnerHtml(displayName, true).setAttribute("title", displayName);

                this.headerPathEl.removeChildren();
                if (item.getPath()) {
                    this.appendToHeaderPath(item.getPath(), 'parent-path');
                    this.appendToHeaderPath(item.getPathName(), 'path-name');
                }
            }
            this.browseItem = item;
        }

        private appendToHeaderPath(value, className) {
            var pathName = new api.dom.SpanEl(className);
            pathName.getEl().setInnerHtml(value, true);
            this.headerPathEl.appendChild(pathName);
        }
    }

}