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
    import Attachment = api.content.attachment.Attachment;
    import UploaderEl = api.ui.uploader.UploaderEl;
    import FileUploaderEl = api.ui.uploader.FileUploaderEl;


    export class AttachmentUploader extends FileUploader {

        private attachmentNames: string[] = [];

        private attachmentUploader: AttachmentUploaderEl;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(config);
            this.addClass("attachment-uploader");
            this.config = config;
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {

            this.uploaderEl = this.createUploader(property);
            this.attachmentUploader = <AttachmentUploaderEl>this.uploaderEl;

            this.uploaderWrapper = this.createUploaderWrapper(property);

            this.updateProperty(property);

            this.attachmentUploader.onUploadStarted(() => {
                this.uploaderWrapper.removeClass("empty");
                this.attachmentNames = this.getProperty().getValue().isNotNull() ? this.getProperty().getString().split(FileUploaderEl.FILE_NAME_DELIMITER) : [];
            });

            this.attachmentUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<Attachment>) => {

                var attachment = <Attachment>event.getUploadItem().getModel();
                this.attachmentNames.push(attachment.getName().toString());

                api.notify.showFeedback('\"' + attachment.getName().toString() + '\" uploaded');
            });

            this.attachmentUploader.onUploadCompleted(() => {

                var unicalAttachmentNames = [];
                this.attachmentNames.forEach(attachmentName => {
                    if(unicalAttachmentNames.indexOf(attachmentName) == -1 ) {
                        unicalAttachmentNames.push(attachmentName);
                    }
                });

                var newValue = ValueTypes.STRING.newValue(unicalAttachmentNames.join(FileUploaderEl.FILE_NAME_DELIMITER));
                this.getProperty().setValue(newValue);

                new ContentRequiresSaveEvent(this.getContext().contentId).fire();
            });

            this.attachmentUploader.onUploadFailed(() => {
                this.attachmentUploader.setProgressVisible(false);
                this.uploaderWrapper.addClass("empty");
            });

            this.attachmentUploader.onUploadReset(() => {
                this.attachmentUploader.setValue("");
                this.getProperty().setValue(ValueTypes.STRING.newNullValue());


            });

            this.appendChild(this.uploaderWrapper);

            return wemQ<void>(null);
        }

        protected getFileNamesFromProperty(property: Property): string[] {

            return property.getValue().getString() ? property.getValue().getString().split(FileUploaderEl.FILE_NAME_DELIMITER) : [];
        }


        protected createUploader(property: Property): UploaderEl<any> {

            var attachmentFileNames = this.getFileNamesFromProperty(property);

            var beforeUploadCallback = (files: PluploadFile[]) => {
                if (attachmentFileNames && files && files.length == 1) {
                    files[0].name = attachmentFileNames[0];
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
                allowMultiSelection: true,
                hideDropZone: !!(<any>(this.config.inputConfig)).hideDropZone,
                deferred: true,
                attachmentRemoveCallback: this.removeItem.bind(this)
            });
        }

        private removeItem(itemName: string) {
            var values = this.getProperty().getValue().getString().split(FileUploaderEl.FILE_NAME_DELIMITER);

            var index = values.indexOf(itemName);
            values.splice(index, 1);

            this.attachmentUploader.removeAttachmentItem(itemName);
            this.getProperty().setValue(ValueTypes.STRING.newValue(values.join(FileUploaderEl.FILE_NAME_DELIMITER)));
        }

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("AttachmentUploader", AttachmentUploader));
}