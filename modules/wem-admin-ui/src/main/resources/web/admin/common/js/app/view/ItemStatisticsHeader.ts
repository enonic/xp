module api.app.view {

    export class ItemStatisticsHeader<M extends api.Equitable> extends api.dom.DivEl {

        private browseItem: ViewItem<M>;

        private iconEl: api.dom.ImgEl;

        private iconDivEl: api.dom.DivEl;

        private headerTitleEl: api.dom.H1El;

        private headerPathEl: api.dom.H4El;

        constructor() {
            super("header");
            this.headerTitleEl  = new api.dom.H1El("title");
            this.headerPathEl  = new api.dom.H4El("path");
            this.appendChild(this.headerTitleEl);
            this.appendChild(this.headerPathEl);
        }

        setItem(item: ViewItem<M>) {
            this.browseItem = item;

            if (this.iconEl) {
                this.iconEl.remove();
            }
            if (this.iconDivEl) {
                this.iconDivEl.remove();
            }

            var icon: HTMLImageElement = null;
            if (this.browseItem.getIconUrl()) {
                var size = this.browseItem.getIconSize() || 64;
                icon = api.util.loader.ImageLoader.get(this.browseItem.getIconUrl() + "?size=size", size, size);
                this.iconEl = <api.dom.ImgEl> new api.dom.Element(new api.dom.NewElementBuilder().
                    setTagName("img").
                    setHelper(new api.dom.ImgHelper(icon)));
                this.iconEl.addClass("icon");
                this.prependChild(this.iconEl);
            } else {
                this.iconDivEl = new api.dom.DivEl(this.browseItem.getIconClass());
                this.iconDivEl.addClass("icon");
                this.prependChild(this.iconDivEl);
            }

            if (this.browseItem.getDisplayName()) {
                this.headerTitleEl.getEl().
                    setInnerHtml(this.browseItem.getDisplayName()).setAttribute("title", this.browseItem.getDisplayName());
            } else {
                this.headerTitleEl.getEl().setInnerHtml("").setAttribute("title", "");
            }

            this.headerPathEl.removeChildren();
            if (this.browseItem.getPath()) {
                var path = new api.dom.SpanEl("parent-path");
                path.getEl().setInnerHtml(this.browseItem.getPath());
                this.headerPathEl.appendChild(path);
            }
            if (this.browseItem.getPath()) {
                var pathName = new api.dom.SpanEl("path-name");
                pathName.getEl().setInnerHtml(this.browseItem.getPathName());
                this.headerPathEl.appendChild(pathName);
            }
        }
    }

}