declare var plupload;

module api.form.inputtype.content.imageupload {

    export class Image extends api.form.inputtype.support.BaseInputTypeView<any> {

        private imageUploaders: api.ui.ImageUploader[] = [];

        private attachmentName: string;

        private attachment: api.content.attachment.Attachment;

        constructor(config: api.form.inputtype.InputTypeViewConfig<any>) {
            super(config, "image");
            this.attachment = config.attachments.getAttachments().pop();
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var inputName = this.getInput().getName() + "-" + index;

            var imageUploaderConfig = <api.ui.ImageUploaderConfig> {
                showImageAfterUpload: true,
                maximumOccurrences: 1
            };
            var uploadUrl = api.util.getRestUri("blob/upload");
            var imageUploader: api.ui.ImageUploader = new api.ui.ImageUploader(inputName, uploadUrl, imageUploaderConfig);

            imageUploader.onImageUploaded((event: api.ui.ImageUploadedEvent) => {
                this.attachment = this.uploadItemToAttachment(event.getUploadedItem());
                this.attachmentName = event.getUploadedItem().getName();
            });

            imageUploader.onImageReset(() => {
                this.attachment = null;
                this.attachmentName = null;
            });

            if (property != null) {
                this.attachmentName = property.getString();
                var imageUrl = api.util.getRestUri("content/image/") + this.getConfig().contentId;
                imageUrl += "?thumbnail=false&size=494"; // TODO: size is hack
                imageUploader.setValue(imageUrl);
            }
            this.imageUploaders.push(imageUploader);
            return imageUploader;
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            return new api.data.Value(this.attachmentName, api.data.ValueTypes.STRING);
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return [this.attachment];
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            if (value == null) {
                return true;
            }

            if (api.util.isStringBlank(value.asString())) {
                return true;
            } else {
                return false;
            }
        }

        addOnValueChangedListener(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {
            //TODO: implement logic
        }

        private uploadItemToAttachment(uploadItem: api.ui.UploadItem): api.content.attachment.Attachment {
            return new api.content.attachment.AttachmentBuilder().
                setBlobKey(uploadItem.getBlobKey()).
                setAttachmentName(new api.content.attachment.AttachmentName(uploadItem.getName())).
                setMimeType(uploadItem.getMimeType()).
                setSize(uploadItem.getSize()).
                build();
        }
    }

    api.form.input.InputTypeManager.register("Image", Image);
}