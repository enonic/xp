module app.view {

    import IsRenderableRequest = api.content.page.IsRenderableRequest;
    import RenderingMode = api.util.RenderingMode;

    export class ContentItemPreviewPanel extends api.ui.Panel {

        private frame: api.dom.IFrameEl;

        private image: api.dom.ImgEl;

        private mask: api.ui.LoadMask;

        constructor() {
            super("item-preview-panel");
            this.mask = new api.ui.LoadMask(this);
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
        }

        private centerImage(imgWidth, imgHeight, myWidth, myHeight) {
            var imgMarginTop = 0;
            if (imgHeight < myHeight) {
                // image should be centered vertically
                imgMarginTop = (myHeight - imgHeight) / 2;
            }
            this.image.getEl().setMarginTop(imgMarginTop + "px");
        }

        public setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {

            this.mask.show();

            if (item.getModel().getType().toString() == "image") {
                this.getEl().removeClass("no-preview page-preview").addClass("image-preview");
                var imgSize = Math.max(this.getEl().getWidth(), this.getEl().getHeight());
                var imgSrc = api.util.getRestUri("content/image/") + item.getModel().getContentId();
                this.image.setSrc(imgSrc + '?thumbnail=false&size=' + imgSize);
            } else {
                new IsRenderableRequest(item.getModel().getContentId()).sendAndParse()
                    .done((renderable: boolean) => {
                        if (renderable) {
                            this.getEl().removeClass("image-preview no-preview").addClass('page-preview');
                            this.frame.setSrc(api.util.getPortalUri(item.getPath(), RenderingMode.PREVIEW));
                        } else {
                            this.getEl().removeClass("image-preview page-preview").addClass('no-preview');
                            this.frame.setSrc("about:blank");
                            this.mask.hide();
                        }
                    });
            }
        }

    }
}
