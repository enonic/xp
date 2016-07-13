module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import ContentRequiresSaveEvent = api.content.ContentRequiresSaveEvent;
    import UploaderEl = api.ui.uploader.UploaderEl
    import FileUploaderEl = api.ui.uploader.FileUploaderEl;


    export class FileUploader extends api.form.inputtype.support.BaseInputTypeManagingAdd<string> {

        protected config: api.content.form.inputtype.ContentInputTypeViewContext;
        protected uploaderEl: FileUploaderEl<any>;
        protected uploaderWrapper: api.dom.DivEl;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super("file-uploader");
            this.config = config;
        }

        getContext(): api.content.form.inputtype.ContentInputTypeViewContext {
            return this.config;
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return null;
        }

        update(propertyArray: PropertyArray, unchangedOnly?: boolean): wemQ.Promise<void> {

            var superPromise = super.update(propertyArray, unchangedOnly);
            this.uploaderEl.setContentId(this.getContext().content.getContentId().toString());

                return superPromise.then(() => {
                    this.uploaderEl.resetValues(this.getValueFromPropertyArray(propertyArray));
                    this.validate(false);
                });

        }

        protected setFileNameProperty(fileName: string) {

            var value = new Value(fileName, ValueTypes.STRING);

            if (!this.getPropertyArray().containsValue(value)) {
                this.ignorePropertyChange = true;
                this.getPropertyArray().add(value);
                this.ignorePropertyChange = false;
            }
        }

        protected getValueFromPropertyArray(propertyArray: PropertyArray): string {
            return this.getFileNamesFromProperty(propertyArray).
                join(FileUploaderEl.FILE_NAME_DELIMITER);
        }

        protected getFileNamesFromProperty(propertyArray: PropertyArray): string[] {
            return propertyArray.getProperties().map((property) => {
                if (property.hasNonNullValue()) {
                    return property.getString();
                }
            })
        }

        protected createUploaderWrapper(): api.dom.DivEl {
            var wrapper = new api.dom.DivEl("uploader-wrapper");

            var uploadButton = new api.ui.button.Button();
            uploadButton.addClass('upload-button');

            uploadButton.onClicked((event: MouseEvent) => {
                this.uploaderEl.showFileSelectionDialog();
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