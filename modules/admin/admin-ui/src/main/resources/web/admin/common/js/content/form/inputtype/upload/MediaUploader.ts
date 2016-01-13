module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import ContentRequiresSaveEvent = api.content.ContentRequiresSaveEvent;
    import PluploadFile = api.ui.uploader.PluploadFile;

    import MediaUploaderEl = api.content.MediaUploaderEl;
    import Content = api.content.Content;
    import UploaderEl = api.ui.uploader.UploaderEl;


    export class MediaUploader extends FileUploader {

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(config);
            this.addClass("media-uploader");
            this.config = config;
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {

            this.uploaderEl = this.createUploader(property);
            var mediaUploader = <MediaUploaderEl>this.uploaderEl;

            this.uploaderWrapper = this.createUploaderWrapper(property);

            this.updateProperty(property);

            property.onPropertyValueChanged((event: api.data.PropertyValueChangedEvent) => {
                this.updateProperty(event.getProperty(), true);
            });

            mediaUploader.onUploadStarted(() => {
                this.uploaderWrapper.removeClass("empty");
            });

            mediaUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {

                var content = event.getUploadItem().getModel(),
                    value = mediaUploader.getMediaValue(content),
                    fileName = value.getString();

                mediaUploader.setFileName(fileName);

                switch (property.getType()) {
                case ValueTypes.DATA:
                    property.getPropertySet().setProperty('attachment', 0, value);
                    break;
                case ValueTypes.STRING:
                    property.setValue(ValueTypes.STRING.newValue(fileName));
                    break;
                }

                api.notify.showFeedback('\"' + fileName + '\" uploaded');

                new ContentRequiresSaveEvent(content.getContentId()).fire();
            });

            mediaUploader.onUploadFailed(() => {
                mediaUploader.setProgressVisible(false);
                this.uploaderWrapper.addClass("empty");
            });

            mediaUploader.onUploadReset(() => {
                mediaUploader.setFileName('');

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


        protected createUploader(property: Property): UploaderEl<Content> {

            var predefinedAllowTypes,
                attachmentFileName = this.getFileNameFromProperty(property);

            if (this.propertyAlreadyHasAttachment(property)) {
                predefinedAllowTypes = this.getAllowTypeFromFileName(attachmentFileName);
            }

            var allowTypesConfig: FileUploaderConfigAllowType[] = predefinedAllowTypes || (<any>(this.config.inputConfig)).allowTypes ||
                [];
            var allowTypes = allowTypesConfig.map((allowType: FileUploaderConfigAllowType) => {
                return {title: allowType.name, extensions: allowType.extensions};
            });

            var beforeUploadCallback = (files: PluploadFile[]) => {
                if (attachmentFileName && files && files.length == 1) {
                    files[0].name = attachmentFileName;
                }
            };

            return new api.content.MediaUploaderEl({
                params: {
                    content: this.getContext().contentId.toString()
                },
                operation: api.content.MediaUploaderElOperation.update,
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

        protected getFileNameFromProperty(property: Property): string {
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

        protected propertyAlreadyHasAttachment(property: Property): boolean {
            return (property.getValue() != null &&
                    property.getType() == ValueTypes.DATA &&
                    !api.util.StringHelper.isEmpty(property.getPropertySet().getString('attachment')));
        }


        protected getAllowTypeFromFileName(fileName: string): FileUploaderConfigAllowType[] {
            return [{name: "Media", extensions: this.getFileExtensionFromFileName(fileName)}];
        }

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("MediaUploader", MediaUploader));
}