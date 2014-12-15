module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class FileUploader extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<any,string> {

        private fileUploader: api.ui.uploader.FileUploader;

        private attachmentName: string;

        private attachment: api.content.attachment.Attachment;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext<any>) {
            super(config, "file");
            var input = config.input;
            this.attachment = config.attachments.getAttachment(0);

            this.fileUploader = new api.ui.uploader.FileUploader({
                name: input.getName(),
                maximumOccurrences: 1
            });

            this.appendChild(this.fileUploader);
        }

        getContext(): api.content.form.inputtype.ContentInputTypeViewContext<any> {
            return <api.content.form.inputtype.ContentInputTypeViewContext<any>>super.getContext();
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return ValueTypes.STRING.newNullValue();
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {
            debugger;
            if (property.hasNonNullValue()) {
                this.attachmentName = property.getString();
                var imgUrl = new ContentImageUrlResolver().
                    setContentId(this.getContext().contentId).
                    setSize(494).resolve();
                this.fileUploader.setValue(imgUrl);
            }

            this.fileUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.ui.uploader.UploadItem>) => {
                if (this.attachmentName == null) {
                    var uploadItem = event.getUploadItem();
                    this.attachmentName = uploadItem.getName();
                    this.attachment = this.createAttachment(uploadItem);

                    var value = new Value(this.attachmentName, ValueTypes.STRING);
                    property.setValue(value);
                }
            });

            this.fileUploader.onUploadReset(() => {
                this.attachment = null;
                this.attachmentName = null;
                property.setValue(ValueTypes.STRING.newNullValue());
            });

            return wemQ<void>(null);
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return this.attachment ? [this.attachment] : [];
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            return new api.form.inputtype.InputValidationRecording();
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.fileUploader.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.fileUploader.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.fileUploader.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.fileUploader.unBlur(listener);
        }

        private createAttachment(uploadItem: api.ui.uploader.UploadItem): api.content.attachment.Attachment {
            return new api.content.attachment.AttachmentBuilder().
                setBlobKey(uploadItem.getBlobKey()).
                setName(new api.content.attachment.AttachmentName(uploadItem.getName())).
                setMimeType(uploadItem.getMimeType()).
                setSize(uploadItem.getSize()).
                build();
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("FileUploader", FileUploader));
}