module api.util.htmlarea.dialog {

    import FormItemBuilder = api.ui.form.FormItemBuilder;
    import FormItem = api.ui.form.FormItem;
    import Validators = api.ui.form.Validators;
    import FileUploadedEvent = api.ui.uploader.FileUploadedEvent;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import FileUploadProgressEvent = api.ui.uploader.FileUploadProgressEvent;
    import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
    import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import Action = api.ui.Action;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
    import ContentSummary = api.content.ContentSummary;
    import Content = api.content.Content;
    import Option = api.ui.selector.Option;
    import i18n = api.util.i18n;
    import ContentSelectorLoader = api.content.form.inputtype.contentselector.ContentSelectorLoader;
    import ContentSummaryLoader = api.content.resource.ContentSummaryLoader;
    import ImageSelectorDisplayValue = api.content.image.ImageSelectorDisplayValue;
    import ContentSelectedOptionsView = api.content.ContentSelectedOptionsView;

    export class ImageModalDialog extends ModalDialog {

        private imagePreviewContainer: api.dom.DivEl;
        private imageCaptionField: FormItem;
        private imageAltTextField: FormItem;
        private imageUploaderEl: api.content.image.ImageUploaderEl;
        private imageElement: HTMLImageElement;
        private content: api.content.ContentSummary;
        private imageSelector: api.content.image.ImageContentComboBox;
        private progress: api.ui.ProgressBar;
        private error: api.dom.DivEl;
        private image: api.dom.ImgEl;
        private elementContainer: HTMLElement;
        private callback: Function;
        private imageToolbar: ImageToolbar;
        private imagePreviewScrollHandler: ImagePreviewScrollHandler;
        private imageLoadMask: api.ui.mask.LoadMask;
        private dropzoneContainer: api.ui.uploader.DropzoneContainer;
        private imageSelectorFormItem: FormItem;

        static imagePrefix: string = 'image://';
        static maxImageWidth: number = 640;

        constructor(config: HtmlAreaImage, content: api.content.ContentSummary) {
            super(<ImageModalDialogConfig>{
                config: config,
                editor: config.editor,
                content: content,
                title: i18n('dialog.image.title'),
                cls: 'image-modal-dialog'
            });

            this.initLoader();
        }

        private setImageFieldValues(field: FormItem, value: string) {
            (<api.dom.InputEl>field.getInput()).setValue(value);
        }

        private initLoader() {
            this.imageUploaderEl.setParams({
                parent: this.content.getContentId().toString()
            });
        }

        private loadImage() {
            let loader = <ContentSummaryLoader>this.imageSelector.getLoader();

            let singleLoadListener = (event: api.util.loader.event.LoadedDataEvent<api.content.ContentSummary>) => {
                let imageContent = this.getImageContent(event.getData());
                if (imageContent) {
                    this.imageSelector.setValue(imageContent.getId());
                    this.createImgElForExistingImage(imageContent);
                    this.previewImage();
                    this.imageSelectorFormItem.addClass('selected-item-preview');
                }
                loader.unLoadedData(singleLoadListener);
            };
            loader.onLoadedData(singleLoadListener);

            loader.load();
        }

        protected getMainFormItems(): FormItem[] {
            this.imageSelectorFormItem = this.createImageSelector('imageId');

            this.addUploaderAndPreviewControls();
            this.setFirstFocusField(this.imageSelectorFormItem.getInput());

            this.imageCaptionField = this.createFormItem(new ModalDialogFormItemBuilder('caption', i18n('dialog.image.formitem.caption')));
            this.imageAltTextField = this.createFormItem(new ModalDialogFormItemBuilder('altText', i18n('dialog.image.formitem.alttext')));

            this.imageCaptionField.addClass('caption').hide();
            this.imageAltTextField.addClass('alttext').hide();

            return [
                this.imageSelectorFormItem,
                this.imageCaptionField,
                this.imageAltTextField
            ];
        }

