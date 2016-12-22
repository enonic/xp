import "../../api.ts";
import {ContentPreviewPathChangedEvent} from "./ContentPreviewPathChangedEvent";

import RenderingMode = api.rendering.RenderingMode;
import ViewItem = api.app.view.ViewItem;
import ContentSummary = api.content.ContentSummary;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import UriHelper = api.util.UriHelper;
import ContentTypeName = api.schema.content.ContentTypeName;
import PEl = api.dom.PEl;


enum PREVIEW_TYPE {
    IMAGE,
    SVG,
    PAGE,
    EMPTY,
    FAILED,
    BLANK
}

export class ContentItemPreviewPanel extends api.app.view.ItemPreviewPanel {

    private image: api.dom.ImgEl;
    private item: ViewItem<ContentSummaryAndCompareStatus>;
    private skipNextSetItemCall: boolean = false;
    private previewType: PREVIEW_TYPE;
    private previewMessageEl: PEl;

    constructor() {
        super("content-item-preview-panel");
        this.image = new api.dom.ImgEl();
        this.image.onLoaded((event: UIEvent) => {
            this.hideMask();
            var imgEl = this.image.getEl();
            var myEl = this.getEl();
            this.centerImage(imgEl.getWidth(), imgEl.getHeight(), myEl.getWidth(), myEl.getHeight());
        });

        this.image.onError((event: UIEvent) => {
            this.setPreviewType(PREVIEW_TYPE.FAILED);
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

        this.onHidden((event) => {
            if (this.mask.isVisible()) {
                this.hideMask();
            }
        });

        this.frame.onLoaded((event: UIEvent) => {
            var frameWindow = this.frame.getHTMLElement()["contentWindow"];

            try {
                if (frameWindow) {
                    frameWindow.addEventListener("click", this.frameClickHandler.bind(this));
                }
            } catch (error) { /* error */ }
        });
    }

    private frameClickHandler(event: UIEvent) {
        let linkClicked: string = this.getLinkClicked(event);
        if (linkClicked) {
            var frameWindow = this.frame.getHTMLElement()["contentWindow"];
            if (!!frameWindow && !UriHelper.isNavigatingOutsideOfXP(linkClicked, frameWindow)) {
                var contentPreviewPath = UriHelper.trimUrlParams(
                    UriHelper.trimAnchor(UriHelper.trimWindowProtocolAndPortFromHref(linkClicked,
                    frameWindow)));
                if (!this.isNavigatingWithinSamePage(contentPreviewPath, frameWindow)) {
                    event.preventDefault();
                    var clickedLinkRelativePath = "/" + UriHelper.trimWindowProtocolAndPortFromHref(linkClicked, frameWindow);
                    this.skipNextSetItemCall = true;
                    new ContentPreviewPathChangedEvent(contentPreviewPath).fire();
                    this.showMask();
                    setTimeout(() => {
                        this.item = null; // we don't have ref to content under contentPreviewPath and there is no point in figuring it out
                        this.skipNextSetItemCall = false;
                        this.frame.setSrc(clickedLinkRelativePath);
                    }, 500);
                }
            }
        }
    }

    private getLinkClicked(event: UIEvent): string {
        if (event.target && (<any>event.target).tagName.toLowerCase() === 'a') {
            return (<any>event.target).href;
        }

        let el: Element = <Element>event.target;
        if (el) {
            while (el.parentNode) {
                el = <Element>el.parentNode;
                if (el.tagName && el.tagName.toLowerCase() === 'a') {
                    return (<any>el).href;
                }
            }
        }
        return "";
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
        var imgUrl = new api.content.util.ContentImageUrlResolver().setContentId(item.getModel().getContentId()).setTimestamp(
            item.getModel().getContentSummary().getModifiedTime()).setSize(imgSize).resolve();
        this.image.setSrc(imgUrl);
    }

    public setItem(item: ViewItem<ContentSummaryAndCompareStatus>) {
        if (item && !item.equals(this.item) && !this.skipNextSetItemCall) {
            if (typeof item.isRenderable() === "undefined") {
                return;
            }
            if (item.getModel().getContentSummary().getType().isImage() ||
                item.getModel().getContentSummary().getType().isVectorMedia()) {

                if (this.isVisible()) {
                    if (item.getModel().getContentSummary().getType().equals(ContentTypeName.MEDIA_VECTOR)) {
                        this.setPreviewType(PREVIEW_TYPE.SVG);
                        var imgUrl = new api.content.util.ContentImageUrlResolver().setContentId(
                            item.getModel().getContentId()).setTimestamp(
                            item.getModel().getContentSummary().getModifiedTime()).resolve();
                        this.image.setSrc(imgUrl);
                    } else {
                        this.addImageSizeToUrl(item);
                        this.setPreviewType(PREVIEW_TYPE.IMAGE);
                    }
                } else {
                    this.setPreviewType(PREVIEW_TYPE.IMAGE);
                }
                if (!this.image.isLoaded()) {
                    this.showMask();
                }
            } else {
                this.showMask();
                if (item.isRenderable()) {
                    this.setPreviewType(PREVIEW_TYPE.PAGE);
                    var src = api.rendering.UriHelper.getPortalUri(item.getPath(), RenderingMode.PREVIEW, api.content.Branch.DRAFT);
                    // test if it returns no error( like because of used app was deleted ) first and show no preview otherwise
                    wemjq.ajax({
                        type: "HEAD",
                        async: true,
                        url: src
                    }).done(() => {
                        this.frame.setSrc(src);
                    }).fail(() => this.setPreviewType(PREVIEW_TYPE.FAILED));
                } else {
                    this.setPreviewType(PREVIEW_TYPE.EMPTY);
                }
            }
        }
        this.item = item;
    }

    public getItem(): ViewItem<ContentSummaryAndCompareStatus> {
        return this.item;
    }

    public setBlank() {
        this.setPreviewType(PREVIEW_TYPE.BLANK);
    }

    public setBlankFrame() {
        this.frame.setSrc("about:blank");
    }

    private setPreviewType(previewType: PREVIEW_TYPE) {

        if (this.previewType != previewType) {

            this.getEl().removeClass("image-preview page-preview svg-preview no-preview");

            if (this.previewMessageEl) {
                this.previewMessageEl.remove();
                this.previewMessageEl = null;
            }

            switch (previewType) {
            case PREVIEW_TYPE.PAGE:
            {
                this.getEl().addClass("page-preview");
                break;
            }
            case PREVIEW_TYPE.IMAGE:
            {
                this.getEl().addClass("image-preview");
                break;
            }
            case PREVIEW_TYPE.SVG:
            {
                this.getEl().addClass("svg-preview");
                break;
            }
            case PREVIEW_TYPE.EMPTY:
            {
                this.showPreviewMessage("Preview not available");
                break;
            }
            case PREVIEW_TYPE.FAILED:
            {
                this.showPreviewMessage(
                    "Failed to render content preview.<br/> Please check logs for errors or open preview in a new window");
                break;
            }
            case PREVIEW_TYPE.BLANK:
            {
                this.getEl().addClass("no-preview");
                break;
            }
            }
        }

        this.previewType = previewType;

        if (PREVIEW_TYPE.FAILED == previewType || PREVIEW_TYPE.EMPTY == previewType) {
            this.hideMask();
        }
    }

    private showPreviewMessage(value: string, escapeHtml: boolean = false) {
        this.getEl().addClass("no-preview");

        this.appendChild(this.previewMessageEl = new PEl("no-preview-message").setHtml(
            value, false));

        this.frame.setSrc("about:blank");
    }

    public showMask() {
        if (this.isVisible()) {
            this.mask.show();
        }
    }

    private hideMask() {
        this.mask.hide();
    }

}
