module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class FileUploader extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<string> {

        private fileUploader: api.content.MediaUploader;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(config, "file");
            var input = config.input;

            this.fileUploader = new api.content.MediaUploader({
                params: {
                    content: config.contentId.toString()
                },
                operation: api.content.MediaUploaderOperation.update,
                name: input.getName(),
                maximumOccurrences: 1
            });

            this.appendChild(this.fileUploader);
        }

        getContext(): api.content.form.inputtype.ContentInputTypeViewContext {
            return <api.content.form.inputtype.ContentInputTypeViewContext>super.getContext();
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return ValueTypes.STRING.newNullValue();
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {

            if (this.getContext().contentId) {
                this.fileUploader.setValue(this.getContext().contentId.toString());
                if (property.getValue() != null) {
                    switch (property.getType()) {
                    case ValueTypes.DATA:
                        var attachmentName = property.getPropertySet().getString('attachment');
                        this.fileUploader.setFileName(attachmentName);
                        break;
                    case ValueTypes.STRING:
                        this.fileUploader.setFileName(property.getValue().getString());
                        break;
                    }
                }
            }

            this.fileUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                var fileName = event.getUploadItem().getName();
                this.fileUploader.setFileName(fileName);

                var fileNameValue = ValueTypes.STRING.newValue(fileName);
                switch (property.getType()) {
                case ValueTypes.DATA:
                    property.getPropertySet().setProperty('attachment', 0, fileNameValue);
                    break;
                case ValueTypes.STRING:
                    property.setValue(fileNameValue);
                    break;
                }
            });

            this.fileUploader.onUploadReset(() => {
                this.fileUploader.setFileName('');

                switch (property.getType()) {
                case ValueTypes.DATA:
                    property.getPropertySet().setProperty('attachment', 0, ValueTypes.STRING.newNullValue());
                    break;
                case ValueTypes.STRING:
                    property.setValue(ValueTypes.STRING.newNullValue());
                    break;
                }
            });

            return wemQ<void>(null);
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