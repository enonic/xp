module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import ContentRequiresSaveEvent = api.content.ContentRequiresSaveEvent;
    import PluploadFile = api.ui.uploader.PluploadFile;
    import UploaderEl = api.ui.uploader.UploaderEl;


    export interface FileUploaderConfigAllowType {
        name: string;
        extensions: string;
    }

    export class FileUploader extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<string> {

        protected config: api.content.form.inputtype.ContentInputTypeViewContext;
        protected uploaderEl: UploaderEl<any>;
        protected uploaderWrapper: api.dom.DivEl;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(config);
            this.config = config;
            this.addClass("file-uploader");
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

            throw new Error("must be implemented in inheritors");
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            return new api.form.inputtype.InputValidationRecording();
        }

        updateProperty(property: Property, unchangedOnly?: boolean): wemQ.Promise<void> {
            if ((!unchangedOnly || !this.uploaderEl.isDirty()) && this.getContext().contentId) {

                this.uploaderEl.setValue(this.getContext().contentId.toString());

                if (property.hasNonNullValue()) {
                    (<MediaUploaderEl>this.uploaderEl).setFileName(this.getFileNameFromProperty(property));
                }
            }
            return wemQ<void>(null);
        }


        protected getFileNameFromProperty(property: Property): string {
            throw new Error("must be implemented in inheritors");
        }

        protected getFileExtensionFromFileName(fileName: string): string {
            return fileName.split('.').pop();
        }


        protected createUploaderWrapper(property: Property): api.dom.DivEl {
            var wrapper = new api.dom.DivEl("uploader-wrapper");

            var uploadButton = new api.ui.button.Button();
            uploadButton.addClass('upload-button');

            uploadButton.onClicked((event: MouseEvent) => {
                wemjq(this.uploaderEl.getDropzone().getEl().getHTMLElement()).simulate("click");
            });

            wrapper.appendChild(this.uploaderEl);
            wrapper.appendChild(uploadButton);

            return wrapper;
        }

        protected createUploader(property: Property): UploaderEl<any> {
            throw new Error("must be implemented in inheritors");
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.uploaderEl.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.uploaderEl.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.uploaderEl.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.uploaderEl.unBlur(listener);
        }
    }
}