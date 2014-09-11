module app.view {

    import IsRenderableRequest = api.content.page.IsRenderableRequest;
    import RenderingMode = api.rendering.RenderingMode;
    import ContentImageUrlResolver = api.content.ContentImageUrlResolver;
    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;

    export class ContentItemPreviewPanel extends api.ui.panel.Panel {

        private frame: api.dom.IFrameEl;

        private image: api.dom.ImgEl;
        private item: ViewItem<ContentSummary>;

        private mask: api.ui.mask.LoadMask;

        constructor() {
            super("content-item-preview-panel");
            this.mask = new api.ui.mask.LoadMask(this);
            this.frame = new api.dom.IFrameEl();
            this.frame.onLoaded((event: UIEvent) => this.mask.hide());
            this.appendChild(this.frame);
            this.image = new api.dom.ImgEl();
            this.image.onLoaded((event: UIEvent) => {
                this.mask.hide();
                var imgEl = this.image.getEl();
                var myEl = this.getEl();
                this.centerImage(imgEl.getWidth(), imgEl.getHeight(), myEl.getWidth(), myEl.getHeight());
            });
            this.appendChild(this.image);

            this.onResized((event: api.dom.ElementResizedEvent) => {
                if (this.hasClass("image-preview")) {
                    var imgEl = this.image.getEl();
                    this.centerImage(imgEl.getWidth(), imgEl.getHeight(), event.getNewWidth(), event.getNewHeight());
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
            this.mask.show();
            this.item = item;
            if (item.getModel().getType().toString() == "image") {
                this.getEl().removeClass("no-preview page-preview").addClass("image-preview");
                if (this.isVisible()) {
                    this.addImageSizeToUrl(item);
                }
            } else {
                new IsRenderableRequest(item.getModel().getContentId()).sendAndParse()
                    .done((renderable: boolean) => {
                        if (renderable) {
                            this.getEl().removeClass("image-preview no-preview").addClass('page-preview');
                            this.frame.setSrc(api.rendering.UriHelper.getPortalUri(item.getPath(), RenderingMode.PREVIEW,
                                api.content.Workspace.STAGE));
                        } else {
                            this.getEl().removeClass("image-preview page-preview").addClass('no-preview');
                            this.frame.setSrc("about:blank");
                            this.mask.hide();
                        }
                    });
            }
        }

        public getItem(): ViewItem<ContentSummary> {
            return this.item;
        }

    }
}
