module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;

    export interface FileUploaderConfigAllowType {
        name: string;
        extensions: string;
    }

    export interface FileUploaderConfig {
        hideDropZone: boolean;
        allowTypes: FileUploaderConfigAllowType[];
    }

    export class FileUploader extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<any,string> {
        private config: api.content.form.inputtype.ContentInputTypeViewContext<FileUploaderConfig>;
        private uploader: api.content.MediaUploader;
        private uploaderWrapper: api.dom.DivEl;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext<FileUploaderConfig>) {
            super(config, "file-uploader");
            this.config = config;
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

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            return new api.form.inputtype.InputValidationRecording();
        }

        private createUploaderWrapper(property: Property): api.dom.DivEl {
            var wrapper = new api.dom.DivEl("uploader-wrapper");
            var linkEl = this.createLinkEl();

            var removeButton = new api.dom.AEl("remove-button icon-close");

            removeButton.onClicked((event: MouseEvent) => {
                if (property.hasNullValue()) {
                    return;
                }

                this.deleteContent(property);
            });

            wrapper.toggleClass("empty", !property.hasNonNullValue());

            wrapper.appendChild(linkEl);
            wrapper.appendChild(this.uploader);
            wrapper.appendChild(removeButton);

            return wrapper;
        }

        private createLinkEl(): api.dom.AEl {
            var linkEl = new api.dom.AEl("upload-text");
            linkEl.getEl().setInnerHtml("Click here to upload a file");
            linkEl.onClicked(() => {
                wemjq(this.uploader.getDropzone().getEl().getHTMLElement()).simulate("click");
            });

            return linkEl;
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {
            this.uploader = this.createUploader();
            this.uploaderWrapper = this.createUploaderWrapper(property);

            if (property.hasNonNullValue()) {
                this.showFileName(property);
            }

            this.uploader.onUploadStarted(() => {
                this.uploaderWrapper.removeClass("empty");
            });

            this.uploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                var content = event.getUploadItem().getModel();
                this.uploader.setFileName(content.getName().toString());

                api.notify.showFeedback('\"' + content.getName().toString() + '\" uploaded');
            });

            this.uploader.onUploadCompleted((event: api.ui.uploader.FileUploadCompleteEvent<api.content.Content>) => {
                var content = event.getUploadItems()[0].getModel();
                property.setValue(new api.data.Value(content.getId().toString(), ValueTypes.STRING));
            });

            this.uploader.onUploadFailed(() => {
                this.uploader.setProgressVisible(false);
                this.uploaderWrapper.addClass("empty");
            });

            this.appendChild(this.uploaderWrapper);

            return wemQ<void>(null);
        }

        private showFileName(property: Property) {
            var contentId = new api.content.ContentId(property.getString());

            new api.content.GetContentByIdRequest(contentId).
                sendAndParse().
                then((content: api.content.Content) => {
                    this.uploader.setFileName(content.getName().toString());
                    this.uploader.setValue(content.getId());

                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                    this.uploaderWrapper.addClass("empty");
                }).done();
        }

        private deleteContent(property: Property) {
            var contentId = new api.content.ContentId(property.getString());

            new api.content.GetContentByIdRequest(contentId).
                sendAndParse().
                then((content: api.content.Content) => {
                    var deleteRequest = new api.content.DeleteContentRequest();

                    deleteRequest.addContentPath(content.getPath());
                    deleteRequest.sendAndParse().then((result: api.content.DeleteContentResult) => {
                        this.uploader.getResultContainer().removeChildren();
                        this.uploaderWrapper.addClass("empty");
                        property.setValue(this.newInitialValue());

                        api.notify.showFeedback('\"' + result.getDeleted()[0].getName() + '\" deleted');
                    }).catch((reason: any) => {
                        if (reason && reason.message) {
                            api.notify.showError(reason.message);
                        } else {
                            api.notify.showError('Content could not be deleted.');
                        }
                    }).done();
                });
        }

        private createUploader(): api.content.MediaUploader {
            var allowTypes = this.config.inputConfig.allowTypes.map((allowType) => {
                return {title: allowType.name, extensions: allowType.extensions};
            });
            return new api.content.MediaUploader({
                params: {
                    parent: this.getContext().contentId.toString()
                },
                operation: api.content.MediaUploaderOperation.create,
                allowTypes: allowTypes,
                name: this.getContext().input.getName(),
                showReset: false,
                showCancel: false,
                maximumOccurrences: 1,
                allowMultiSelection: false,
                hideDropZone: !!this.config.inputConfig.hideDropZone,
                deferred: true
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