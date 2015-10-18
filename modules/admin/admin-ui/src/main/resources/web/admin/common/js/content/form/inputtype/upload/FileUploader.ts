module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import ContentRequiresSaveEvent = api.content.ContentRequiresSaveEvent;
    import PluploadFile = api.ui.uploader.PluploadFile;

    export interface FileUploaderConfigAllowType {
        name: string;
        extensions: string;
    }

    export class FileUploader extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<string> {
        private config: api.content.form.inputtype.ContentInputTypeViewContext;
        private uploader: api.content.MediaUploader;
        private uploaderWrapper: api.dom.DivEl;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(config, "file-uploader");
            this.config = config;
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

            var fileName = this.getFileNameFromProperty(property);

            if (this.propertyAlreadyHasAttachment(property)) {
                this.uploader = this.createUploader(fileName, this.getAllowTypeFromFileName(fileName));
            } else {
                this.uploader = this.createUploader(fileName);
            }

            this.uploaderWrapper = this.createUploaderWrapper(property);

            if (this.getContext().contentId) {
                this.uploader.setValue(this.getContext().contentId.toString());
                if (property.getValue() != null) {
                    this.uploader.setFileName(this.getFileNameFromProperty(property));
                }
            }

            this.uploader.onUploadStarted(() => {
                this.uploaderWrapper.removeClass("empty");
            });

            this.uploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {

                var content = event.getUploadItem().getModel(),
                    value = this.uploader.getMediaValue(content),
                    fileName = value.getString();

                this.uploader.setFileName(fileName);

                switch (property.getType()) {
                case ValueTypes.DATA:
                    property.getPropertySet().setProperty('attachment', 0, value);
                    break;
                case ValueTypes.STRING:
                    property.setValue(ValueTypes.STRING.newValue(fileName));
                    break;
                }

                api.notify.showFeedback('\"' + fileName + '\" uploaded');

                new ContentRequiresSaveEvent(content).fire();
            });

            this.uploader.onUploadFailed(() => {
                this.uploader.setProgressVisible(false);
                this.uploaderWrapper.addClass("empty");
            });

            this.uploader.onUploadReset(() => {
                this.uploader.setFileName('');

                switch (property.getType()) {
                case ValueTypes.DATA:
                    property.getPropertySet().setProperty('attachment', 0, ValueTypes.STRING.newNullValue());
                    break;
                case ValueTypes.STRING:
                    property.setValue(ValueTypes.STRING.newNullValue());
                    break;
                }
            });

            this.appendChild(this.uploaderWrapper);

            return wemQ<void>(null);
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            return new api.form.inputtype.InputValidationRecording();
        }

        private getFileNameFromProperty(property: Property): string {
            if (property.getValue() != null) {
                switch (property.getType()) {
                case ValueTypes.DATA:
                    return property.getPropertySet().getString('attachment');
                case ValueTypes.STRING:
                    return property.getValue().getString();
                }
            }
            return "";
        }

        private getFileExtensionFromFileName(fileName: string): string {
            return fileName.split('.').pop();
        }

        private propertyAlreadyHasAttachment(property: Property): boolean {
            return (property.getValue() != null &&
                    property.getType() == ValueTypes.DATA &&
                    !api.util.StringHelper.isEmpty(property.getPropertySet().getString('attachment')));
        }

        private getAllowTypeFromFileName(fileName: string): FileUploaderConfigAllowType[] {
            return [{name: "Media", extensions: this.getFileExtensionFromFileName(fileName)}];
        }

        private createUploaderWrapper(property: Property): api.dom.DivEl {
            var wrapper = new api.dom.DivEl("uploader-wrapper");

            var uploadButton = new api.ui.button.Button();
            uploadButton.addClass('upload-button');

            uploadButton.onClicked((event: MouseEvent) => {
                if (property.hasNullValue()) {
                    return;
                }
                wemjq(this.uploader.getDropzone().getEl().getHTMLElement()).simulate("click");
            });

            wrapper.appendChild(this.uploader);
            wrapper.appendChild(uploadButton);

            return wrapper;
        }

        private createUploader(attachmentFileName: string,
                               predefinedAllowTypes?: FileUploaderConfigAllowType[]): api.content.MediaUploader {
            var allowTypesConfig: FileUploaderConfigAllowType[] = predefinedAllowTypes || (<any>(this.config.inputConfig)).allowTypes || [];
            var allowTypes = allowTypesConfig.map((allowType: FileUploaderConfigAllowType) => {
                return {title: allowType.name, extensions: allowType.extensions};
            });

            var beforeUploadCallback = (files: PluploadFile[]) => {
                if (attachmentFileName && files && files.length == 1) {
                    files[0].name = attachmentFileName;
                }
            };

            return new api.content.MediaUploader({
                params: {
                    content: this.getContext().contentId.toString()
                },
                operation: api.content.MediaUploaderOperation.update,
                allowTypes: allowTypes,
                name: this.getContext().input.getName(),
                showReset: false,
                showCancel: false,
                maximumOccurrences: 1,
                allowMultiSelection: false,
                hideDropZone: !!(<any>(this.config.inputConfig)).hideDropZone,
                deferred: true,
                beforeUploadCallback: beforeUploadCallback
            });
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.uploader.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.uploader.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.uploader.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.uploader.unBlur(listener);
        }
    }
    api.form.inputtype.InputTypeManager.register(new api.Class("FileUploader", FileUploader));
}