module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class FileUploader extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<any,string> {

        private fileUploader: api.content.MediaUploader;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext<any>) {
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

            if (this.getContext().contentId) {
                this.fileUploader.setValue(this.getContext().contentId.toString());
                if (property.getValue() != null) {
                    this.fileUploader.setFileName(property.getValue().getString());
                }
            }

            this.fileUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                property.setValue(ValueTypes.STRING.newValue(event.getUploadItem().getName()));
                this.fileUploader.setFileName(event.getUploadItem().getName());
            });

            this.fileUploader.onUploadReset(() => {
                property.setValue(ValueTypes.STRING.newNullValue());
                this.fileUploader.setFileName('');
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