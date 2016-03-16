module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
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

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(config);
            this.addClass("attachment-uploader");
            this.config = config;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {
            if (!ValueTypes.STRING.equals(propertyArray.getType())) {
                propertyArray.convertValues(ValueTypes.STRING);
            }

            return super.layout(input, propertyArray).then(() => {
                this.uploaderEl = this.createUploader();
                this.uploaderWrapper = this.createUploaderWrapper();

                this.update(propertyArray);

                this.uploaderEl.onUploadStarted(() => {
                    this.uploaderWrapper.removeClass("empty");
                });

                this.uploaderEl.onFileUploaded((event:api.ui.uploader.FileUploadedEvent<Attachment>) => {

                    var attachment = <Attachment>event.getUploadItem().getModel();

                    this.setFileNameProperty(attachment.getName().toString());
                    this.attachmentNames = this.getFileNamesFromProperty(this.getPropertyArray());

                    api.notify.showFeedback('\"' + attachment.getName().toString() + '\" uploaded');
                });

                this.uploaderEl.onUploadCompleted(() => {

                    this.validate(false);
                    new ContentRequiresSaveEvent(this.getContext().contentId).fire();

                });

                this.uploaderEl.onUploadFailed(() => {
                    this.uploaderEl.setProgressVisible(false);
                    this.uploaderWrapper.addClass("empty");
                });

                this.appendChild(this.uploaderWrapper);

                this.setLayoutInProgress(false);
                this.validate(false);

                return wemQ<void>(null);
            });
        }

        protected getNumberOfValids(): number {
            return this.getPropertyArray().getProperties().length;
        }

        protected createUploader(): FileUploaderEl<any> {

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
                maximumOccurrences: this.getInput().getOccurrences().getMaximum(),
                attachmentRemoveCallback: this.removeItem.bind(this)
            });
        }

        private removeItem(itemName: string) {
            var values = this.getFileNamesFromProperty(this.getPropertyArray());

            var index = values.indexOf(itemName);
            values.splice(index, 1);

            (<AttachmentUploaderEl>this.uploaderEl).removeAttachmentItem(itemName);
            this.getPropertyArray().remove(index);
            this.attachmentNames = this.getFileNamesFromProperty(this.getPropertyArray());

            new ContentRequiresSaveEvent(this.getContext().contentId).fire();
        }

    }
    api.form.inputtype.InputTypeManager.register(new api.Class("AttachmentUploader", AttachmentUploader));
}