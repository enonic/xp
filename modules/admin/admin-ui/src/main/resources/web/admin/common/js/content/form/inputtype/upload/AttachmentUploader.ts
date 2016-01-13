module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import ContentRequiresSaveEvent = api.content.ContentRequiresSaveEvent;
    import PluploadFile = api.ui.uploader.PluploadFile;

    import AttachmentUploaderEl = api.content.AttachmentUploaderEl;
    import Content = api.content.Content;
    import UploaderEl = api.ui.uploader.UploaderEl;


    export class AttachmentUploader extends FileUploader {

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(config);
            this.addClass("attachment-uploader");
            this.config = config;
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {

            this.uploaderEl = this.createUploader(property);
            var attachmentUploader = <AttachmentUploaderEl>this.uploaderEl;

            this.uploaderWrapper = this.createUploaderWrapper(property);

            this.updateProperty(property);

            property.onPropertyValueChanged((event: api.data.PropertyValueChangedEvent) => {
                this.updateProperty(event.getProperty(), true);
            });

            attachmentUploader.onUploadStarted(() => {
                this.uploaderWrapper.removeClass("empty");
            });

            attachmentUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<Attachment>) => {

                var attachment = event.getUploadItem().getModel();
                attachmentUploader.setFileName(attachment.getName().toString());
                property.setValue(ValueTypes.STRING.newValue(attachment.getName().toString()));

                api.notify.showFeedback('\"' + attachment.getName().toString() + '\" uploaded');
                new ContentRequiresSaveEvent(this.getContext().contentId).fire();

            });

            attachmentUploader.onUploadFailed(() => {
                attachmentUploader.setProgressVisible(false);
                this.uploaderWrapper.addClass("empty");
            });

            attachmentUploader.onUploadReset(() => {
                attachmentUploader.setFileName('');
                property.setValue(ValueTypes.STRING.newNullValue());


            });

            this.appendChild(this.uploaderWrapper);

            return wemQ<void>(null);
        }

        protected getFileNameFromProperty(property: Property): string {
            return property.getValue().getString();
        }


        protected createUploader(property: Property): UploaderEl<Content> {

            var attachmentFileName = this.getFileNameFromProperty(property);


            var beforeUploadCallback = (files: PluploadFile[]) => {
                if (attachmentFileName && files && files.length == 1) {
                    files[0].name = attachmentFileName;
                }
            };

            return new api.content.AttachmentUploaderEl({
                params: {
                    id: this.getContext().contentId.toString()
                },
                operation: api.content.MediaUploaderElOperation.update,
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

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("AttachmentUploader", AttachmentUploader));
}