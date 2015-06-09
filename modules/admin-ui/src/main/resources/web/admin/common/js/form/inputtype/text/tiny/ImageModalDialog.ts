module api.form.inputtype.text.tiny {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;

    export class ImageModalDialog extends ModalDialog {

        private imagePreviewContainer: api.dom.DivEl;
        private uploader: api.content.ImageUploader;
        private imageElement: HTMLImageElement;

        constructor(editor: TinyMceEditor, imageElement: HTMLImageElement) {
            this.imageElement = imageElement;

            super(editor, new api.ui.dialog.ModalDialogHeader("Insert Image"));
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

            var image = this.createImgEl(imgUrl, imageContent.getDisplayName(), contentId);

            image.onLoaded(() => {
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });
            this.imagePreviewContainer.appendChild(image);
        }

        private removePreview() {
            this.imagePreviewContainer.removeChildren();
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

        private addUploaderAndPreviewControls(imageSelector: FormItem) {
            var imageSelectorContainer = imageSelector.getInput().getParentElement();

            imageSelectorContainer.appendChild(this.uploader = this.createImageUploader());

            this.imagePreviewContainer = new api.dom.DivEl("content-item-preview-panel");
            wemjq(this.imagePreviewContainer.getHTMLElement()).insertAfter(imageSelectorContainer.getHTMLElement());
        }

        private createImageUploader(): api.content.ImageUploader {
            var uploader = new api.content.ImageUploader(<api.content.ImageUploaderConfig>{
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

            return uploader;
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
            var imageEl = <api.dom.ImgEl>this.imagePreviewContainer.getFirstChild();

            if (this.imageElement) {
                this.imageElement.parentElement.replaceChild(imageEl.getEl().getHTMLElement(), this.imageElement);
            }
            else {
                this.getEditor().insertContent("<figure>" + imageEl.toString() + "<figcaption></figcaption></figure>");
            }
        }
    }
}