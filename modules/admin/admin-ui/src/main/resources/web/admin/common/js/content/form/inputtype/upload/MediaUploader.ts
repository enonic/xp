module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import ContentRequiresSaveEvent = api.content.ContentRequiresSaveEvent;
    import PluploadFile = api.ui.uploader.PluploadFile;

    export interface MediaUploaderConfigAllowType {
        name: string;
        extensions: string;
    }

    export class MediaUploader extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<string> {
        private config: api.content.form.inputtype.ContentInputTypeViewContext;
        private mediaUploaderEl: api.content.MediaUploaderEl;
        private uploaderWrapper: api.dom.DivEl;
        private svgImage: api.dom.ImgEl;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(config, "media-uploader");
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
            if (!ValueTypes.STRING.equals(property.getType()) && !ValueTypes.DATA.equals(property.getType())) {
                property.convertValueType(ValueTypes.STRING);
            }
            this.mediaUploaderEl = this.createUploader(property);

            this.uploaderWrapper = this.createUploaderWrapper(property);

            this.updateProperty(property);

            this.mediaUploaderEl.onUploadStarted(() => {
                this.uploaderWrapper.removeClass("empty");
            });

            this.mediaUploaderEl.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {

                var content = event.getUploadItem().getModel(),
                    value = this.mediaUploaderEl.getMediaValue(content),
                    fileName = value.getString();

                this.mediaUploaderEl.setFileName(fileName);

                switch (property.getType()) {
                    case ValueTypes.DATA:
                        property.getPropertySet().setProperty('attachment', 0, value);
                        break;
                    case ValueTypes.STRING:
                        property.setValue(ValueTypes.STRING.newValue(fileName));
                        break;
                }

                api.notify.showFeedback('\"' + fileName + '\" uploaded');

                this.manageSVGImageIfPresent(content);

                new ContentRequiresSaveEvent(content.getContentId()).fire();
            });

            this.mediaUploaderEl.onUploadFailed(() => {
                this.mediaUploaderEl.setProgressVisible(false);
                this.uploaderWrapper.addClass("empty");
            });

            this.mediaUploaderEl.onUploadReset(() => {
                this.mediaUploaderEl.setFileName('');

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

            this.createSvgImageWrapperIfNeeded();

            return wemQ<void>(null);
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            return new api.form.inputtype.InputValidationRecording();
        }

        updateProperty(property: Property, unchangedOnly?: boolean): wemQ.Promise<void> {
            if ((!unchangedOnly || !this.mediaUploaderEl.isDirty()) && this.getContext().contentId) {

                this.mediaUploaderEl.setValue(this.getContext().contentId.toString());

                if (property.hasNonNullValue()) {
                    this.mediaUploaderEl.setFileName(this.getFileNameFromProperty(property));
                }
            }
            return wemQ<void>(null);
        }

        private manageSVGImageIfPresent(content: api.content.Content) {
            if (content.getType().isVectorMedia()) {
                this.addClass("with-svg-image");
                var imgUrl = new api.content.ContentImageUrlResolver().
                    setContentId(this.getContext().contentId).
                    setTimestamp(content.getModifiedTime()).
                    resolve();

                this.svgImage.setSrc(imgUrl);
            } else {
                this.removeClass("with-svg-image");
            }
        }

        private deleteContent(property: Property) {
            var contentId = this.getContext().contentId;

            new api.content.GetContentByIdRequest(contentId).
            sendAndParse().
            then((content: api.content.Content) => {
                var deleteRequest = new api.content.DeleteContentRequest();

                deleteRequest.addContentPath(content.getPath());
                deleteRequest.sendAndParse().then((result: api.content.DeleteContentResult) => {
                    this.mediaUploaderEl.getResultContainer().removeChildren();
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

        private getAllowTypeFromFileName(fileName: string): MediaUploaderConfigAllowType[] {
            return [{name: "Media", extensions: this.getFileExtensionFromFileName(fileName)}];
        }

        private createSvgImageWrapperIfNeeded() {
            if (this.config.formContext.getContentTypeName().isVectorMedia()) {
                this.svgImage = new api.dom.ImgEl();
                this.addClass("with-svg-image");

                var content = this.config.formContext.getPersistedContent();

                var imgUrl = new api.content.ContentImageUrlResolver().
                    setContentId(this.getContext().contentId).
                    setTimestamp(content.getModifiedTime()).
                    resolve();

                this.svgImage.setSrc(imgUrl);

                this.appendChild(new api.dom.DivEl("svg-image-wrapper").appendChild(this.svgImage));

                this.svgImage.onLoaded((event: UIEvent) => {
                    this.mediaUploaderEl.setResultVisible(true); // need to call it manually as svg images are uploaded too quickly
                });
            }
        }

        private createUploaderWrapper(property: Property): api.dom.DivEl {
            var wrapper = new api.dom.DivEl("uploader-wrapper");

            var uploadButton = new api.ui.button.Button();
            uploadButton.addClass('upload-button');

            uploadButton.onClicked((event: MouseEvent) => {
                if (property.hasNullValue()) {
                    return;
                }
                wemjq(this.mediaUploaderEl.getDropzone().getEl().getHTMLElement()).simulate("click");
            });

            wrapper.appendChild(this.mediaUploaderEl);
            wrapper.appendChild(uploadButton);

            return wrapper;
        }

        private createUploader(property: Property): api.content.MediaUploaderEl {

            var predefinedAllowTypes,
                attachmentFileName = this.getFileNameFromProperty(property);

            if (this.propertyAlreadyHasAttachment(property)) {
                predefinedAllowTypes = this.getAllowTypeFromFileName(attachmentFileName);
            }

            var allowTypesConfig: MediaUploaderConfigAllowType[] = predefinedAllowTypes || (<any>(this.config.inputConfig)).allowTypes ||
                [];
            var allowTypes = allowTypesConfig.map((allowType: MediaUploaderConfigAllowType) => {
                return {title: allowType.name, extensions: allowType.extensions};
            });

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
                deferred: true
            });
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.mediaUploaderEl.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.mediaUploaderEl.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.mediaUploaderEl.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.mediaUploaderEl.unBlur(listener);
        }
    }
    api.form.inputtype.InputTypeManager.register(new api.Class("MediaUploader", MediaUploader));
}