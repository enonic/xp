module api.content.inputtype.image {

    import ContentSummary = api.content.ContentSummary;
    import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import ResponsiveManager = api.ui.ResponsiveManager;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export interface ImageSelectorConfig {
        relationshipType: string
    }

    export class ImageSelector extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private config: api.form.inputtype.InputTypeViewConfig<ImageSelectorConfig>;

        private input: api.form.Input;

        private comboBox: ComboBox<ContentSummary>;

        private uploadButton: api.ui.Button;

        private selectedOptionsView: SelectedOptionsView;

        private contentSummaryLoader: api.content.ContentSummaryLoader;

        private contentRequestsAllowed: boolean;

        private uploadDialog: UploadDialog;

        private editContentRequestListeners: {(content: ContentSummary): void }[] = [];

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        private layoutInProgress: boolean;

        private valueAddedListeners: {(event: api.form.inputtype.ValueAddedEvent) : void}[] = [];

        private valueChangedListeners: {(event: api.form.inputtype.ValueChangedEvent) : void}[] = [];

        private valueRemovedListeners: {(event: api.form.inputtype.ValueRemovedEvent) : void}[] = [];

        constructor(config: api.form.inputtype.InputTypeViewConfig<ImageSelectorConfig>) {
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

            this.uploadDialog = new UploadDialog();
            this.uploadDialog.onImageUploaded((event: api.ui.ImageUploadedEvent) => {
                this.createImageContent(event.getUploadedItem());
            });

            ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.ResponsiveItem) => {
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
            })

            this.uploadButton = new api.ui.Button();
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
                        this.comboBox.selectOption(<api.ui.selector.Option<ContentSummary>>{
                            value: content.getId(),
                            displayValue: content
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

        getAttachments(): api.content.attachment.Attachment[] {
            return [];
        }

        getElement(): api.dom.Element {
            return this;
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

        onValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
            this.valueAddedListeners.push(listener);
        }

        unValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
            this.valueAddedListeners.filter((currentListener: (event: api.form.inputtype.ValueAddedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValueAdded(value: api.data.Value) {
            var event = new api.form.inputtype.ValueAddedEvent(value);
            this.valueAddedListeners.forEach((listener: (event: api.form.inputtype.ValueAddedEvent)=>void) => {
                listener(event);
            });
        }

        onValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            this.valueChangedListeners.filter((currentListener: (event: api.form.inputtype.ValueChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValueChanged(event: api.form.inputtype.ValueChangedEvent) {
            this.valueChangedListeners.forEach((listener: (event: api.form.inputtype.ValueChangedEvent)=>void) => {
                listener(event);
            });
        }

        onValueRemoved(listener: (event: api.form.inputtype.ValueRemovedEvent) => void) {
            this.valueRemovedListeners.push(listener);
        }

        unValueRemoved(listener: (event: api.form.inputtype.ValueRemovedEvent) => void) {
            this.valueRemovedListeners.filter((currentListener: (event: api.form.inputtype.ValueRemovedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValueRemoved(index: number) {
            var event = new api.form.inputtype.ValueRemovedEvent(index);
            this.valueRemovedListeners.forEach((listener: (event: api.form.inputtype.ValueRemovedEvent)=>void) => {
                listener(event);
            });
        }

        onValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.filter((currentListener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValidityChanged(event: api.form.inputtype.InputValidityChangedEvent) {
            this.inputValidityChangedListeners.forEach((listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                listener(event);
            });
        }

        giveFocus(): boolean {
            if (this.comboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.comboBox.giveFocus();
        }

        createAndAddOccurrence() {
            throw new Error("ImageSelector manages occurrences self");
        }

        isManagingAdd(): boolean {
            return true;
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

        private createComboBox(input: api.form.Input): ComboBox<ContentSummary> {

            this.selectedOptionsView = new SelectedOptionsView();
            this.selectedOptionsView.onEditSelectedOptions((options: api.ui.selector.combobox.SelectedOption<ContentSummary>[]) => {
                options.forEach((option: api.ui.selector.combobox.SelectedOption<ContentSummary>) => {
                    this.notifyEditContentRequested(option.getOption().displayValue);
                });
            });

            this.selectedOptionsView.onRemoveSelectedOptions((options: api.ui.selector.combobox.SelectedOption<ContentSummary>[]) => {
                options.forEach((option: api.ui.selector.combobox.SelectedOption<ContentSummary>) => {
                    this.comboBox.removeSelectedOption(option.getOption());
                });
                this.validate(false);
            });

            this.selectedOptionsView.onValueChanged((event: api.form.inputtype.ValueChangedEvent) => {
                this.notifyValueChanged(event);
            });

            var comboBoxConfig = <ComboBoxConfig<ContentSummary>> {
                hideComboBoxWhenMaxReached: true,
                optionDisplayValueViewer: new ContentSummaryViewer(),
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum(),
                delayedInputValueChangedHandling: 500
            };

            var comboBox = new ComboBox<ContentSummary>(input.getName(), comboBoxConfig);

            this.loadOptions("");

            comboBox.onSelectedOptionRemoved((removed: api.ui.selector.combobox.SelectedOption<ContentSummary>) => {
                this.notifyValueRemoved(removed.getIndex());
                this.validate(false);
            });

            comboBox.onOptionFilterInputValueChanged((event: api.ui.selector.OptionFilterInputValueChangedEvent<ContentSummary>) => {
                this.loadOptions(event.getNewValue());
            });

            comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<ContentSummary>) => {
                if (!this.layoutInProgress) {
                    var value = new api.data.Value(event.getOption().displayValue.getContentId(), api.data.ValueTypes.CONTENT_ID);
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

        private createOptions(contents: ContentSummary[]): api.ui.selector.Option<ContentSummary>[] {
            return contents.map((content: ContentSummary) => {
                return {
                    value: content.getId(),
                    displayValue: content
                };
            });
        }

        private createImageContent(uploadItem: api.ui.UploadItem) {

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

                    this.comboBox.selectOption({
                        value: createdContent.getId(),
                        displayValue: createdContent
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
    }

    api.form.inputtype.InputTypeManager.register("ImageSelector", ImageSelector);

}
