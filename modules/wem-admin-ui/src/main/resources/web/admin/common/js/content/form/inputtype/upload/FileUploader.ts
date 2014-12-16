module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class FileUploader extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<any,string> {

        private fileUploader: api.content.MediaUploader;

        private attachmentName: string;

        private attachment: api.content.attachment.Attachment;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext<any>) {
            super(config, "file");
            var input = config.input;
            this.attachment = config.attachments.getAttachment(0);

            this.fileUploader = new api.content.MediaUploader({
                operation: api.content.MediaUploaderOperation.create,
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

            this.fileUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {

                if (this.attachmentName == null) {
                    this.attachment = event.getUploadItem().getAttachments().getAttachmentByLabel("source");

                    if (this.attachment) {
                        this.attachmentName = this.attachment.getName().toString();
                        var value = new Value(this.attachmentName, ValueTypes.STRING);
                        property.setValue(value);
                    }
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
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("FileUploader", FileUploader));
}