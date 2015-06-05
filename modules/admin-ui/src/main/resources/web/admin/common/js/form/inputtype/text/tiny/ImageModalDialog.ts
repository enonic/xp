module api.form.inputtype.text.tiny {

    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;

    export class ImageModalDialog extends ModalDialog {
        private imageContainer: api.dom.DivEl;

        constructor(editor: TinyMceEditor) {
            super(editor, new api.ui.dialog.ModalDialogHeader("Insert Image"));
        }

        private createImageSelector(id: string, imageId: string): FormItem {
            var loader = new api.content.ContentSummaryLoader(),
                imageSelector = api.content.ContentComboBox.create().setLoader(loader).setMaximumOccurrences(1).build(),
                selectorComboBox = imageSelector.getComboBox(),
                formItem = this.createFormItem(id, "Image", Validators.required, imageId, <api.dom.FormItemEl>imageSelector),
                imageContainer = new api.dom.DivEl("content-item-preview-panel");

            loader.setAllowedContentTypeNames([api.schema.content.ContentTypeName.IMAGE]);

            selectorComboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.ContentSummary>) => {
                var contentId = event.getOption().displayValue.getContentId();
                if (!contentId) {
                    return;
                }

                formItem.addClass("image-preview");
                this.previewImage(imageContainer, contentId.toString(), selectorComboBox.getEl().getWidth());
            });

            selectorComboBox.onExpanded((event: api.ui.selector.DropdownExpandedEvent) => {
                this.adjustSelectorDropDown(selectorComboBox.getInput(), event.getDropdownElement().getEl());
            });

            selectorComboBox.onOptionDeselected(() => {
                formItem.removeClass("image-preview");
                this.removePreview(imageContainer);
            });

            imageContainer.insertAfterEl(formItem.getInput());

            return formItem;
        }

        private adjustSelectorDropDown(inputElement: api.dom.Element, dropDownElement: api.dom.ElementHelper) {
            var inputPosition = wemjq(inputElement.getHTMLElement()).offset();

            dropDownElement.setMaxWidthPx(inputElement.getEl().getWidthWithBorder());
            dropDownElement.setTopPx(inputPosition.top + inputElement.getEl().getHeightWithBorder() - 1);
            dropDownElement.setLeftPx(inputPosition.left);
        }

        private previewImage(imageContainer: api.dom.DivEl, contentId: string, width: number) {
            var imgUrl = new api.content.ContentImageUrlResolver().
                setContentId(new api.content.ContentId(contentId)).
                setScaleWidth(true).
                setSize(width).
                resolve();

            var image = new api.dom.ImgEl(imgUrl);
            imageContainer.appendChild(image);
        }

        private removePreview(imageContainer: api.dom.DivEl) {
            imageContainer.removeChildren();
        }

        private getImageId(): string {
            return api.util.StringHelper.EMPTY_STRING;
        }

        private getCaption(): string {
            return api.util.StringHelper.EMPTY_STRING;
        }

        protected getMainFormItems(): FormItem[] {
            var imageSelector = this.createImageSelector("imageId", this.getImageId());
            this.setFirstFocusField(imageSelector.getInput());

            return [
                imageSelector,
                this.createFormItem("caption", "Caption", Validators.required, this.getCaption())
            ];
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