module api.form.inputtype.text.tiny {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;
    import UploadItem = api.ui.uploader.UploadItem;
    import FileUploadedEvent = api.ui.uploader.FileUploadedEvent;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import FileUploadProgressEvent = api.ui.uploader.FileUploadProgressEvent;
    import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
    import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;
    import Content = api.content.Content;

    export class ImageModalDialog extends ModalDialog {

        private imagePreviewContainer: api.dom.DivEl;
        private uploader: api.content.ImageUploader;
        private imageElement: HTMLImageElement;
        private contentId: api.content.ContentId;
        private imageSelector: api.content.ContentComboBox;
        private progress: api.ui.ProgressBar;
        private error: api.dom.DivEl;
        private image: api.dom.ImgEl;
        private elementContainer: HTMLElement;
        private callback: Function;

        constructor(config: api.form.inputtype.text.TinyMCEImage, contentId: api.content.ContentId) {
            this.imageElement = <HTMLImageElement>config.element;
            this.elementContainer = config.container;
            this.contentId = contentId;
            this.callback = config.callback;

            super(config.editor, new api.ui.dialog.ModalDialogHeader("Insert Image"));
        }

        private getImageId(images: api.content.ContentSummary[]): string {
            var filteredImages = images.filter((image: api.content.ContentSummary) => {
                return this.imageElement.src.indexOf(image.getId()) > 0;
            });

            return filteredImages.length > 0 ? filteredImages[0].getId() : api.util.StringHelper.EMPTY_STRING;
        }

        private createImageSelector(id: string): FormItem {
            var loader = new api.content.ContentSummaryLoader(),
                imageSelector = api.content.ContentComboBox.create().setLoader(loader).setMaximumOccurrences(1).build(),
                formItem = this.createFormItem(id, "Image", Validators.required, api.util.StringHelper.EMPTY_STRING,
                    <api.dom.FormItemEl>imageSelector),
                imageSelectorComboBox = imageSelector.getComboBox();

            this.imageSelector = imageSelector;

            formItem.addClass("image-selector");

            loader.setAllowedContentTypeNames([api.schema.content.ContentTypeName.IMAGE]);

            if (this.imageElement) {
                var singleLoadListener = (event: api.util.loader.event.LoadedDataEvent<api.content.ContentSummary>) => {
                    var imageId = this.getImageId(event.getData());
                    if (imageId) {
                        imageSelector.setValue(imageId);
                    }
                    loader.unLoadedData(singleLoadListener);
                };
                loader.onLoadedData(singleLoadListener);
                loader.load();
            }

            imageSelectorComboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.ContentSummary>) => {
                var imageContent = event.getOption().displayValue;
                if (!imageContent.getContentId()) {
                    return;
                }

                formItem.addClass("image-preview");
                this.previewImage(imageContent);
                this.uploader.hide();
            });

            imageSelectorComboBox.onExpanded((event: api.ui.selector.DropdownExpandedEvent) => {
                this.adjustSelectorDropDown(imageSelectorComboBox.getInput(), event.getDropdownElement().getEl());
            });

            imageSelectorComboBox.onOptionDeselected(() => {
                formItem.removeClass("image-preview");
                this.removePreview();
                this.uploader.show();
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });

