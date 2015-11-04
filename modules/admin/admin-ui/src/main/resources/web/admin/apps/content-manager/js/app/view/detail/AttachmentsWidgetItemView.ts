module app.view.detail {

    import CompareStatus = api.content.CompareStatus;
    import CompareStatusFormatter = api.content.CompareStatusFormatter;
    import ContentSummary = api.content.ContentSummary;
    import MediaUploader = api.content.MediaUploader;
    import Attachments = api.content.attachment.Attachments;
    import Attachment = api.content.attachment.Attachment;
    import DateTimeFormatter = api.ui.treegrid.DateTimeFormatter;

    export class AttachmentsWidgetItemView extends WidgetItemView {

        private content: ContentSummary;

        public static debug = false;

        constructor() {
            super("attachments-widget-item-view");
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
            this.removeChildren();

            return super.layout().then(() => {
                if (this.content != undefined) {
                    return this.layoutAttachments();
                }
            });
        }

        private layoutAttachments(): wemQ.Promise<Attachments> {
            return new api.content.GetContentAttachmentsRequest(this.content.getContentId()).sendAndParse().then((attachments: Attachments) => {

                if (attachments) {
                    var uploaderList = new api.dom.UlEl("uploader-list");

                    attachments.forEach((attachment: Attachment) => {
                        var uploader = new MediaUploader({
                            params: {
                                parent: this.content.getContentId().toString()
                            },
                            operation: api.content.MediaUploaderOperation.create,
                            name: attachment.getName().toString(),
                            showReset: false,
                            showCancel: false,
                            maximumOccurrences: 1,
                            allowMultiSelection: false,
                            hideDropZone: true,
                            deferred: true
                        });

                        uploader.setValue(this.content.getContentId().toString());
                        uploader.setFileName(attachment.getName().toString());

                        var uploaderContainer = new api.dom.LiEl("uploader-container");
                        uploaderContainer.appendChild(uploader);
                        uploaderList.appendChild(uploaderContainer);

                    });

                    this.appendChild(uploaderList);

                } else {
                    this.appendChild(new api.dom.SpanEl("att-placeholder").setHtml("This item has no attachments"));
                }

                return attachments;
            });
        }
    }

}