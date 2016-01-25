module api.form.inputtype.text.htmlarea {

    import FormItemBuilder = api.ui.form.FormItemBuilder;
    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;
    import UploadItem = api.ui.uploader.UploadItem;
    import FileUploadedEvent = api.ui.uploader.FileUploadedEvent;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import FileUploadProgressEvent = api.ui.uploader.FileUploadProgressEvent;
    import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
    import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;
    import Content = api.content.Content;
    import Action = api.ui.Action;

    export class ImageModalDialog extends ModalDialog {

        private imagePreviewContainer: api.dom.DivEl;
        private imageCaptionField: FormItem;
        private imageUploaderEl: api.content.ImageUploaderEl;
        private imageElement: HTMLImageElement;
        private contentId: api.content.ContentId;
        private imageSelector: api.content.ContentComboBox;
        private progress: api.ui.ProgressBar;
        private error: api.dom.DivEl;
        private image: api.dom.ImgEl;
        private elementContainer: HTMLElement;
        private callback: Function;
        private imageToolbar: ImageToolbar;

        constructor(config: api.form.inputtype.text.HtmlAreaImage, contentId: api.content.ContentId) {
            this.imageElement = <HTMLImageElement>config.element;
            this.elementContainer = config.container;
            this.contentId = contentId;
            this.callback = config.callback;

            super(config.editor, new api.ui.dialog.ModalDialogHeader("Insert Image"));
        }

        private getImageContent(images: api.content.ContentSummary[]): api.content.ContentSummary {
            var filteredImages = images.filter((image: api.content.ContentSummary) => {
                return this.imageElement.src.indexOf(image.getId()) > 0;
            });

            return filteredImages.length > 0 ? filteredImages[0] : null;
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
                    var imageContent = this.getImageContent(event.getData());
                    if (imageContent) {
                        imageSelector.setValue(imageContent.getId());
                        this.previewImage(imageContent, formItem);
                    }
                    loader.unLoadedData(singleLoadListener);
                };
                loader.onLoadedData(singleLoadListener);
                loader.load();
            }

            imageSelectorComboBox.onOptionSelected((selectedOption: api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => {
                var imageContent = selectedOption.getOption().displayValue;
                if (!imageContent.getContentId()) {
                    return;
                }

                this.previewImage(imageContent, formItem);
            });

            imageSelectorComboBox.onExpanded((event: api.ui.selector.DropdownExpandedEvent) => {
                if (event.isExpanded()) {
                    this.adjustSelectorDropDown(imageSelectorComboBox.getInput(), event.getDropdownElement().getEl());
                }
            });

            imageSelectorComboBox.onOptionDeselected(() => {
                formItem.removeClass("image-preview");
                this.removePreview();
                this.imageToolbar.remove();
                this.showCaptionLabel();
                this.imageUploaderEl.show();
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });

            imageSelectorComboBox.onKeyDown((e: KeyboardEvent) => {
                if (api.ui.KeyHelper.isEscKey(e) && !imageSelectorComboBox.isDropdownShown()) {
                    // Prevent modal dialog from closing on Esc key when dropdown is expanded
                    e.preventDefault();
                    e.stopPropagation();
                }
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
            imageEl.getEl().setAttribute("data-src", HtmlArea.imagePrefix + contentId);

            return imageEl;
        }

        private previewImage(imageContent: api.content.ContentSummary, formItem: FormItem) {
            var contentId = imageContent.getContentId().toString(),
                imgUrl = new api.content.ContentImageUrlResolver().
                    setContentId(new api.content.ContentId(contentId)).
                    setScaleWidth(true).
                    setSize(HtmlArea.maxImageWidth).
                    resolve();

            this.image = this.createImgEl(imgUrl, imageContent.getDisplayName(), contentId);

            if (this.imageElement) {
                this.image.getHTMLElement().style["text-align"] =
                    this.imageElement.parentElement.style.textAlign || this.imageElement.parentElement.style.cssFloat;

                this.imageToolbar = new ImageToolbar(this.image);
                if (this.imageToolbar.isImageInOriginalSize(new api.dom.ElementHelper(this.imageElement))) {
                    this.imageToolbar.appendKeepSizeParamToImagePath();
                }
            }
            else {
                this.imageToolbar = new ImageToolbar(this.image);
                this.image.getHTMLElement().style["text-align"] = "justify";
            }

            this.image.onLoaded(() => {
                this.imagePreviewContainer.removeClass("upload");
                wemjq(this.imageToolbar.getHTMLElement()).insertBefore(this.imagePreviewContainer.getHTMLElement());
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
                if (this.getCaptionFieldValue() == "") {
                    this.imageCaptionField.getEl().scrollIntoView();
                    this.imageCaptionField.getInput().giveFocus();
                }
            });

            formItem.addClass("image-preview");

            this.hideUploadMasks();
            this.hideCaptionLabel();
            this.imageUploaderEl.hide();

            this.imagePreviewContainer.insertChild(this.image, 0);
        }

        private hideCaptionLabel() {
            this.imageCaptionField.getLabel().hide();
            this.imageCaptionField.getInput().getEl().setAttribute("placeholder", "Caption");
            this.imageCaptionField.getInput().getParentElement().getEl().setMarginLeft("0px");
        }

        private showCaptionLabel() {
            this.imageCaptionField.getLabel().show();
            this.imageCaptionField.getInput().getEl().removeAttribute("placeholder")
            this.imageCaptionField.getInput().getParentElement().getEl().setMarginLeft("");
        }

        private removePreview() {
            this.imagePreviewContainer.removeChild(this.image);
        }

        show() {
            super.show();

            this.imageUploaderEl.show();
        }

        protected getMainFormItems(): FormItem[] {
            var imageSelector = this.createImageSelector("imageId");

            this.addUploaderAndPreviewControls(imageSelector);
            this.setFirstFocusField(imageSelector.getInput());

            return [
                imageSelector,
                this.imageCaptionField = this.createFormItem("caption", "Caption", null, this.getCaption())
            ];
        }

        private createImagePreviewContainer() {
            var imagePreviewContainer = new api.dom.DivEl("content-item-preview-panel");

            this.progress = new api.ui.ProgressBar();
            imagePreviewContainer.appendChild(this.progress);

            this.error = new api.dom.DivEl("error");
            imagePreviewContainer.appendChild(this.error);

            this.imagePreviewContainer = imagePreviewContainer;
        }

        private getCaption(): string {
            if (this.imageElement) {
                return wemjq(this.imageElement.parentElement).children("figcaption").text();
            }
            else {
                return api.util.StringHelper.EMPTY_STRING;
            }
        }

        private addUploaderAndPreviewControls(imageSelector: FormItem) {
            var imageSelectorContainer = imageSelector.getInput().getParentElement();

            imageSelectorContainer.appendChild(this.imageUploaderEl = this.createImageUploader());

            this.createImagePreviewContainer();

            wemjq(this.imagePreviewContainer.getHTMLElement()).insertAfter(imageSelectorContainer.getHTMLElement());
        }

        private createImageUploader(): api.content.ImageUploaderEl {
            var uploader = new api.content.ImageUploaderEl(<api.content.ImageUploaderElConfig>{
                params: {
                    parent: this.contentId.toString()
                },
                operation: api.content.MediaUploaderElOperation.create,
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

                //new api.content.ContentUpdatedEvent(this.contentId).fire();
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
            var submitAction = new api.ui.Action(this.imageElement ? "Update" : "Insert", "enter");
            this.setSubmitAction(submitAction);
            this.addAction(submitAction.onExecuted(() => {
                if (this.validate()) {
                    this.createImageTag();
                    this.close();
                }
            }));

            super.initializeActions();
        }

        private generateUUID(): string {
            var d = new Date().getTime();
            var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                var r = (d + Math.random() * 16) % 16 | 0;
                d = Math.floor(d / 16);
                return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
            });
            return uuid;
        }

        private getCaptionFieldValue() {
            return (<api.dom.InputEl>this.imageCaptionField.getInput()).getValue().trim();
        }

        private isImageWiderThanEditor() {
            return (this.image.getHTMLElement()["width"] > this.getEditor()["editorContainer"].clientWidth);
        }

        private setImageWidthConstraint() {
            var keepImageSize = this.imageToolbar.isImageInOriginalSize(this.image.getEl());
            this.image.getHTMLElement().style["width"] = (this.isImageWiderThanEditor() || !keepImageSize) ? "100%" : "auto";
        }

        private createFigureElement(figCaptionId: string) {

            var figure = api.dom.ElementHelper.fromName("figure");
            var figCaption = api.dom.ElementHelper.fromName("figcaption");
            figCaption.setId(figCaptionId);
            figCaption.setText(this.getCaptionFieldValue());
            figCaption.setAttribute("style", "text-align: center");

            figure.appendChildren([(<api.dom.ImgEl>this.image).getEl().getHTMLElement(), figCaption.getHTMLElement()]);

            return figure;
        }

        private updateImageParentAlignment(element: HTMLElement, alignment?: string) {
            if (!alignment) {
                alignment = this.image.getHTMLElement().style["text-align"];
            }

            var styleFormat = "float: {0}; margin: {1};" +
                              (this.imageToolbar.isImageInOriginalSize(this.image.getEl()) ? "" : "width: {2}%;");
            var styleAttr = "text-align: " + alignment + ";";

            switch (alignment) {
            case 'left':
            case 'right':
                styleAttr = api.util.StringHelper.format(styleFormat, alignment, "15px", "40");
                break;
            case 'center':
                styleAttr = styleAttr + api.util.StringHelper.format(styleFormat, "none", "auto", "60");
                break;
            }

            element.setAttribute("style", styleAttr);
            element.setAttribute("data-mce-style", styleAttr);
        }

        private createImageTag(): void {
            var container = this.elementContainer,
                isProperContainer = function () {
                    return container.nodeName !== "FIGCAPTION" && container.nodeName !== "#text"
                };

            if (this.imageElement) {
                this.updateImageParentAlignment(this.imageElement.parentElement);
                this.changeImageParentAlignmentOnImageAlignmentChange(this.imageElement.parentElement);

                wemjq(this.imageElement.parentElement).children("figcaption").text(this.getCaptionFieldValue());
                this.imageElement.parentElement.replaceChild((<api.dom.ImgEl>this.image).getEl().getHTMLElement(), this.imageElement);
                this.setImageWidthConstraint();

                setTimeout(() => {
                    this.getEditor().nodeChanged({selectionChange: true})
                }, 50);
            }
            else {
                var figCaptionId = this.generateUUID(),
                    figure = this.createFigureElement(figCaptionId);

                this.updateImageParentAlignment(figure.getHTMLElement());
                this.changeImageParentAlignmentOnImageAlignmentChange(figure.getHTMLElement());
                this.setImageWidthConstraint();

                if (!isProperContainer() || container.nodeName === "FIGURE") {
                    while (!isProperContainer()) {
                        container = container.parentElement;
                    }
                }

                this.removeExtraIndentFromContainer(container);

                new api.dom.ElementHelper(container).appendChild(figure.getHTMLElement());

                this.getEditor().nodeChanged();

                this.callback(figCaptionId);
            }
        }

        private removeExtraIndentFromContainer(container) {
            var elements = <Element[]>new api.dom.ElementHelper(container).getChildren();

            for (let i = 0; i < elements.length; i++) {
                if (elements[i].tagName.toLowerCase() === "br" && elements[i].hasAttribute("data-mce-bogus")) {
                    container.removeChild(elements[i]);
                }
            }
        }

        private changeImageParentAlignmentOnImageAlignmentChange(parent: HTMLElement) {
            var observer = new MutationObserver((mutations) => {
                mutations.forEach((mutation) => {
                    var alignment = (<HTMLElement>mutation.target).style["text-align"];
                    this.updateImageParentAlignment(parent, alignment);
                });
            });

            var config = {attributes: true, childList: false, characterData: false, attributeFilter: ["style"]};

            observer.observe((<api.dom.ImgEl>this.image).getEl().getHTMLElement(), config);
        }
    }

    export class ImageToolbar extends api.ui.toolbar.Toolbar {

        private image: api.dom.ImgEl;

        private justifyButton: api.ui.button.ActionButton;

        private alignLeftButton: api.ui.button.ActionButton;

        private centerButton: api.ui.button.ActionButton;

        private alignRightButton: api.ui.button.ActionButton;

        private keepOriginalSizeCheckbox: api.ui.Checkbox;

        constructor(image: api.dom.ImgEl) {
            super("image-toolbar");

            this.image = image;

            super.addElement(this.justifyButton = this.createJustifiedButton());
            super.addElement(this.alignLeftButton = this.createLeftAlignedButton());
            super.addElement(this.centerButton = this.createCenteredButton());
            super.addElement(this.alignRightButton = this.createRightAlignedButton());
            super.addElement(this.keepOriginalSizeCheckbox = this.createKeepOriginalSizeCheckbox());

            this.initActiveButton();
        }

        public isImageInOriginalSize(image: api.dom.ElementHelper) {
            return image.getAttribute("data-src").indexOf("keepSize=true") > 0;
        }

        private createJustifiedButton(): api.ui.button.ActionButton {
            return this.createAlignmentButton("icon-paragraph-justify");
        }

        private createLeftAlignedButton(): api.ui.button.ActionButton {
            return this.createAlignmentButton("icon-paragraph-left");
        }

        private createCenteredButton(): api.ui.button.ActionButton {
            return this.createAlignmentButton("icon-paragraph-center");
        }

        private createRightAlignedButton(): api.ui.button.ActionButton {
            return this.createAlignmentButton("icon-paragraph-right");
        }

        private createAlignmentButton(iconClass: string): api.ui.button.ActionButton {
            var action: Action = new Action("");

            action.setIconClass(iconClass);

            var button = new api.ui.button.ActionButton(action);

            action.onExecuted(() => {
                this.resetActiveButton();
                button.addClass("active");
                this.image.getHTMLElement().style["text-align"] = this.getImageAlignment();
            });

            return button;
        }

        private createKeepOriginalSizeCheckbox(): api.ui.Checkbox {
            var keepOriginalSizeCheckbox = new api.ui.Checkbox();
            keepOriginalSizeCheckbox.setChecked(this.image.getEl().getAttribute("data-src").indexOf("keepSize=true") > 0);
            keepOriginalSizeCheckbox.addClass('keep-size-check');
            keepOriginalSizeCheckbox.onValueChanged(()=> {
                this.keepOriginalSizeCheckbox.isChecked()
                    ? this.appendKeepSizeParamToImagePath()
                    : this.removeKeepSizeParamFromImagePath();
            });
            keepOriginalSizeCheckbox.setLabel('Keep original size');

            return keepOriginalSizeCheckbox;
        }

        public appendKeepSizeParamToImagePath() {
            if (!this.isImageInOriginalSize(this.image.getEl())) {
                var pathAttr = this.image.getEl().getAttribute("data-src");
                this.image.getEl().setAttribute("data-src", pathAttr + "?keepSize=true");
            }
            if (!this.keepOriginalSizeCheckbox.isChecked()) {
                this.keepOriginalSizeCheckbox.setChecked(true);
            }
        }

        private removeKeepSizeParamFromImagePath() {
            if (this.isImageInOriginalSize(this.image.getEl())) {
                var pathAttr = this.image.getEl().getAttribute("data-src");
                var paramToRemoveIndex = this.image.getEl().getAttribute("data-src").indexOf("?keepSize=true");
                this.image.getEl().setAttribute("data-src", pathAttr.substring(0, paramToRemoveIndex));
            }
            if (this.keepOriginalSizeCheckbox.isChecked()) {
                this.keepOriginalSizeCheckbox.setChecked(false);
            }
        }

        private initActiveButton() {
            var alignment = this.image.getHTMLElement().style["text-align"];

            switch (alignment) {
            case 'justify':
                this.justifyButton.addClass("active");
                break;
            case 'left':
                this.alignLeftButton.addClass("active");
                break;
            case 'center':
                this.centerButton.addClass("active");
                break;
            case 'right':
                this.alignRightButton.addClass("active");
                break;
            default:
                this.justifyButton.addClass("active");
                break;
            }
        }

        private resetActiveButton() {
            this.justifyButton.removeClass("active");
            this.alignLeftButton.removeClass("active");
            this.centerButton.removeClass("active");
            this.alignRightButton.removeClass("active");
        }

        private getImageAlignment(): string {
            if (this.justifyButton.hasClass("active")) {
                return "justify";
            }

            if (this.alignLeftButton.hasClass("active")) {
                return "left";
            }

            if (this.centerButton.hasClass("active")) {
                return "center";
            }

            if (this.alignRightButton.hasClass("active")) {
                return "right";
            }

            return "justify";
        }

    }
}