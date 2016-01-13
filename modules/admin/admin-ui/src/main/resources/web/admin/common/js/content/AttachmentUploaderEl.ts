module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;
    import ValueTypes = api.data.ValueTypes;

    import Attachment = api.content.attachment.Attachment;
    import AttachmentJson = api.content.attachment.AttachmentJson;
    import AttachmentBuilder = api.content.attachment.AttachmentBuilder;


    export class AttachmentUploaderEl extends api.ui.uploader.UploaderEl<Attachment> {

        protected fileName: string;
        protected contentId: string;

        constructor(config) {

            if (config.url == undefined) {
                config.url = api.util.UriHelper.getRestUri("content/createAttachment");
            }

            super(config);

            this.addClass('attachment-uploader-el');
        }


        createModel(serverResponse: AttachmentJson): Attachment {
            if (serverResponse) {
                return new AttachmentBuilder().
                    fromJson(serverResponse).
                    build();
            }
            else {
                return null;
            }
        }

        getModelValue(): string {
            return this.contentId;
        }

        setFileName(name: string) {
            this.fileName = name;
        }

        createResultItem(contentId: string): api.dom.Element {
            this.contentId = contentId;
            var link = new api.dom.AEl().setUrl(api.util.UriHelper.getRestUri('content/media/' + contentId+'/'+this.fileName));
            return link.setHtml(this.fileName != null && this.fileName != "" ? this.fileName : contentId);
        }
    }
}