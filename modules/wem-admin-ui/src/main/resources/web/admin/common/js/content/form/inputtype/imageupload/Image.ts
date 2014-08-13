declare var plupload;

module api.content.form.inputtype.imageupload {

    export class Image extends api.form.inputtype.support.BaseInputTypeView<any> {

        private imageUploadersByIndex: {[i:number] : api.ui.uploader.ImageUploader;} = {};

        private attachmentName: string;

        private attachment: api.content.attachment.Attachment;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext<any>) {
            super(config, "image");

            var attachments: api.content.attachment.Attachment[] = config.attachments.getAttachments();

            if (attachments.length > 1) {
                throw new Error("Expected max one attachment for Image content, actual " + (attachments.length));
            }

            this.attachment = attachments.pop();
        }

        getConfig(): api.content.form.inputtype.ContentInputTypeViewContext<any> {
            return <api.content.form.inputtype.ContentInputTypeViewContext<any>>super.getConfig();
        }

        newInitialValue(): api.data.Value {
            return null;
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var inputName = this.getInput().getName() + "-" + index;

            var imageUploaderConfig = <api.ui.uploader.ImageUploaderConfig> {
                showImageAfterUpload: true,
                maximumOccurrences: 1
            };
            var uploadUrl = api.util.getRestUri("blob/upload");
            var imageUploader: api.ui.uploader.ImageUploader = new api.ui.uploader.ImageUploader(inputName, uploadUrl, imageUploaderConfig);

            imageUploader.onImageUploaded((event: api.ui.uploader.ImageUploadedEvent) => {
                if (this.attachmentName == null) {
                    this.attachmentName = event.getUploadedItem().getName();
                    this.attachment = this.uploadItemToAttachment(event.getUploadedItem());

                    var value = new api.data.Value(this.attachmentName, api.data.ValueTypes.STRING);
                    this.notifyValueAdded(new api.form.inputtype.ValueAddedEvent(value));
                }
            });

            imageUploader.onImageReset(() => {
                this.attachment = null;
                this.attachmentName = null;
                this.notifyValueRemoved(new api.form.inputtype.ValueRemovedEvent(index));
            });

            if (property != null) {
                this.attachmentName = property.getString();
                var imageUrl = api.util.getRestUri("content/image/") + this.getConfig().contentId;
                imageUrl += "?thumbnail=false&size=494"; // TODO: size is hack
                imageUploader.setValue(imageUrl);
            }
            this.imageUploadersByIndex[index] = imageUploader;
            return imageUploader;
        }

        getValue(occurrence: api.dom.Element): api.data.Value {
            if (!this.attachmentName) {
                return null;
            }
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

        onOccurrenceValueChanged(element: api.dom.Element, listener: (event: api.form.inputtype.support.ValueChangedEvent) => void) {

            var imageUploader = <api.ui.uploader.ImageUploader>element;
            imageUploader.onImageUploaded((event: api.ui.uploader.ImageUploadedEvent) => {
                var attachmentName = event.getUploadedItem().getName();
                var value = new api.data.Value(attachmentName, api.data.ValueTypes.STRING);
                var valueChangedEvent = new api.form.inputtype.support.ValueChangedEvent(value);
                listener(valueChangedEvent);
            });
            imageUploader.onImageReset(() => {
                var value = new api.data.Value("", api.data.ValueTypes.STRING);
                var valueChangedEvent = new api.form.inputtype.support.ValueChangedEvent(value);
                listener(valueChangedEvent);
            })
        }

        private uploadItemToAttachment(uploadItem: api.ui.uploader.UploadItem): api.content.attachment.Attachment {
            return new api.content.attachment.AttachmentBuilder().
                setBlobKey(uploadItem.getBlobKey()).
                setAttachmentName(new api.content.attachment.AttachmentName(uploadItem.getName())).
                setMimeType(uploadItem.getMimeType()).
                setSize(uploadItem.getSize()).
                build();
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("Image", Image));
}