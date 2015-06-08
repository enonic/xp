module api.form.inputtype.text.tiny {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;

    export class ImageModalDialog extends ModalDialog {

        private imagePreviewContainer: api.dom.DivEl;
        private uploader: api.content.ImageUploader;

        constructor(editor: TinyMceEditor) {
            super(editor, new api.ui.dialog.ModalDialogHeader("Insert Image"));
        }

        private createImageSelector(id: string, imageId: string): FormItem {
            var loader = new api.content.ContentSummaryLoader(),
                imageSelector = api.content.ContentComboBox.create().setLoader(loader).setMaximumOccurrences(1).build(),
                formItem = this.createFormItem(id, "Image", Validators.required, imageId, <api.dom.FormItemEl>imageSelector),
                imageSelectorComboBox = imageSelector.getComboBox();

            formItem.addClass("image-selector");

            loader.setAllowedContentTypeNames([api.schema.content.ContentTypeName.IMAGE]);

            imageSelectorComboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.ContentSummary>) => {
                var contentId = event.getOption().displayValue.getContentId();
                if (!contentId) {
                    return;
                }

                formItem.addClass("image-preview");
                this.previewImage(contentId.toString());
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

        private previewImage(contentId: string) {
            var imgUrl = new api.content.ContentImageUrlResolver().
                setContentId(new api.content.ContentId(contentId)).
                setScaleWidth(true).
                setSize(640).
                resolve();

            var image = new api.dom.ImgEl(imgUrl);
            image.onLoaded(() => {
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });
            this.imagePreviewContainer.appendChild(image);
        }

        private removePreview() {
            this.imagePreviewContainer.removeChildren();
        }

        private getImageId(): string {
            return api.util.StringHelper.EMPTY_STRING;
        }

        protected show() {
            super.show();

            this.uploader.show();
        }

        protected getMainFormItems(): FormItem[] {
            var imageSelector = this.createImageSelector("imageId", this.getImageId());
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
            this.addAction(new api.ui.Action("Insert").onExecuted(() => {
                if (this.validate()) {
                    this.close();
                }
            }));

            super.initializeActions();
        }
    }
}