module app.view {

    import RenderingMode = api.rendering.RenderingMode;
    import ContentImageUrlResolver = api.content.ContentImageUrlResolver;
    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import UriHelper = api.util.UriHelper;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class ContentItemPreviewPanel extends api.app.view.ItemPreviewPanel {

        private image: api.dom.ImgEl;
        private item: ViewItem<ContentSummaryAndCompareStatus>;
        private skipNextSetItemCall: boolean = false;

        constructor() {
            super("content-item-preview-panel");
            this.image = new api.dom.ImgEl();
            this.image.onLoaded((event: UIEvent) => {
                this.mask.hide();
                var imgEl = this.image.getEl();
                var myEl = this.getEl();
                this.centerImage(imgEl.getWidth(), imgEl.getHeight(), myEl.getWidth(), myEl.getHeight());
            });

            this.image.onError((event: UIEvent) => {
                this.setNoPreview();
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

            this.frame.onLoaded((event: UIEvent) => {
                var frameWindow = this.frame.getHTMLElement()["contentWindow"];

                try {
                    if (frameWindow) {
                        var pathname: string = frameWindow.location.pathname;
                        if (pathname && pathname !== 'blank') {
                            new ContentPreviewPathChangedEvent(pathname).fire();
                        }
                        frameWindow.addEventListener("click", this.frameClickHandler.bind(this));
                    }
                } catch (reason) {}
            });
        }

        private frameClickHandler(event: UIEvent) {
            if (this.isLinkClicked(event)) {
                var href = (<any>event.target).href,
                    frameWindow = this.frame.getHTMLElement()["contentWindow"];
                if (!!frameWindow && !UriHelper.isNavigatingOutsideOfXP(href, frameWindow)) {
                    var contentPreviewPath = UriHelper.trimUrlParams(UriHelper.trimAnchor(UriHelper.trimWindowProtocolAndPortFromHref(href,
                        frameWindow)));
                    if (!this.isNavigatingWithinSamePage(contentPreviewPath, frameWindow)) {
                        event.preventDefault();
                        var clickedLinkRelativePath = "/" + UriHelper.trimWindowProtocolAndPortFromHref(href, frameWindow);
                        this.skipNextSetItemCall = true;
                        new ContentPreviewPathChangedEvent(contentPreviewPath).fire();
                        this.showMask();
                        setTimeout(() => {
                            this.skipNextSetItemCall = false;
                            this.frame.setSrc(clickedLinkRelativePath);
                        }, 500)
                    }
                }
            }
        }

        private isLinkClicked(event: UIEvent): boolean {
            return event.target && (<any>event.target).tagName.toLowerCase() === 'a';
        }

        private isNavigatingWithinSamePage(contentPreviewPath: string, frameWindow: Window): boolean {
            var href = frameWindow.location.href;
            return contentPreviewPath === UriHelper.trimAnchor(UriHelper.trimWindowProtocolAndPortFromHref(href, frameWindow));
        }

        private centerImage(imgWidth, imgHeight, myWidth, myHeight) {
            var imgMarginTop = 0;
            if (imgHeight < myHeight) {
                // image should be centered vertically
                imgMarginTop = (myHeight - imgHeight) / 2;
            }
            this.image.getEl().setMarginTop(imgMarginTop + "px");

        }

        public addImageSizeToUrl(item: ViewItem<ContentSummaryAndCompareStatus>) {
            var imgSize = Math.max(this.getEl().getWidth(), this.getEl().getHeight());
            var imgUrl = new ContentImageUrlResolver().
                setContentId(item.getModel().getContentId()).
                setTimestamp(item.getModel().getContentSummary().getModifiedTime()).
                setSize(imgSize).resolve();
            this.image.setSrc(imgUrl);
        }

        public setItem(item: ViewItem<ContentSummaryAndCompareStatus>) {
            if (item && !item.equals(this.item) && !this.skipNextSetItemCall) {
                if (typeof item.isRenderable() === "undefined") {
                    return;
                }
                if (item.getModel().getContentSummary().getType().isImage() ||
                    item.getModel().getContentSummary().getType().isVectorMedia()) {
                    this.getEl().removeClass("no-preview page-preview").addClass("image-preview");
                    if (this.isVisible()) {
                        if (item.getModel().getContentSummary().getType().equals(ContentTypeName.MEDIA_VECTOR)) {
                            this.getEl().addClass("svg-preview");
                            var imgUrl = new ContentImageUrlResolver().
                                setContentId(item.getModel().getContentId()).
                                setTimestamp(item.getModel().getContentSummary().getModifiedTime()).
                                resolve();
                            this.image.setSrc(imgUrl);
                        } else {
                            this.addImageSizeToUrl(item);
                        }
                    }
                    if (!this.image.isLoaded()) {
                        this.showMask();
                    }
                } else {
                    this.showMask();
                    if (item.isRenderable()) {
                        this.getEl().removeClass("image-preview no-preview svg-preview").addClass('page-preview');
                        var src = api.rendering.UriHelper.getPortalUri(item.getPath(), RenderingMode.PREVIEW,
                            api.content.Branch.DRAFT);
                        this.frame.setSrc(src);
                    } else {
                        this.setNoPreview();
                    }
                }
            }
            this.item = item;
        }

        public getItem(): ViewItem<ContentSummaryAndCompareStatus> {
            return this.item;
        }

        private setNoPreview() {
            this.getEl().removeClass("image-preview page-preview svg-preview").addClass('no-preview');
            this.frame.setSrc("about:blank");
            this.mask.hide();
        }

        private showMask() {
            if (this.isVisible()) {
                this.mask.show();
            }
        }

    }
}
