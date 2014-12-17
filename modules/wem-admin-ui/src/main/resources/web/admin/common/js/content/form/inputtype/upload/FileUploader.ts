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
            if (property.hasNonNullValue()) {
                var imgUrl = new ContentImageUrlResolver().
                    setContentId(this.getContext().contentId).
                    setSize(494).resolve();
                this.fileUploader.setValue(imgUrl);
            }

            this.fileUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {

            });

            this.fileUploader.onUploadReset(() => {
                property.setValue(ValueTypes.STRING.newNullValue());
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