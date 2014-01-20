declare var plupload;

module api.form.inputtype.content.imageupload {

    export class Image extends api.form.inputtype.support.BaseInputTypeView {

        private config: api.form.inputtype.InputTypeViewConfig<any>;

        private imageUploaders: api.ui.ImageUploader[] = [];

        private attachmentName: string;

        private attachments:api.content.attachment.Attachments;

        constructor(config: api.form.inputtype.InputTypeViewConfig<any>) {
            super("image");
            this.config = config;
            this.attachments = config.attachments;
        }

        createInputOccurrenceElement(index: number, property: api.data.Property): api.dom.Element {

            var inputName = this.getInput().getName() + "-" + index;

            var imageUploaderConfig = <api.ui.ImageUploaderConfig> {
                showImageAfterUpload: true,
                maximumOccurrences: 1
            };
            var uploadUrl = api.util.getRestUri("blob/upload");
            var imageUploader = new api.ui.ImageUploader(inputName, uploadUrl, imageUploaderConfig);
            imageUploader.addListener({
                onFileUploaded: (uploadItem: api.ui.UploadItem) => {
                    this.attachments = new api.content.attachment.AttachmentsBuilder().
                        addAll(this.attachments.getAttachments()).
                        add(this.uploadItemToAttachment(uploadItem)).build();
                    this.attachmentName = uploadItem.getName();
                },
                onUploadComplete: null
            });

            if (property != null) {
                this.attachmentName = property.getString();
                var imageUrl = api.util.getRestUri("content/image/") + this.config.contentId;
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

            return this.attachments.getAttachments();
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            if (api.util.isStringBlank(value.asString())) {
                return true;
            } else {
                return false;
            }
        }

        private uploadItemToAttachment(uploadItem:api.ui.UploadItem) : api.content.attachment.Attachment {
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