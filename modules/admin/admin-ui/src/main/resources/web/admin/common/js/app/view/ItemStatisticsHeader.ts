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
                var icon: HTMLImageElement = null;
                if (item.getIconUrl()) {
                    var size = item.getIconSize() || 64;
                    icon = api.util.loader.ImageLoader.get(item.getIconUrl() + "?size=size", size, size);
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

                if (item.getDisplayName()) {
                    this.headerTitleEl.getEl().
                        setInnerHtml(item.getDisplayName(), true).setAttribute("title", item.getDisplayName());
                } else {
                    this.headerTitleEl.getEl().setInnerHtml("").setAttribute("title", "");
                }

                this.headerPathEl.removeChildren();
                if (item.getPath()) {
                    var path = new api.dom.SpanEl("parent-path");
                    path.getEl().setInnerHtml(item.getPath(), true);
                    this.headerPathEl.appendChild(path);
                }
                if (item.getPath()) {
                    var pathName = new api.dom.SpanEl("path-name");
                    pathName.getEl().setInnerHtml(item.getPathName(), true);
                    this.headerPathEl.appendChild(pathName);
                }
            }
            this.browseItem = item;
        }
    }

}