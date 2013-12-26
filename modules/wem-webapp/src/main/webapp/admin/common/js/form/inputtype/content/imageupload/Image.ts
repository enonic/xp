declare var plupload;

module api_form_inputtype_content_imageupload {

    export class Image extends api_form_inputtype_support.BaseInputTypeView {

        private config: api_form_inputtype.InputTypeViewConfig<any>;

        private imageUploaders: api_ui.ImageUploader[] = [];

        private attachmentName: string;

        private attachments:api_content_attachment.Attachments;

        constructor(config: api_form_inputtype.InputTypeViewConfig<any>) {
            super("Image");
            this.addClass("image");
            this.config = config;
            this.attachments = config.attachments;
        }

        createInputOccurrenceElement(index: number, property: api_data.Property): api_dom.Element {

            var inputName = this.getInput().getName() + "-" + index;

            var imageUploaderConfig = <api_ui.ImageUploaderConfig> {
                showImageAfterUpload: true,
                maximumOccurrences: 1
            };
            var uploadUrl = api_util.getRestUri("blob/upload");
            var imageUploader = new api_ui.ImageUploader(inputName, uploadUrl, imageUploaderConfig);
            imageUploader.addListener({
                onFileUploaded: (uploadItem: api_ui.UploadItem) => {
                    this.attachments = new api_content_attachment.AttachmentsBuilder().
                        addAll(this.attachments.getAttachments()).
                        add(this.uploadItemToAttachment(uploadItem)).build();
                    this.attachmentName = uploadItem.getName();
                },
                onUploadComplete: null
            });

            if (property != null) {
                this.attachmentName = property.getString();
                var imageUrl = api_util.getRestUri("content/image/") + this.config.contentId;
                imageUrl += "?thumbnail=false&size=494"; // TODO: size is hack
                imageUploader.setValue(imageUrl);
            }
            this.imageUploaders.push(imageUploader);
            return imageUploader;
        }

        getValue(occurrence: api_dom.Element): api_data.Value {

            return new api_data.Value(this.attachmentName, api_data.ValueTypes.STRING);
        }

        getAttachments(): api_content_attachment.Attachment[] {

            return this.attachments.getAttachments();
        }

        valueBreaksRequiredContract(value: api_data.Value): boolean {
            // TODO:
            return false;
        }

        private uploadItemToAttachment(uploadItem:api_ui.UploadItem) : api_content_attachment.Attachment {
             return new api_content_attachment.AttachmentBuilder().
                setBlobKey(uploadItem.getBlobKey()).
                setAttachmentName(new api_content_attachment.AttachmentName(uploadItem.getName())).
                setMimeType(uploadItem.getMimeType()).
                setSize(uploadItem.getSize()).
                build();
        }

    }

    api_form_input.InputTypeManager.register("Image", Image);

}