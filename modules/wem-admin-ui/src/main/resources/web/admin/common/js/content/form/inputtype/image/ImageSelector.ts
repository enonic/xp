module api.content.form.inputtype.image {

    import ContentSummary = api.content.ContentSummary;
    import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Option = api.ui.selector.Option;

    export interface ImageSelectorConfig {
        relationshipType: string
    }

    export class ImageSelector extends api.form.inputtype.support.BaseInputTypeManagingAdd {

        private config: api.content.form.inputtype.ContentInputTypeViewContext<ImageSelectorConfig>;

        private input: api.form.Input;

        private comboBox: ComboBox<ImageSelectorDisplayValue>;

        private uploadButton: api.dom.DivEl;

        private selectedOptionsView: SelectedOptionsView;

        private contentSummaryLoader: api.content.ContentSummaryLoader;

        private contentRequestsAllowed: boolean;

        private uploadDialog: ImageSelectorUploadDialog;

        private editContentRequestListeners: {(content: ContentSummary): void }[] = [];

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        private layoutInProgress: boolean;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext<ImageSelectorConfig>) {
            super("image-selector");
            this.addClass("input-type-view");

            this.config = config;
            this.contentSummaryLoader = new api.content.ContentSummaryLoader();
            this.contentSummaryLoader.onLoadingData((event: LoadingDataEvent) => {
                this.comboBox.setEmptyDropdownText("Searching...");
            });
            this.contentSummaryLoader.onLoadedData((event: LoadedDataEvent<ContentSummary>) => {
                var options = this.createOptions(event.getData());

                this.comboBox.setOptions(options);
            });


            // requests aren't allowed until allowed contentTypes are specified
            this.contentRequestsAllowed = false;

            var name = new api.schema.relationshiptype.RelationshipTypeName("default");

            if (config.inputConfig.relationshipType != null) {
                name = new api.schema.relationshiptype.RelationshipTypeName(config.inputConfig.relationshipType);
            }
            new api.schema.relationshiptype.GetRelationshipTypeByNameRequest(name).sendAndParse()
                .done((relationshipType: api.schema.relationshiptype.RelationshipType) => {
                    this.comboBox.setInputIconUrl(relationshipType.getIconUrl());
                    this.contentSummaryLoader.setAllowedContentTypes(relationshipType.getAllowedToTypes());
                    this.contentRequestsAllowed = true;
                    this.loadOptions("");
                });

            this.uploadDialog = new ImageSelectorUploadDialog();
            this.uploadDialog.onUploadStarted((event: api.ui.uploader.ImageUploadStartedEvent) => {
                this.uploadDialog.close();

                event.getUploadedItems().forEach((uploadItem: api.ui.uploader.UploadItem) => {
                    var value = ImageSelectorDisplayValue.fromUploadItem(uploadItem);
                    var option = <api.ui.selector.Option<ImageSelectorDisplayValue>>{
                        value: value.getId(),
                        displayValue: value
                    };
                    this.comboBox.selectOption(option);
                });
            });
            this.uploadDialog.onUploadProgress((event: api.ui.uploader.ImageUploadProgressEvent) => {
                var selectedOption = this.selectedOptionsView.getById(event.getUploadItem().getId());
                (<SelectedOptionView>selectedOption.getOptionView()).setProgress(event.getUploadItem().getProgress());
            });
            this.uploadDialog.onImageUploaded((event: api.ui.uploader.ImageUploadedEvent) => {
                this.createImageContent(event.getUploadedItem());
            });

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                this.availableSizeChanged();
            });

            // Don't forget to clean up the modal dialog on remove
            this.onRemoved((event) => {
                this.uploadDialog.remove();
                ResponsiveManager.unAvailableSizeChanged(this);
            });
        }

        availableSizeChanged() {
            this.selectedOptionsView.updateLayout();
        }

        newInitialValue(): api.data.Value {
            return null;
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.layoutInProgress = true;
            this.input = input;

            var comboboxWrapper = new api.dom.DivEl("combobox-wrapper");

            this.comboBox = this.createComboBox(input);
            comboboxWrapper.appendChild(this.comboBox);
            this.comboBox.onHidden((event: api.dom.ElementHiddenEvent) => {
                // hidden on max occurences reached
                this.uploadButton.hide();
            });
            this.comboBox.onShown((event: api.dom.ElementShownEvent) => {
                // shown on occurences between min and max
                this.uploadButton.show();
            });

            this.uploadButton = new api.dom.DivEl();
            comboboxWrapper.appendChild(this.uploadButton);
            this.uploadButton.addClass("upload-button");
            this.uploadButton.onClicked((event: MouseEvent) => {
                var inputMaximum = input.getOccurrences().getMaximum();
                var countSelected = this.comboBox.countSelectedOptions();
                var rest = -1;
                if (inputMaximum == 0) {
                    rest = 0;
                } else {
                    rest = inputMaximum - countSelected;
                    rest = (rest == 0) ? -1 : rest;
                }
                this.uploadDialog.setMaximumOccurrences(rest);
                this.uploadDialog.open();
            });

            this.appendChild(comboboxWrapper);
            this.appendChild(this.selectedOptionsView);

            this.doLoadContent(properties).
                then((contents: ContentSummary[]) => {

                    contents.forEach((content: ContentSummary) => {
                        this.comboBox.selectOption(<Option<ImageSelectorDisplayValue>>{
                            value: content.getId(),
                            displayValue: ImageSelectorDisplayValue.fromContentSummary(content)
                        });
                    });

                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).finally(()=> {
                    this.layoutInProgress = false;
                }).done();
        }

        private doLoadContent(properties: api.data.Property[]): Q.Promise<ContentSummary[]> {

            if (!properties) {
                return Q(<ContentSummary[]> []);
            }
            else {
                var contentIds = properties.map((property: api.data.Property) => new api.content.ContentId(property.getString()));
                return new api.content.GetContentSummaryByIds(contentIds).get();
            }
        }

        getValues(): api.data.Value[] {
            return this.selectedOptionsView.getValues();
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            var recording = new api.form.inputtype.InputValidationRecording();

            var numberOfValids = this.comboBox.countSelectedOptions();
            if (numberOfValids < this.input.getOccurrences().getMinimum()) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (this.input.getOccurrences().maximumBreached(numberOfValids)) {
                recording.setBreaksMaximumOccurrences(true);
            }

            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
                }
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        giveFocus(): boolean {
            if (this.comboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.comboBox.giveFocus();
        }

        private createComboBox(input: api.form.Input): ComboBox<ImageSelectorDisplayValue> {

            this.selectedOptionsView = new SelectedOptionsView();
            this.selectedOptionsView.onEditSelectedOptions((options: SelectedOption<ImageSelectorDisplayValue>[]) => {
                options.forEach((option: SelectedOption<ImageSelectorDisplayValue>) => {
                    this.notifyEditContentRequested(option.getOption().displayValue.getContentSummary());
                });
            });

            this.selectedOptionsView.onRemoveSelectedOptions((options: SelectedOption<ImageSelectorDisplayValue>[]) => {
                options.forEach((option: SelectedOption<ImageSelectorDisplayValue>) => {
                    this.comboBox.removeSelectedOption(option.getOption());
                });
                this.validate(false);
            });

            this.selectedOptionsView.onValueChanged((event: api.form.inputtype.ValueChangedEvent) => {
                this.notifyValueChanged(event);
            });

            var comboBoxConfig = <ComboBoxConfig<ImageSelectorDisplayValue>> {
                hideComboBoxWhenMaxReached: true,
                optionDisplayValueViewer: new ImageSelectorViewer(),
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum(),
                delayedInputValueChangedHandling: 500
            };

            var comboBox = new ComboBox<ImageSelectorDisplayValue>(input.getName(), comboBoxConfig);

            this.loadOptions("");

            comboBox.onSelectedOptionRemoved((removed: SelectedOption<ImageSelectorDisplayValue>) => {
                this.notifyValueRemoved(removed.getIndex());
                this.validate(false);
            });

            comboBox.onOptionFilterInputValueChanged((event: api.ui.selector.OptionFilterInputValueChangedEvent<ImageSelectorDisplayValue>) => {
                this.loadOptions(event.getNewValue());
            });

            comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<ImageSelectorDisplayValue>) => {
                if (!this.layoutInProgress) {
                    var contentId = event.getOption().displayValue.getContentId();
                    if (!contentId) {
                        return;
                    }
                    var value = new api.data.Value(contentId, api.data.ValueTypes.CONTENT_ID);
                    this.notifyValueAdded(value);
                }
                this.validate(false);
            });

            return comboBox;
        }

        private loadOptions(searchString: string) {
            if (!this.contentRequestsAllowed || !this.comboBox) {
                return;
            }

            this.contentSummaryLoader.search(searchString);
        }

        private createOptions(contents: ContentSummary[]): Option<ImageSelectorDisplayValue>[] {
            return contents.map((content: ContentSummary) => {
                return {
                    value: content.getId(),
                    displayValue: ImageSelectorDisplayValue.fromContentSummary(content)
                };
            });
        }

        private createImageContent(uploadItem: api.ui.uploader.UploadItem) {

            new api.schema.content.GetContentTypeByNameRequest(new api.schema.content.ContentTypeName("image")).
                sendAndParse().
                then((contentType: api.schema.content.ContentType) => {

                    var attachmentName = new api.content.attachment.AttachmentName(uploadItem.getName());

                    var attachment = new api.content.attachment.AttachmentBuilder().
                        setBlobKey(uploadItem.getBlobKey()).
                        setAttachmentName(attachmentName).
                        setMimeType(uploadItem.getMimeType()).
                        setSize(uploadItem.getSize()).
                        build();

                    var contentData = new api.content.image.ImageContentDataFactory().
                        setImage(attachmentName).
                        setMimeType(uploadItem.getMimeType()).
                        create();

                    var createContentRequest = new api.content.CreateContentRequest().
                        setDraft(false).
                        setParent(this.config.contentPath).
                        setName(api.content.ContentName.fromString(api.content.ContentName.ensureValidContentName(attachmentName.toString()))).
                        setContentType(contentType.getContentTypeName()).
                        setDisplayName(attachmentName.toString()).
                        setForm(contentType.getForm()).
                        setContentData(contentData).
                        addAttachment(attachment);

                    return createContentRequest.sendAndParse();

                }).then((createdContent: api.content.Content) => {

                    var value = ImageSelectorDisplayValue.fromContentSummary(createdContent, uploadItem);
                    this.comboBox.selectOption({
                        value: value.getId(),
                        displayValue: value
                    });

                }).catch((reason: any) => {

                    api.DefaultErrorHandler.handle(reason);

                }).done();
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.comboBox.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.comboBox.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.comboBox.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.comboBox.unBlur(listener);
        }

        onEditContentRequest(listener: (content: ContentSummary) => void) {
            this.editContentRequestListeners.push(listener);
        }

        unEditContentRequest(listener: (content: ContentSummary) => void) {
            this.editContentRequestListeners = this.editContentRequestListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyEditContentRequested(content: ContentSummary) {
            this.editContentRequestListeners.forEach((listener) => {
                listener(content);
            });
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("ImageSelector", ImageSelector));

}
