module app.view {

    import RenderingMode = api.rendering.RenderingMode;
    import ContentImageUrlResolver = api.content.ContentImageUrlResolver;
    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;

    export class ContentItemPreviewPanel extends api.app.view.ItemPreviewPanel {

        private image: api.dom.ImgEl;
        private item: ViewItem<ContentSummary>;

        constructor() {
            super("content-item-preview-panel");
            this.image = new api.dom.ImgEl();
            this.image.onLoaded((event: UIEvent) => {
                this.mask.hide();
                var imgEl = this.image.getEl();
                var myEl = this.getEl();
                this.centerImage(imgEl.getWidth(), imgEl.getHeight(), myEl.getWidth(), myEl.getHeight());
            });
            this.appendChild(this.image);

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
                if (this.hasClass("image-preview")) {
                    var imgEl = this.image.getEl(),
                        el = this.getEl();
                    this.centerImage(imgEl.getWidth(), imgEl.getHeight(), el.getWidth(), el.getHeight());
                }
            });

            this.onShown((event) => {
                if (this.item && this.hasClass("image-preview")) {
                    this.addImageSizeToUrl(this.item);
                }
            });
        }

        private centerImage(imgWidth, imgHeight, myWidth, myHeight) {
            var imgMarginTop = 0;
            if (imgHeight < myHeight) {
                // image should be centered vertically
                imgMarginTop = (myHeight - imgHeight) / 2;
            }
            this.image.getEl().setMarginTop(imgMarginTop + "px");

        }

        public addImageSizeToUrl(item: ViewItem<ContentSummary>) {
            var imgSize = Math.max(this.getEl().getWidth(), this.getEl().getHeight());
            var imgUrl = new ContentImageUrlResolver().
                setContentId(item.getModel().getContentId()).
                setSize(imgSize).resolve();
            this.image.setSrc(imgUrl);
        }

        public setItem(item: ViewItem<ContentSummary>) {
            if (typeof item.isRenderable() === "undefined") {
                return;
            }
            if (!this.item || !item || !this.item.equals(item)) {
                this.item = item;
                if (item.getModel().getType().isImage()) {
                    this.getEl().removeClass("no-preview page-preview").addClass("image-preview");
                    if (this.isVisible()) {
                        this.addImageSizeToUrl(item);
                    }
                    if (!this.image.isLoaded()) {
                        this.mask.show();
                    }
                } else {
                    this.mask.show();
                    if (item.isRenderable()) {
                        this.getEl().removeClass("image-preview no-preview").addClass('page-preview');
                        this.frame.setSrc(api.rendering.UriHelper.getPortalUri(item.getPath(), RenderingMode.PREVIEW,
                            api.content.Branch.DRAFT));
                    } else {
                        this.getEl().removeClass("image-preview page-preview").addClass('no-preview');
                        this.frame.setSrc("about:blank");
                        this.mask.hide();
                    }
                }
            }
        }

        public getItem(): ViewItem<ContentSummary> {
            return this.item;
        }

    }
}
