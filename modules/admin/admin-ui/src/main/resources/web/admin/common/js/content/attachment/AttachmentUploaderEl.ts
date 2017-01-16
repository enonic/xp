module api.content.attachment {

    import Button = api.ui.button.Button;
    import CloseButton = api.ui.button.CloseButton;
    import ValueTypes = api.data.ValueTypes;

    import Attachment = api.content.attachment.Attachment;
    import AttachmentJson = api.content.attachment.AttachmentJson;
    import AttachmentBuilder = api.content.attachment.AttachmentBuilder;
    import SelectionItem = api.app.browse.SelectionItem;

    export class AttachmentUploaderEl extends api.ui.uploader.FileUploaderEl<Attachment> {

        private attachmentItems: AttachmentItem[];

        private removeCallback: (value: string) => void;
        private addCallback: (value: string) => void;

        constructor(config: any) {

            if (config.url == undefined) {
                config.url = api.util.UriHelper.getRestUri('content/createAttachment');
            }
            if (config.selfIsDropzone == undefined) {
                config.selfIsDropzone = true;
            }

            super(config);

            this.attachmentItems = [];

            if (config.attachmentRemoveCallback) {
                this.removeCallback = config.attachmentRemoveCallback;
            }

            if (config.attachmentAddCallback) {
                this.addCallback = config.attachmentAddCallback;
            }

            this.addClass('attachment-uploader-el');
        }

        createModel(serverResponse: AttachmentJson): Attachment {
            if (serverResponse) {
                return new AttachmentBuilder().fromJson(serverResponse).build();
            } else {
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

        getExistingItem(value: string): api.dom.Element {
            let element = null;
            this.getResultContainer().getChildren().forEach((item) => {
                if ((<AttachmentItem>item).getValue() == value) {
                    element = item;
                }
            });
            return element;
        }

        createResultItem(value: string): api.dom.Element {

            let attachmentItem = new AttachmentItem(this.contentId, value, this.removeCallback);
            this.attachmentItems.push(attachmentItem);

            if (this.addCallback) {
                this.addCallback(attachmentItem.getValue());
            }

            return attachmentItem;
        }

        maximumOccurrencesReached(): boolean {
            if (this.config.maximumOccurrences) {
                return this.attachmentItems.length >= this.config.maximumOccurrences;
            }
            return false;
        }
    }
}
