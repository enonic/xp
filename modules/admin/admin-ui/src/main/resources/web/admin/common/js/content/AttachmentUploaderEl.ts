module api.content {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;
    import ValueTypes = api.data.ValueTypes;

    import Attachment = api.content.attachment.Attachment;
    import AttachmentJson = api.content.attachment.AttachmentJson;
    import AttachmentBuilder = api.content.attachment.AttachmentBuilder;
    import SelectionItem = api.app.browse.SelectionItem;


    export class AttachmentUploaderEl extends api.ui.uploader.FileUploaderEl<Attachment> {

        private attachmentItems: AttachmentItem[];

        private removeCallback: (value:string) => void;

        constructor(config) {

            if (config.url == undefined) {
                config.url = api.util.UriHelper.getRestUri("content/createAttachment");
            }
            if (config.attachmentRemoveCallback) {
                this.removeCallback = config.attachmentRemoveCallback;
            }

            this.attachmentItems = [];
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

        getModelValue(item: Attachment): string {
            return item.getName().toString();
        }

        removeAttachmentItem(value: string) {
            this.attachmentItems = this.attachmentItems.filter(
                item => !(item.getValue() == value)
            );
        }

        getExistingItem(value: string) : api.dom.Element {
            var element = null;
            this.attachmentItems.forEach((item) => {
                if(item.getValue() == value) {
                    element = item;
                }
            });
            return element;
        }

        createResultItem(value: string): api.dom.Element {

            var attachmentItem = new AttachmentItem(value, this.removeCallback);
            this.attachmentItems.push(attachmentItem);

            return attachmentItem;
        }
    }
}