            return formItem;
        }

        private adjustSelectorDropDown(inputElement: api.dom.Element, dropDownElement: api.dom.ElementHelper) {
            var inputPosition = wemjq(inputElement.getHTMLElement()).offset();

            dropDownElement.setMaxWidthPx(inputElement.getEl().getWidthWithBorder() - 2);
            dropDownElement.setTopPx(inputPosition.top + inputElement.getEl().getHeightWithBorder() - 1);
            dropDownElement.setLeftPx(inputPosition.left);
        }

        private createImgEl(src: string, alt: string, contentId: string): api.dom.ImgEl {
            var imageEl = new api.dom.ImgEl(src);
            imageEl.getEl().setAttribute("alt", alt);
            imageEl.getEl().setAttribute("style", "width: 100%;");
            imageEl.getEl().setAttribute("data-src", TinyMCE.imagePrefix + contentId);

            return imageEl;
        }

        private previewImage(imageContent: api.content.ContentSummary) {
            var contentId = imageContent.getContentId().toString(),
                imgUrl = new api.content.ContentImageUrlResolver().
                    setContentId(new api.content.ContentId(contentId)).
                    setScaleWidth(true).
                    setSize(TinyMCE.maxImageWidth).
                    resolve();

            this.image = this.createImgEl(imgUrl, imageContent.getDisplayName(), contentId);

            this.image.onLoaded(() => {
                this.imagePreviewContainer.removeClass("upload");
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });

            this.hideUploadMasks();
            this.imagePreviewContainer.insertChild(this.image, 0);
        }

        private removePreview() {
            this.imagePreviewContainer.removeChild(this.image);
        }

        show() {
            super.show();

            this.uploader.show();
        }

        protected getMainFormItems(): FormItem[] {
            var imageSelector = this.createImageSelector("imageId");

            this.addUploaderAndPreviewControls(imageSelector);
            this.setFirstFocusField(imageSelector.getInput());

            return [
                imageSelector
            ];
        }

        private getImagePreviewContainer() {
            var imagePreviewContainer = new api.dom.DivEl("content-item-preview-panel");

            this.progress = new api.ui.ProgressBar();
            imagePreviewContainer.appendChild(this.progress);

            this.error = new api.dom.DivEl("error");
            imagePreviewContainer.appendChild(this.error);

            return imagePreviewContainer;
        }

        private addUploaderAndPreviewControls(imageSelector: FormItem) {
            var imageSelectorContainer = imageSelector.getInput().getParentElement();

            imageSelectorContainer.appendChild(this.uploader = this.createImageUploader());

            this.imagePreviewContainer = this.getImagePreviewContainer();

            wemjq(this.imagePreviewContainer.getHTMLElement()).insertAfter(imageSelectorContainer.getHTMLElement());
        }

        private createImageUploader(): api.content.ImageUploader {
            var uploader = new api.content.ImageUploader(<api.content.ImageUploaderConfig>{
                params: {
                    parent: this.contentId.toString()
                },
                operation: api.content.MediaUploaderOperation.create,
                name: 'image-selector-upload-dialog',
                showButtons: false,
                showResult: false,
                maximumOccurrences: 1,
                allowMultiSelection: false,
                scaleWidth: false,
                deferred: true
            });

            uploader.addClass("minimized");
            uploader.hide();

            uploader.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                this.hideUploadMasks();
                this.imagePreviewContainer.addClass("upload");
                this.showProgress();
            });

            uploader.onUploadProgress((event: FileUploadProgressEvent<Content>) => {
                var item = event.getUploadItem();

                this.setProgress(item.getProgress());
            });

            uploader.onFileUploaded((event: FileUploadedEvent<Content>) => {
                var item = event.getUploadItem();
                var createdContent = item.getModel();

                new api.content.ContentUpdatedEvent(this.contentId).fire();
                this.imageSelector.setContent(createdContent);
            });

            uploader.onUploadFailed((event: FileUploadFailedEvent<Content>) => {
                this.showError("Upload failed")
            });

            return uploader;
        }

        private setProgress(value: number) {
            this.progress.setValue(value);
        }

        private showProgress() {
            this.progress.show();
        }


        private hideUploadMasks() {
            this.progress.hide();
            this.error.hide();
        }

        private showError(text: string) {
            this.progress.hide();
            this.error.setHtml(text).show();
            this.error.show();
        }

        protected initializeActions() {
            this.addAction(new api.ui.Action(this.imageElement ? "Update" : "Insert").onExecuted(() => {
                if (this.validate()) {
                    this.createImageTag();
                    this.close();
                }
            }));

            super.initializeActions();
        }

        private createImageTag(): void {
            var imageEl = <api.dom.ImgEl>this.image,
                container = this.elementContainer,
                isProperContainer = function () {
                    return container.nodeName !== "FIGURE" && container.nodeName !== "FIGCAPTION"
                };

            if (this.imageElement) {
                this.imageElement.parentElement.replaceChild(imageEl.getEl().getHTMLElement(), this.imageElement);
            }
            else {
                var figure = api.dom.ElementHelper.fromName("figure");
                var figCaption = api.dom.ElementHelper.fromName("figcaption");
                figure.appendChildren([imageEl.getEl().getHTMLElement(), figCaption.getHTMLElement()]);

                if (!isProperContainer()) {
                    if (container.nodeName === "FIGCAPTION") {
                        container = container.parentElement;
                    }
                    if (container.nodeName === "FIGURE") {
                        figure.insertAfterEl(new api.dom.ElementHelper(container));
                    }

                    this.getEditor().nodeChanged();
                }
                else {
                    this.getEditor().insertContent(figure.getHTMLElement().outerHTML);
                }

                this.callback(figCaption.getHTMLElement());
                figCaption.scrollIntoView();
            }
        }

    }
}