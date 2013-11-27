declare var plupload;

module api_form_inputtype_content_imageupload {

    export class Image extends api_form_inputtype_support.BaseInputTypeView {

        private imageUploaders:api_ui.ImageUploader[] = [];

        private uploadId:string;

        private name:string;

        private mimeType:string

        constructor() {
            super("Image");
            this.addClass("image");
        }

        createInputOccurrenceElement(index:number, property:api_data.Property):api_dom.Element {

            var imageUploader = new api_ui.ImageUploader(this.getInput().getName() + "-" + index, api_util.getRestUri("upload"));
            imageUploader.addListener({
                                          onFileUploaded: (id:string, name:string, mimeType:string) => {
                                              this.uploadId = id;
                                              this.name = name;
                                              this.mimeType = mimeType;
                                          },
                                          onUploadComplete: null
                                      });

            if (property != null) {
                var imageUrl = api_util.getAdminUri("rest/content/image");
                //imageUrl += "" + property.get;
                imageUploader.setValue(property.getString());
            }
            this.imageUploaders.push(imageUploader);
            return imageUploader;
        }

        getValue(occurrence:api_dom.Element):api_data.Value {

            return new api_data.Value(this.name, api_data.ValueTypes.ATTACHMENT_NAME);

        }

        getAttachments():api_content.Attachment[] {
            var attachments:api_content.Attachment[] = [];
            var attachment = new api_content.Attachment(this.uploadId, new api_content.AttachmentName(this.name));
            attachments.push(attachment);
            return attachments;
        }

        valueBreaksRequiredContract(value:api_data.Value):boolean {
            // TODO:
            return false;
        }

    }

    api_form_input.InputTypeManager.register("Image", Image);

}