import "../../../api.ts";

import ContentSummary = api.content.ContentSummary;
import Attachments = api.content.attachment.Attachments;
import Attachment = api.content.attachment.Attachment;
import ContentId = api.content.ContentId;
import AttachmentName = api.content.attachment.AttachmentName;
import {WidgetItemView} from "./WidgetItemView";

export class AttachmentsWidgetItemView extends WidgetItemView {

    private content: ContentSummary;

    private list: api.dom.UlEl;

    private placeholder: api.dom.SpanEl;

    public static debug = false;

    constructor() {
        super('attachments-widget-item-view');
    }

    public setContent(content: ContentSummary) {
        if (AttachmentsWidgetItemView.debug) {
            console.debug('AttachmentsWidgetItemView.setContent: ', content);
        }
        if (!api.ObjectHelper.equals(content, this.content)) {
            this.content = content;
            return this.layout();
        }
        return wemQ<any>(null);
    }

    public layout(): wemQ.Promise<any> {
        if (AttachmentsWidgetItemView.debug) {
            console.debug('AttachmentsWidgetItemView.layout');
        }

        return super.layout().then(() => {
            if (this.content != undefined) {
                return this.layoutAttachments();
            } else {
                this.removeChildren();
            }
        });
    }

    private layoutAttachments(): wemQ.Promise<Attachments> {
        return new api.content.GetContentAttachmentsRequest(this.content.getContentId()).sendAndParse().then(
            (attachments: Attachments) => {

                if (this.hasChild(this.list)) {
                    this.removeChild(this.list);
                }

                if (this.hasChild(this.placeholder)) {
                    this.removeChild(this.placeholder);
                }

                if (attachments) {
                    this.list = new api.dom.UlEl('attachment-list');

                    var contentId = this.content.getContentId();
                    attachments.forEach((attachment: Attachment) => {
                        var attachmentContainer = new api.dom.LiEl('attachment-container');
                        var link = this.createLinkEl(contentId, attachment.getName());
                        attachmentContainer.appendChild(link);
                        this.list.appendChild(attachmentContainer);

                    });

                    this.appendChild(this.list);

                } else {
                    this.placeholder = new api.dom.SpanEl('att-placeholder').setHtml('This item has no attachments');
                    this.appendChild(this.placeholder);
                }

                return attachments;
            });
    }

    private createLinkEl(contentId: ContentId, attachmentName: AttachmentName): api.dom.AEl {
        var url = `content/media/${contentId.toString()}/${attachmentName.toString()}`;
        var link = new api.dom.AEl().setUrl(api.util.UriHelper.getRestUri(url), '_blank');
        link.setHtml(attachmentName.toString());
        return link;
    }

}