        private createImageSelector(id: string): FormItem {

            let imageSelector = api.content.image.ImageContentComboBox.create().setMaximumOccurrences(1).setContent(
                this.content).setSelectedOptionsView(new api.content.ContentSelectedOptionsView()).setTreegridDropdownEnabled(true).build();

            let formItemBuilder = new ModalDialogFormItemBuilder(id, i18n('dialog.image.formitem.image')).setValidator(
                Validators.required).setInputEl(imageSelector);

            let formItem = this.createFormItem(formItemBuilder);
            let imageSelectorComboBox = imageSelector.getComboBox();

            imageSelector.getComboBox().getInput().setPlaceholder(i18n('field.image.option.placeholder'));

            this.imageSelector = imageSelector;

            formItem.addClass('image-selector');

            imageSelectorComboBox.onOptionSelected((event: SelectedOptionEvent<ImageSelectorDisplayValue>) => {
                let imageContent = event.getSelectedOption().getOption().displayValue;
                if (!imageContent.getContentId()) {
                    return;
                }

                this.imageLoadMask.show();
                this.createImgElForNewImage(imageContent.getContentSummary());
                this.previewImage();
                formItem.addClass('selected-item-preview');
                this.setAltTextFieldValue(imageContent.getDisplayName());
                this.fetchImageCaption(imageContent.getContentSummary()).then(value => this.setCaptionFieldValue(value)).catch(
                    (reason: any) => api.DefaultErrorHandler.handle(reason)).done();
            });

            imageSelectorComboBox.onOptionDeselected(() => {
                formItem.removeClass('selected-item-preview');
                this.displayValidationErrors(false);
                this.removePreview();
                this.imageToolbar.remove();
                this.imageCaptionField.hide();
                this.imageAltTextField.hide();
                this.imageUploaderEl.show();
                this.imagePreviewScrollHandler.toggleScrollButtons();
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });

            return formItem;
        }

        private addUploaderAndPreviewControls() {
            let imageSelectorContainer = this.imageSelectorFormItem.getInput().getParentElement();

            imageSelectorContainer.appendChild(this.imageUploaderEl = this.createImageUploader());
            this.initDragAndDropUploaderEvents();

            this.createImagePreviewContainer();

            let scrollBarWrapperDiv = new api.dom.DivEl('preview-panel-scrollbar-wrapper');
            scrollBarWrapperDiv.appendChild(this.imagePreviewContainer);
            let scrollNavigationWrapperDiv = new api.dom.DivEl('preview-panel-scroll-navigation-wrapper');
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
            let filteredImages = images.filter((image: api.content.ContentSummary) => {
                return this.imageElement.src.indexOf(image.getId()) > 0;
            });

            return filteredImages.length > 0 ? filteredImages[0] : null;
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
                this.imagePreviewContainer.removeClass('upload');
                wemjq(this.imageToolbar.getHTMLElement()).insertBefore(
                    this.imagePreviewContainer.getHTMLElement().parentElement.parentElement);
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
                if (this.getCaptionFieldValue() === '') {
                    this.imageCaptionField.getEl().scrollIntoView();
                    this.imageCaptionField.getInput().giveFocus();
                }
            });

