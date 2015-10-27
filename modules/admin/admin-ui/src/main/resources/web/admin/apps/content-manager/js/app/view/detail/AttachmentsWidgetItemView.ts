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

        constructor() {
            super("attachments-widget-item-view");
        }

        public setContent(content: ContentSummary) {
            this.content = content;
        }

        public layout() {
            this.removeChildren();
            if (this.content != undefined) {

                new api.content.GetContentAttachmentsRequest(this.content.getContentId()).sendAndParse().then((attachments: Attachments) => {
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

                }).done();
            }
            super.layout();
        }
    }

}