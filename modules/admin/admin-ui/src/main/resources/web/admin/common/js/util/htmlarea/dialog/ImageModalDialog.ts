module api.util.htmlarea.dialog {

    import FormItemBuilder = api.ui.form.FormItemBuilder;
    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;
    import UploadItem = api.ui.uploader.UploadItem;
    import FileUploadedEvent = api.ui.uploader.FileUploadedEvent;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import FileUploadProgressEvent = api.ui.uploader.FileUploadProgressEvent;
    import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
    import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
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
        private imagePreviewScrollHandler: ImagePreviewScrollHandler;
        private imageLoadMask: api.ui.mask.LoadMask;

        static imagePrefix = "image://";
        static maxImageWidth = 640;

        constructor(config:HtmlAreaImage, contentId:api.content.ContentId) {
            this.imageElement = <HTMLImageElement>config.element;
            this.elementContainer = config.container;
            this.contentId = contentId;
            this.callback = config.callback;

            super(config.editor, new api.ui.dialog.ModalDialogHeader("Insert Image"), "image-modal-dialog");
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
                        this.createImgElForExistingImage(imageContent);
                        this.previewImage();
                        formItem.addClass("image-preview");
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

                this.imageLoadMask.show();
                this.createImgElForNewImage(imageContent);
                this.previewImage();
                formItem.addClass("image-preview");
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
                this.imagePreviewScrollHandler.toggleScrollButtons();
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

        private addUploaderAndPreviewControls(imageSelector: FormItem) {
            var imageSelectorContainer = imageSelector.getInput().getParentElement();

            imageSelectorContainer.appendChild(this.imageUploaderEl = this.createImageUploader());

            this.createImagePreviewContainer();

            var scrollBarWrapperDiv = new api.dom.DivEl("preview-panel-scrollbar-wrapper");
            scrollBarWrapperDiv.appendChild(this.imagePreviewContainer);
            var scrollNavigationWrapperDiv = new api.dom.DivEl("preview-panel-scroll-navigation-wrapper");
            scrollNavigationWrapperDiv.appendChild(scrollBarWrapperDiv);

            wemjq(scrollNavigationWrapperDiv.getHTMLElement()).insertAfter(imageSelectorContainer.getHTMLElement());

            this.imagePreviewScrollHandler = new ImagePreviewScrollHandler(this.imagePreviewContainer);

            this.imageLoadMask = new api.ui.mask.LoadMask(this.imagePreviewContainer);
            this.imagePreviewContainer.appendChild(this.imageLoadMask);

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
                this.resetPreviewContainerMaxHeight();
                this.imagePreviewScrollHandler.toggleScrollButtons();
                this.imagePreviewScrollHandler.setMarginRight();
            });
        }

        private getImageContent(images: api.content.ContentSummary[]): api.content.ContentSummary {
            var filteredImages = images.filter((image: api.content.ContentSummary) => {
                return this.imageElement.src.indexOf(image.getId()) > 0;
            });

            return filteredImages.length > 0 ? filteredImages[0] : null;
        }

        private adjustSelectorDropDown(inputElement: api.dom.Element, dropDownElement: api.dom.ElementHelper) {
            var inputPosition = wemjq(inputElement.getHTMLElement()).offset();

            dropDownElement.setMaxWidthPx(inputElement.getEl().getWidthWithBorder() - 2);
            dropDownElement.setTopPx(inputPosition.top + inputElement.getEl().getHeightWithBorder() - 1);
            dropDownElement.setLeftPx(inputPosition.left);
        }

        private createImgElForExistingImage(imageContent: api.content.ContentSummary) {
            this.image = this.createImgElForPreview(imageContent, true);
        }

        private createImgElForNewImage(imageContent: api.content.ContentSummary) {
            this.image = this.createImgElForPreview(imageContent, false);
        }

        private previewImage() {
            this.imageToolbar = new ImageToolbar(this.image, this.imageLoadMask);
            this.imageToolbar.onCroppingChanged(() => {
               this.imagePreviewScrollHandler.resetScrollPosition();
            });

            this.image.onLoaded(() => {
                this.imageLoadMask.hide();
                this.imagePreviewContainer.removeClass("upload");
                wemjq(this.imageToolbar.getHTMLElement()).insertBefore(
                    this.imagePreviewContainer.getHTMLElement().parentElement.parentElement);
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
                if (this.getCaptionFieldValue() == "") {
                    this.imageCaptionField.getEl().scrollIntoView();
                    this.imageCaptionField.getInput().giveFocus();
                }
            });

            this.hideUploadMasks();
            this.hideCaptionLabel();
            this.imageUploaderEl.hide();
            this.imagePreviewContainer.insertChild(this.image, 0);
        }

        private createImgElForPreview(imageContent: api.content.ContentSummary, isExistingImg: boolean = false): api.dom.ImgEl {
            var imgSrcAttr = isExistingImg
                ? new api.dom.ElementHelper(this.imageElement).getAttribute("src")
                : this.generateDefaultImgSrc(imageContent.getContentId().toString());
            var imgDataSrcAttr = isExistingImg
                ? new api.dom.ElementHelper(this.imageElement).getAttribute("data-src")
                : ImageModalDialog.imagePrefix + imageContent.getContentId().toString();

            var imageEl = new api.dom.ImgEl(imgSrcAttr);
            imageEl.getEl().setAttribute("alt", imageContent.getDisplayName());
            imageEl.getEl().setAttribute("data-src", imgDataSrcAttr);

            var imageAlignment = isExistingImg ? (this.imageElement.parentElement.style.textAlign ||
                                                  this.imageElement.parentElement.style.cssFloat) : "justify";
            imageEl.getHTMLElement().style.textAlign = imageAlignment;

            return imageEl;
        }

        private generateDefaultImgSrc(contentId): string {
            return new api.content.ContentImageUrlResolver().setContentId(new api.content.ContentId(contentId)).setScaleWidth(true).setSize(
                ImageModalDialog.maxImageWidth).resolve();
        }

        private hideCaptionLabel() {
            this.imageCaptionField.getLabel().hide();
            this.imageCaptionField.getInput().getEl().setAttribute("placeholder", "Caption");
            this.imageCaptionField.getInput().getParentElement().getEl().setMarginLeft("0px");
        }

        private showCaptionLabel() {
            this.imageCaptionField.getLabel().show();
            this.imageCaptionField.getInput().getEl().removeAttribute("placeholder");
            this.imageCaptionField.getInput().getParentElement().getEl().setMarginLeft("");
        }

        private removePreview() {
            this.imagePreviewContainer.removeChild(this.image);
        }

        show() {
            super.show();

            this.imageUploaderEl.show();
        }

        private createImagePreviewContainer() {
            var imagePreviewContainer = new api.dom.DivEl("content-item-preview-panel");

            this.progress = new api.ui.ProgressBar();
            imagePreviewContainer.appendChild(this.progress);

            this.error = new api.dom.DivEl("error");
            imagePreviewContainer.appendChild(this.error);

            this.imagePreviewContainer = imagePreviewContainer;

            this.resetPreviewContainerMaxHeight();
        }

        private resetPreviewContainerMaxHeight() {
            //limiting image modal dialog height up to screen size except padding on top and bottom
            //so 340 is 300px content of image modal dialog except preview container + 20*2 from top and bottom of screen
            var maxImagePreviewHeight = wemjq(window).height() - 340;
            new api.dom.ElementHelper(this.imagePreviewContainer.getHTMLElement()).setMaxHeightPx(maxImagePreviewHeight);
        }


        private getCaption(): string {
            if (this.imageElement) {
                return wemjq(this.imageElement.parentElement).children("figcaption").text();
            }
            else {
                return api.util.StringHelper.EMPTY_STRING;
            }
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
            var submitAction = new api.ui.Action(this.imageElement ? "Update" : "Insert");
            this.setSubmitAction(submitAction);
            this.addAction(submitAction.onExecuted(() => {
                if (this.validate()) {
                    this.createImageTag();
                    this.close();
                }
            }));

            super.initializeActions();
        }

        private getCaptionFieldValue() {
            return (<api.dom.InputEl>this.imageCaptionField.getInput()).getValue().trim();
        }

        private isImageWiderThanEditor() {
            if (!!this.getEditor()["editorContainer"]) {
                return (this.image.getHTMLElement()["width"] > this.getEditor()["editorContainer"].clientWidth);
            }
            else if (!!this.getEditor() && this.getEditor()["inline"] === true) {
                return (this.image.getHTMLElement()["width"] > this.getEditor()["bodyElement"].clientWidth);
            }
            return true;
        }

        private createFigureElement() {

            var figure = api.dom.ElementHelper.fromName("figure");
            var figCaption = api.dom.ElementHelper.fromName("figcaption");
            figCaption.setText(this.getCaptionFieldValue());
            figCaption.setAttribute("style", "text-align: center");
            this.image.setId("__mcenew");

            figure.appendChildren([(<api.dom.ImgEl>this.image).getEl().getHTMLElement(), figCaption.getHTMLElement()]);

            return figure;
        }

        private createImageTag(): void {
            var figure = this.createFigureElement();

            api.util.htmlarea.editor.HTMLAreaHelper.updateImageParentAlignment(this.image.getHTMLElement());
            this.setImageWidthConstraint();

            var img = this.callback(figure.getHTMLElement().outerHTML);
            api.util.htmlarea.editor.HTMLAreaHelper.changeImageParentAlignmentOnImageAlignmentChange(img);
        }

        private setImageWidthConstraint() {
            var keepImageSize = this.isImageInOriginalSize(this.image.getHTMLElement());
            this.image.getHTMLElement().style["width"] = (this.isImageWiderThanEditor() || !keepImageSize) ? "100%" : "auto";
        }

        private isImageInOriginalSize(image: HTMLElement) {
            return image.getAttribute("data-src").indexOf("keepSize=true") > 0;
        }
    }

    export class ImageToolbar extends api.ui.toolbar.Toolbar {

        private image: api.dom.ImgEl;

        private justifyButton: api.ui.button.ActionButton;

        private alignLeftButton: api.ui.button.ActionButton;

        private centerButton: api.ui.button.ActionButton;

        private alignRightButton: api.ui.button.ActionButton;

        private keepOriginalSizeCheckbox: api.ui.Checkbox;

        private imageCroppingSelector: ImageCroppingSelector;

        private imageLoadMask: api.ui.mask.LoadMask;

        constructor(image: api.dom.ImgEl, imageLoadMask: api.ui.mask.LoadMask) {
            super("image-toolbar");

            this.image = image;
            this.imageLoadMask = imageLoadMask;

            super.addElement(this.justifyButton = this.createJustifiedButton());
            super.addElement(this.alignLeftButton = this.createLeftAlignedButton());
            super.addElement(this.centerButton = this.createCenteredButton());
            super.addElement(this.alignRightButton = this.createRightAlignedButton());
            super.addElement(this.keepOriginalSizeCheckbox = this.createKeepOriginalSizeCheckbox());
            super.addElement(this.imageCroppingSelector = this.createImageCroppingSelector());

            this.initKeepSizeCheckbox();
            this.initActiveButton();
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
                this.image.getHTMLElement().style.textAlign = this.getImageAlignment();
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });

            return button;
        }

        private createKeepOriginalSizeCheckbox(): api.ui.Checkbox {
            var keepOriginalSizeCheckbox = new api.ui.Checkbox();
            keepOriginalSizeCheckbox.addClass('keep-size-check');
            keepOriginalSizeCheckbox.onValueChanged(() => {
                this.imageLoadMask.show();
                this.rebuildImgSrcParams();
                this.rebuildImgDataSrcParams();
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });
            keepOriginalSizeCheckbox.setLabel('Keep original size');

            return keepOriginalSizeCheckbox;
        }

        private createImageCroppingSelector(): ImageCroppingSelector {
            var imageCroppingSelector: ImageCroppingSelector = new ImageCroppingSelector();

            this.initSelectedCropping(imageCroppingSelector);

            imageCroppingSelector.onOptionSelected((event: OptionSelectedEvent<ImageCroppingOption>) => {
                this.imageLoadMask.show();
                this.rebuildImgSrcParams();
                this.rebuildImgDataSrcParams();
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });

            return imageCroppingSelector;
        }

        private initSelectedCropping(imageCroppingSelector: ImageCroppingSelector) {
            var imgSrc: string = this.image.getEl().getAttribute("src");
            var scalingApplied: boolean = imgSrc.indexOf("scale=") > 0;
            if (scalingApplied) {
                var scaleParamValue = api.util.UriHelper.decodeUrlParams(imgSrc.replace("&amp;", "&"))["scale"];
                var scaleOption = ImageCroppingOptions.getOptionByProportion(scaleParamValue);
                if (!!scaleOption) {
                    imageCroppingSelector.selectOption(imageCroppingSelector.getOptionByValue(scaleOption.getName()));
                }
            }
        }

        private initActiveButton() {
            var alignment = this.image.getHTMLElement().style.textAlign;

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

        private initKeepSizeCheckbox() {
            this.keepOriginalSizeCheckbox.setChecked(this.image.getEl().getAttribute("data-src").indexOf("keepSize=true") > 0);
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

        private rebuildImgSrcParams() {
            var imgSrc = this.image.getEl().getAttribute("src"),
                newSrc = api.util.UriHelper.trimUrlParams(imgSrc),
                isCroppingSelected: boolean = !!this.imageCroppingSelector.getSelectedOption(),
                keepOriginalSizeChecked: boolean = this.keepOriginalSizeCheckbox.isChecked();

            if (isCroppingSelected) {
                var imageCroppingOption: ImageCroppingOption = this.imageCroppingSelector.getSelectedOption().displayValue;
                newSrc = newSrc + "?scale=" + imageCroppingOption.getProportionString() +
                         (keepOriginalSizeChecked ? "" : "&size=640");
            }
            else {
                newSrc = newSrc + (keepOriginalSizeChecked ? "?scaleWidth=true" : "?size=640&scaleWidth=true");
            }

            this.image.getEl().setAttribute("src", newSrc);
        }

        private rebuildImgDataSrcParams() {
            var dataSrc = this.image.getEl().getAttribute("data-src"),
                newDataSrc = api.util.UriHelper.trimUrlParams(dataSrc),
                isCroppingSelected: boolean = !!this.imageCroppingSelector.getSelectedOption(),
                keepOriginalSizeChecked: boolean = this.keepOriginalSizeCheckbox.isChecked();

            if (isCroppingSelected) {
                var imageCroppingOption: ImageCroppingOption = this.imageCroppingSelector.getSelectedOption().displayValue;
                newDataSrc = newDataSrc + "?scale=" + imageCroppingOption.getProportionString() +
                             (keepOriginalSizeChecked ? "&keepSize=true" : "&size=640");
            }
            else {
                newDataSrc = newDataSrc + (keepOriginalSizeChecked ? "?keepSize=true" : "");
            }

            this.image.getEl().setAttribute("data-src", newDataSrc);
        }

        onCroppingChanged(listener: (event: OptionSelectedEvent<ImageCroppingOption>) => void) {
            this.imageCroppingSelector.onOptionSelected(listener);
        }

    }

    export class ImagePreviewScrollHandler {

        private imagePreviewContainer: api.dom.DivEl;

        private scrollDownButton: api.dom.Element;
        private scrollUpButton: api.dom.Element;
        private scrollBarWidth: number;
        private scrollBarRemoveTimeoutId: number;
        private scrolling;

        constructor(imagePreviewContainer: api.dom.DivEl) {
            this.imagePreviewContainer = imagePreviewContainer;

            this.initializeImageScrollNavigation();

            this.imagePreviewContainer.onScroll(() => {
                this.toggleScrollButtons();
                this.showScrollBar();
                this.removeScrollBarOnTimeout();
            });
        }

        private initializeImageScrollNavigation() {
            this.scrollDownButton = this.createScrollButton("down");
            this.scrollUpButton = this.createScrollButton("up");
            this.initScrollbarWidth();
        }

        private isScrolledToTop(): boolean {
            var element = this.imagePreviewContainer.getHTMLElement();
            return element.scrollTop === 0;
        }

        private isScrolledToBottom(): boolean {
            var element = this.imagePreviewContainer.getHTMLElement();
            return (element.scrollHeight - element.scrollTop) === element.clientHeight;
        }

        private createScrollButton(direction: string): api.dom.Element {
            var scrollAreaDiv = new api.dom.DivEl(direction === "up" ? "scroll-up-div" : "scroll-down-div"),
                imageEl = new api.dom.ImgEl(api.util.UriHelper.getAdminUri("common/images/icons/512x512/arrow_" + direction + ".png")),
                scrollTop = (direction === "up" ? "-=50" : "+=50");

            scrollAreaDiv.appendChild(imageEl);

            imageEl.onClicked((event) => {
                event.preventDefault();
                wemjq(this.imagePreviewContainer.getHTMLElement()).animate({scrollTop: scrollTop}, 400);
            });

            imageEl.onMouseOver(() => {
                this.scrolling = true;
                this.scrollImagePreview(direction);
            });

            imageEl.onMouseOut(() => {
                this.scrolling = false;
            });

            direction === "up"
                ? wemjq(scrollAreaDiv.getHTMLElement()).insertBefore(this.imagePreviewContainer.getHTMLElement().parentElement)
                : wemjq(scrollAreaDiv.getHTMLElement()).insertAfter(this.imagePreviewContainer.getHTMLElement().parentElement);

            scrollAreaDiv.hide();

            return scrollAreaDiv;
        }

        private initScrollbarWidth() {
            var outer = document.createElement("div");
            outer.style.visibility = "hidden";
            outer.style.width = "100px";
            outer.style.msOverflowStyle = "scrollbar"; // needed for WinJS apps

            document.body.appendChild(outer);

            var widthNoScroll = outer.offsetWidth;
            // force scrollbars
            outer.style.overflow = "scroll";

            // add innerdiv
            var inner = document.createElement("div");
            inner.style.width = "100%";
            outer.appendChild(inner);

            var widthWithScroll = inner.offsetWidth;

            // remove divs
            outer.parentNode.removeChild(outer);

            this.scrollBarWidth = widthNoScroll - widthWithScroll;
        }

        private scrollImagePreview(direction, scrollBy: number = 2) {
            var scrollByPx = (direction === "up" ? "-=" : "+=") + Math.round(scrollBy) + "px";
            var delta = 0.05;
            wemjq(this.imagePreviewContainer.getHTMLElement()).animate({scrollTop: scrollByPx}, 1, () => {
                if (this.scrolling) {
                    // If we want to keep scrolling, call the scrollContent function again:
                    this.scrollImagePreview(direction, scrollBy + delta);   // Increase scroll height by delta on each iteration
                                                                            // to emulate scrolling speed up effect
                }
            });
        }

        setMarginRight() {
            this.imagePreviewContainer.getEl().setMarginRight("");
            if (this.scrollDownButton.isVisible() || this.scrollUpButton.isVisible()) {
                this.imagePreviewContainer.getEl().setMarginRight("-" + this.scrollBarWidth + "px");
            }
        }

        toggleScrollButtons() {
            if (this.isScrolledToBottom()) {
                this.scrollDownButton.hide();
            }
            else {
                this.scrollDownButton.show();
            }

            if (this.isScrolledToTop()) {
                this.scrollUpButton.hide();
            }
            else {
                this.scrollUpButton.show();
            }
        }

        resetScrollPosition() {
            this.imagePreviewContainer.getEl().setScrollTop(0);
        }

        private showScrollBar() {
            this.imagePreviewContainer.getHTMLElement().parentElement.style.marginRight = "-" + this.scrollBarWidth + "px";
            this.imagePreviewContainer.getEl().setMarginRight("");
            this.imagePreviewContainer.getHTMLElement().style.overflowY = "auto";
        }

        private removeScrollBarOnTimeout() {
            if (!!this.scrollBarRemoveTimeoutId) {
                window.clearTimeout(this.scrollBarRemoveTimeoutId);
            }

            this.scrollBarRemoveTimeoutId = window.setTimeout(() => {
                this.imagePreviewContainer.getHTMLElement().parentElement.style.marginRight = "";
                this.imagePreviewContainer.getEl().setMarginRight("-" + this.scrollBarWidth + "px");
                this.imagePreviewContainer.getHTMLElement().style.overflowY = "auto";
            }, 500);
        }
    }
}