            this.hideUploadMasks();
            this.imageCaptionField.show();
            this.imageAltTextField.show();
            this.imageUploaderEl.hide();
            this.imagePreviewContainer.insertChild(this.image, 0);
        }

        private createImgElForPreview(imageContent: api.content.ContentSummary, isExistingImg: boolean = false): api.dom.ImgEl {
            let imgSrcAttr = isExistingImg
                ? new api.dom.ElementHelper(this.imageElement).getAttribute('src')
                : this.generateDefaultImgSrc(imageContent.getContentId().toString());
            let imgDataSrcAttr = isExistingImg
                ? new api.dom.ElementHelper(this.imageElement).getAttribute('data-src')
                : ImageModalDialog.imagePrefix + imageContent.getContentId().toString();

            let imageEl = new api.dom.ImgEl(imgSrcAttr);
            imageEl.getEl().setAttribute('alt', imageContent.getDisplayName());
            imageEl.getEl().setAttribute('data-src', imgDataSrcAttr);

            let imageAlignment = isExistingImg ? (this.imageElement.style.textAlign ||
                                                  this.imageElement.parentElement.style.cssFloat) : 'justify';
            imageEl.getHTMLElement().style.textAlign = imageAlignment;

            return imageEl;
        }

        private generateDefaultImgSrc(contentId: string): string {
            return new api.content.util.ContentImageUrlResolver().setContentId(new api.content.ContentId(contentId)).setScaleWidth(
                true).setSize(
                ImageModalDialog.maxImageWidth).resolve();
        }

        private removePreview() {
            this.imagePreviewContainer.removeChild(this.image);
        }

        show() {
            super.show();

            this.imageUploaderEl.show();
        }

        private createImagePreviewContainer() {
            let imagePreviewContainer = new api.dom.DivEl('content-item-preview-panel');

            this.progress = new api.ui.ProgressBar();
            imagePreviewContainer.appendChild(this.progress);

            this.error = new api.dom.DivEl('error');
            imagePreviewContainer.appendChild(this.error);

            this.imagePreviewContainer = imagePreviewContainer;

            this.resetPreviewContainerMaxHeight();
        }

        private resetPreviewContainerMaxHeight() {
            //limiting image modal dialog height up to screen size except padding on top and bottom
            //so 340 is 300px content of image modal dialog except preview container + 20*2 from top and bottom of screen
            let maxImagePreviewHeight = wemjq(window).height() - 440;
            new api.dom.ElementHelper(this.imagePreviewContainer.getHTMLElement()).setMaxHeightPx(maxImagePreviewHeight);
        }

        private getCaption(): string {
            if (this.imageElement) {
                return wemjq(this.imageElement.parentElement).children('figcaption').text();
            } else {
                return api.util.StringHelper.EMPTY_STRING;
            }
        }

        private getAltText(): string {
            if (this.imageElement) {
                return this.imageElement.alt;
            } else {
                return api.util.StringHelper.EMPTY_STRING;
            }
        }

        private createImageUploader(): api.content.image.ImageUploaderEl {
            let uploader = new api.content.image.ImageUploaderEl({
                operation: api.ui.uploader.MediaUploaderElOperation.create,
                name: 'image-selector-upload-dialog',
                showResult: false,
                maximumOccurrences: 1,
                allowMultiSelection: false,
                deferred: true,
                showCancel: false,
                selfIsDropzone: false
            });

            this.dropzoneContainer = new api.ui.uploader.DropzoneContainer(true);
            this.dropzoneContainer.hide();
            this.appendChild(this.dropzoneContainer);

            uploader.addDropzone(this.dropzoneContainer.getDropzone().getId());

            uploader.hide();

            uploader.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                this.hideUploadMasks();
                this.imagePreviewContainer.addClass('upload');
                this.showProgress();
            });

            uploader.onUploadProgress((event: FileUploadProgressEvent<Content>) => {
                let item = event.getUploadItem();

                this.setProgress(item.getProgress());
            });

            uploader.onFileUploaded((event: FileUploadedEvent<Content>) => {
                let item = event.getUploadItem();
                let createdContent = item.getModel();

                //new api.content.ContentUpdatedEvent(this.contentId).fire();
                this.imageSelector.setContent(createdContent);
            });

            uploader.onUploadFailed((event: FileUploadFailedEvent<Content>) => {
                this.showError('Upload failed');
            });

            return uploader;
        }

        private initDragAndDropUploaderEvents() {
            let dragOverEl;
            this.onDragEnter((event: DragEvent) => {
                if (this.imageUploaderEl.isEnabled()) {
                    let target = <HTMLElement> event.target;

                    if (!!dragOverEl || dragOverEl === this.getHTMLElement()) {
                        this.dropzoneContainer.show();
                    }
                    dragOverEl = target;
                }
            });

            this.imageUploaderEl.onDropzoneDragLeave(() => this.dropzoneContainer.hide());
            this.imageUploaderEl.onDropzoneDrop(() => this.dropzoneContainer.hide());
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

        protected initializeConfig(params: ImageModalDialogConfig) {
            super.initializeConfig(params);

            const config = params.config;

            this.elementContainer = config.container;
            this.callback = config.callback;

            this.content = params.content;

            if (config.element) {
                this.imageElement = <HTMLImageElement>params.config.element;

                this.loadImage();

                this.setImageFieldValues(this.imageCaptionField, this.getCaption());
                this.setImageFieldValues(this.imageAltTextField, this.getAltText());
            }
        }

        protected initializeActions() {
            let submitAction = new api.ui.Action(this.imageElement ? 'Update' : 'Insert');
            this.setSubmitAction(submitAction);
            this.addAction(submitAction.onExecuted(() => {
                this.displayValidationErrors(true);
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

        private setCaptionFieldValue(value: string) {
            (<api.dom.InputEl>this.imageCaptionField.getInput()).setValue(value);
        }

        private getAltTextFieldValue() {
            return (<api.dom.InputEl>this.imageAltTextField.getInput()).getValue().trim();
        }

        private setAltTextFieldValue(value: string) {
            (<api.dom.InputEl>this.imageAltTextField.getInput()).setValue(value);
        }

        private fetchImageCaption(imageContent: ContentSummary): wemQ.Promise<string> {
            return new api.content.resource.GetContentByIdRequest(imageContent.getContentId()).sendAndParse()
                .then((content: api.content.Content) => {
                    return this.getDescriptionFromImageContent(content) || content.getProperty('caption').getString() || '';
                });
        }

        private getDescriptionFromImageContent(imageContent: Content): string {
            let imageInfoMixin = new api.schema.mixin.MixinName('media:imageInfo');
            let imageInfoData = imageContent.getExtraData(imageInfoMixin);

            if (!imageInfoData || !imageInfoData.getData()) {
                return null;
            }

            let descriptionProperty = imageInfoData.getData().getProperty('description');

            if (descriptionProperty) {
                let description = descriptionProperty.getString();
                if (description) {
                    return description;
                }
            }

            return null;
        }

        private isImageWiderThanEditor() {
            if (!!this.getEditor()['editorContainer']) {
                return (this.image.getHTMLElement()['width'] > this.getEditor()['editorContainer'].clientWidth);
            } else if (!!this.getEditor() && this.getEditor()['inline'] === true) {
                return (this.image.getHTMLElement()['width'] > this.getEditor()['bodyElement'].clientWidth);
            }
            return true;
        }

        private createFigureElement() {

            let figure = api.dom.ElementHelper.fromName('figure');
            let figCaption = api.dom.ElementHelper.fromName('figcaption');
            figCaption.setText(this.getCaptionFieldValue());
            figCaption.setAttribute('style', 'text-align: left');
            this.image.setId('__mcenew');
            (<HTMLImageElement>this.image.getHTMLElement()).alt = this.getAltTextFieldValue();

            figure.appendChildren([(<api.dom.ImgEl>this.image).getEl().getHTMLElement(), figCaption.getHTMLElement()]);

            return figure;
        }

        private createImageTag(): void {
            let figure = this.createFigureElement();

            api.util.htmlarea.editor.HTMLAreaHelper.updateImageParentAlignment(this.image.getHTMLElement());
            this.setImageWidthConstraint();

            let img = this.callback(figure.getHTMLElement());
            api.util.htmlarea.editor.HTMLAreaHelper.changeImageParentAlignmentOnImageAlignmentChange(img);
        }

        private setImageWidthConstraint() {
            let keepImageSize = this.isImageInOriginalSize(this.image.getHTMLElement());
            this.image.getHTMLElement().style['width'] = (this.isImageWiderThanEditor() || !keepImageSize) ? '100%' : 'auto';
        }

        private isImageInOriginalSize(image: HTMLElement) {
            return image.getAttribute('data-src').indexOf('keepSize=true') > 0;
        }
    }

    export class ImageModalDialogConfig extends HtmlAreaModalDialogConfig {

        config: HtmlAreaImage;

        content: api.content.ContentSummary;
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
            super('image-toolbar');

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
            return this.createAlignmentButton('icon-paragraph-justify');
        }

        private createLeftAlignedButton(): api.ui.button.ActionButton {
            return this.createAlignmentButton('icon-paragraph-left');
        }

        private createCenteredButton(): api.ui.button.ActionButton {
            return this.createAlignmentButton('icon-paragraph-center');
        }

        private createRightAlignedButton(): api.ui.button.ActionButton {
            return this.createAlignmentButton('icon-paragraph-right');
        }

        private createAlignmentButton(iconClass: string): api.ui.button.ActionButton {
            let action: Action = new Action('');

            action.setIconClass(iconClass);

            let button = new api.ui.button.ActionButton(action);

            action.onExecuted(() => {
                this.resetActiveButton();
                button.addClass('active');
                this.image.getHTMLElement().style.textAlign = this.getImageAlignment();
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });

            return button;
        }

        private createKeepOriginalSizeCheckbox(): api.ui.Checkbox {
            let keepOriginalSizeCheckbox = api.ui.Checkbox.create().build();
            keepOriginalSizeCheckbox.addClass('keep-size-check');
            keepOriginalSizeCheckbox.onValueChanged(() => {
                this.imageLoadMask.show();
                this.rebuildImgSrcParams();
                this.rebuildImgDataSrcParams();
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });
            keepOriginalSizeCheckbox.setLabel(i18n('dialog.image.keepsize'));

            return keepOriginalSizeCheckbox;
        }

        private createImageCroppingSelector(): ImageCroppingSelector {
            let imageCroppingSelector: ImageCroppingSelector = new ImageCroppingSelector();

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
            let imgSrc: string = this.image.getEl().getAttribute('src');
            let scalingApplied: boolean = imgSrc.indexOf('scale=') > 0;
            if (scalingApplied) {
                let scaleParamValue = api.util.UriHelper.decodeUrlParams(imgSrc.replace('&amp;', '&'))['scale'];
                let scaleOption = ImageCroppingOptions.get().getOptionByProportion(scaleParamValue);
                if (!!scaleOption) {
                    imageCroppingSelector.selectOption(imageCroppingSelector.getOptionByValue(scaleOption.getName()));
                } else {
                    const customOption: Option<ImageCroppingOption> = imageCroppingSelector.addCustomScaleOption(scaleParamValue);
                    if (!!customOption) {
                        imageCroppingSelector.selectOption(customOption);
                    }
                }
            }
        }

        private initActiveButton() {
            let alignment = this.image.getHTMLElement().style.textAlign;

            switch (alignment) {
            case 'justify':
                this.justifyButton.addClass('active');
                break;
            case 'left':
                this.alignLeftButton.addClass('active');
                break;
            case 'center':
                this.centerButton.addClass('active');
                break;
            case 'right':
                this.alignRightButton.addClass('active');
                break;
            default:
                this.justifyButton.addClass('active');
                break;
            }
        }

        private resetActiveButton() {
            this.justifyButton.removeClass('active');
            this.alignLeftButton.removeClass('active');
            this.centerButton.removeClass('active');
            this.alignRightButton.removeClass('active');
        }

        private initKeepSizeCheckbox() {
            this.keepOriginalSizeCheckbox.setChecked(this.image.getEl().getAttribute('data-src').indexOf('keepSize=true') > 0);
        }

        private getImageAlignment(): string {
            if (this.justifyButton.hasClass('active')) {
                return 'justify';
            }

            if (this.alignLeftButton.hasClass('active')) {
                return 'left';
            }

            if (this.centerButton.hasClass('active')) {
                return 'center';
            }

            if (this.alignRightButton.hasClass('active')) {
                return 'right';
            }

            return 'justify';
        }

        private rebuildImgSrcParams() {
            let imgSrc = this.image.getEl().getAttribute('src');
            let newSrc = api.util.UriHelper.trimUrlParams(imgSrc);
            let isCroppingSelected: boolean = !!this.imageCroppingSelector.getSelectedOption();
            let keepOriginalSizeChecked: boolean = this.keepOriginalSizeCheckbox.isChecked();

            if (isCroppingSelected) {
                let imageCroppingOption: ImageCroppingOption = this.imageCroppingSelector.getSelectedOption().displayValue;
                newSrc = newSrc + '?scale=' + imageCroppingOption.getProportionString() +
                         (keepOriginalSizeChecked ? '' : '&size=640');
            } else {
                newSrc = newSrc + (keepOriginalSizeChecked ? '?scaleWidth=true' : '?size=640&scaleWidth=true');
            }

            this.image.getEl().setAttribute('src', newSrc);
        }

        private rebuildImgDataSrcParams() {
            let dataSrc = this.image.getEl().getAttribute('data-src');
            let newDataSrc = api.util.UriHelper.trimUrlParams(dataSrc);
            let isCroppingSelected: boolean = !!this.imageCroppingSelector.getSelectedOption();
            let keepOriginalSizeChecked: boolean = this.keepOriginalSizeCheckbox.isChecked();

            if (isCroppingSelected) {
                let imageCroppingOption: ImageCroppingOption = this.imageCroppingSelector.getSelectedOption().displayValue;
                newDataSrc = newDataSrc + '?scale=' + imageCroppingOption.getProportionString() +
                             (keepOriginalSizeChecked ? '&keepSize=true' : '&size=640');
            } else {
                newDataSrc = newDataSrc + (keepOriginalSizeChecked ? '?keepSize=true' : '');
            }

            this.image.getEl().setAttribute('data-src', newDataSrc);
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
        private scrolling: boolean;

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
            this.scrollDownButton = this.createScrollButton('down');
            this.scrollUpButton = this.createScrollButton('up');
            this.initScrollbarWidth();
        }

        private isScrolledToTop(): boolean {
            let element = this.imagePreviewContainer.getHTMLElement();
            return element.scrollTop === 0;
        }

        private isScrolledToBottom(): boolean {
            let element = this.imagePreviewContainer.getHTMLElement();
            return (element.scrollHeight - element.scrollTop) === element.clientHeight;
        }

        private createScrollButton(direction: string): api.dom.Element {
            let scrollAreaDiv = new api.dom.DivEl(direction === 'up' ? 'scroll-up-div' : 'scroll-down-div');
            let imageEl = new api.dom.ImgEl(api.util.UriHelper.getAdminUri('common/images/icons/512x512/arrow_' + direction + '.png'));
            let scrollTop = (direction === 'up' ? '-=50' : '+=50');

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

            direction === 'up'
                ? wemjq(scrollAreaDiv.getHTMLElement()).insertBefore(this.imagePreviewContainer.getHTMLElement().parentElement)
                : wemjq(scrollAreaDiv.getHTMLElement()).insertAfter(this.imagePreviewContainer.getHTMLElement().parentElement);

            scrollAreaDiv.hide();

            return scrollAreaDiv;
        }

        private initScrollbarWidth() {
            let outer = document.createElement('div');
            outer.style.visibility = 'hidden';
            outer.style.width = '100px';
            outer.style.msOverflowStyle = 'scrollbar'; // needed for WinJS apps

            document.body.appendChild(outer);

            let widthNoScroll = outer.offsetWidth;
            // force scrollbars
            outer.style.overflow = 'scroll';

            // add innerdiv
            let inner = document.createElement('div');
            inner.style.width = '100%';
            outer.appendChild(inner);

            let widthWithScroll = inner.offsetWidth;

            // remove divs
            outer.parentNode.removeChild(outer);

            this.scrollBarWidth = widthNoScroll - widthWithScroll;
        }

        private scrollImagePreview(direction: string, scrollBy: number = 2) {
            let scrollByPx = (direction === 'up' ? '-=' : '+=') + Math.round(scrollBy) + 'px';
            let delta = 0.05;
            wemjq(this.imagePreviewContainer.getHTMLElement()).animate({scrollTop: scrollByPx}, 1, () => {
                if (this.scrolling) {
                    // If we want to keep scrolling, call the scrollContent function again:
                    this.scrollImagePreview(direction, scrollBy + delta);   // Increase scroll height by delta on each iteration
                                                                            // to emulate scrolling speed up effect
                }
            });
        }

        setMarginRight() {
            this.imagePreviewContainer.getEl().setMarginRight('');
            if (this.scrollDownButton.isVisible() || this.scrollUpButton.isVisible()) {
                this.imagePreviewContainer.getEl().setMarginRight('-' + this.scrollBarWidth + 'px');
            }
        }

        toggleScrollButtons() {
            if (this.isScrolledToBottom()) {
                this.scrollDownButton.hide();
            } else {
                this.scrollDownButton.show();
            }

            if (this.isScrolledToTop()) {
                this.scrollUpButton.hide();
            } else {
                this.scrollUpButton.show();
            }
        }

        resetScrollPosition() {
            this.imagePreviewContainer.getEl().setScrollTop(0);
        }

        private showScrollBar() {
            this.imagePreviewContainer.getHTMLElement().parentElement.style.marginRight = '-' + this.scrollBarWidth + 'px';
            this.imagePreviewContainer.getEl().setMarginRight('');
            this.imagePreviewContainer.getHTMLElement().style.overflowY = 'auto';
        }

        private removeScrollBarOnTimeout() {
            if (!!this.scrollBarRemoveTimeoutId) {
                window.clearTimeout(this.scrollBarRemoveTimeoutId);
            }

            this.scrollBarRemoveTimeoutId = window.setTimeout(() => {
                this.imagePreviewContainer.getHTMLElement().parentElement.style.marginRight = '';
                this.imagePreviewContainer.getEl().setMarginRight('-' + this.scrollBarWidth + 'px');
                this.imagePreviewContainer.getHTMLElement().style.overflowY = 'auto';
            }, 500);
        }
    }
}
