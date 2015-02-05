module api.content.form.inputtype.image {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import ContentId = api.content.ContentId;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryLoader = api.content.ContentSummaryLoader;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ComboBoxConfig = api.ui.selector.combobox.ComboBoxConfig;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Option = api.ui.selector.Option;
    import RelationshipTypeName = api.schema.relationshiptype.RelationshipTypeName;

    import UploadItem = api.ui.uploader.UploadItem;
    import FileUploadedEvent = api.ui.uploader.FileUploadedEvent;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import FileUploadProgressEvent = api.ui.uploader.FileUploadProgressEvent;
    import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
    import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;

    export interface ImageSelectorConfig {
        relationshipType: string
    }

    export class ImageSelector extends api.form.inputtype.support.BaseInputTypeManagingAdd<ContentId> {

        private config: api.content.form.inputtype.ContentInputTypeViewContext<ImageSelectorConfig>;

        private input: api.form.Input;

        private propertyArray: PropertyArray;

        private relationshipTypeName: RelationshipTypeName;

        private comboBox: ComboBox<ImageSelectorDisplayValue>;

        private uploadButton: api.dom.ButtonEl;

        private selectedOptionsView: ImageSelectorSelectedOptionsView;

        private contentSummaryLoader: ContentSummaryLoader;

        private contentRequestsAllowed: boolean;

        private uploadDialog: ImageUploadDialog;

        private editContentRequestListeners: {(content: ContentSummary): void }[] = [];

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        private layoutInProgress: boolean;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext<ImageSelectorConfig>) {
            super("image-selector");
            this.addClass("input-type-view");

            this.config = config;

            // requests aren't allowed until allowed contentTypes are specified
            this.contentRequestsAllowed = false;

            this.relationshipTypeName = new RelationshipTypeName("default");

            if (config.inputConfig.relationshipType != null) {
                this.relationshipTypeName = new RelationshipTypeName(config.inputConfig.relationshipType);
            }

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

        getValueType(): ValueType {
            return ValueTypes.REFERENCE;
        }

        newInitialValue(): Value {
            return null;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {

            this.layoutInProgress = true;
            this.input = input;
            this.propertyArray = propertyArray;

            var comboboxWrapper = new api.dom.DivEl("combobox-wrapper");

            this.comboBox = this.createComboBox(input);
            comboboxWrapper.appendChild(this.comboBox);
            this.comboBox.onHidden((event: api.dom.ElementHiddenEvent) => {
                // hidden on max occurrences reached
                this.uploadButton.hide();
            });
            this.comboBox.onShown((event: api.dom.ElementShownEvent) => {
                // shown on occurrences between min and max
                this.uploadButton.show();
            });

            this.uploadButton = new api.dom.ButtonEl();
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


            this.contentSummaryLoader = new ContentSummaryLoader();
            this.contentSummaryLoader.onLoadingData((event: LoadingDataEvent) => {
                this.comboBox.setEmptyDropdownText("Searching...");
            });
            this.contentSummaryLoader.onLoadedData((event: LoadedDataEvent<ContentSummary>) => {
                var options = this.createOptions(event.getData());

                this.comboBox.setOptions(options);
            });


            return new api.schema.relationshiptype.GetRelationshipTypeByNameRequest(this.relationshipTypeName).sendAndParse()
                .then((relationshipType: api.schema.relationshiptype.RelationshipType) => {

                    this.comboBox.setInputIconUrl(relationshipType.getIconUrl());
                    this.contentSummaryLoader.setAllowedContentTypes(relationshipType.getAllowedToTypes());
                    this.contentRequestsAllowed = true;
                    this.loadOptions("");

                    if (this.config.contentId) {
                        this.layoutUploadDialog();
                    }

                    var loadContentPromise = this.doLoadContent(this.propertyArray);
                    return loadContentPromise.then((contents: ContentSummary[]) => {
                        contents.forEach((content: ContentSummary) => {
                            this.comboBox.selectOption(<Option<ImageSelectorDisplayValue>>{
                                value: content.getId(),
                                displayValue: ImageSelectorDisplayValue.fromContentSummary(content)
                            });
                        });

                        this.layoutInProgress = false;
                    });
                });
        }

        private layoutUploadDialog() {
            this.uploadDialog = new ImageUploadDialog(this.config.contentId);

            this.uploadDialog.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                this.uploadDialog.close();

                event.getUploadItems().forEach((uploadItem: UploadItem<Content>) => {
                    var value = ImageSelectorDisplayValue.fromUploadItem(uploadItem);

                    var option = <api.ui.selector.Option<ImageSelectorDisplayValue>>{
                        value: value.getId(),
                        displayValue: value
                    };
                    this.comboBox.selectOption(option);
                });
            });

            this.uploadDialog.onUploadProgress((event: FileUploadProgressEvent<Content>) => {
                var item = event.getUploadItem();

                var selectedOption = this.selectedOptionsView.getById(item.getId());
                (<ImageSelectorSelectedOptionView> selectedOption.getOptionView()).setProgress(item.getProgress());
            });

            this.uploadDialog.onImageUploaded((event: FileUploadedEvent<Content>) => {
                var item = event.getUploadItem();
                var createdContent = item.getModel();

                new api.content.ContentUpdatedEvent(this.config.contentId).fire();

                var selectedOption = this.selectedOptionsView.getById(item.getId());
                var option = selectedOption.getOption();
                option.displayValue.setContentSummary(createdContent);

                selectedOption.getOptionView().setOption(option);

                this.setContentIdProperty(createdContent.getContentId());
                this.validate(false);
            });

            this.uploadDialog.onUploadFailed((event: FileUploadFailedEvent<Content>) => {
                var item = event.getUploadItem();

                var selectedOption = this.selectedOptionsView.getById(item.getId());
                (<ImageSelectorSelectedOptionView> selectedOption.getOptionView()).showError("Upload failed");
            })
        }

        private doLoadContent(propertyArray: PropertyArray): wemQ.Promise<ContentSummary[]> {

            var contentIds: ContentId[] = [];
            propertyArray.forEach((property: Property) => {
                if (property.hasNonNullValue()) {
                    contentIds.push(ContentId.fromReference(property.getReference()));
                }
            });
            return new api.content.GetContentSummaryByIds(contentIds).get();
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

            this.selectedOptionsView = new ImageSelectorSelectedOptionsView();
            this.selectedOptionsView.onEditSelectedOptions((options: SelectedOption<ImageSelectorDisplayValue>[]) => {
                options.forEach((option: SelectedOption<ImageSelectorDisplayValue>) => {
                    this.notifyEditContentRequested(option.getOption().displayValue.getContentSummary());
                });
            });

            this.selectedOptionsView.onRemoveSelectedOptions((options: SelectedOption<ImageSelectorDisplayValue>[]) => {
                options.forEach((option: SelectedOption<ImageSelectorDisplayValue>) => {
                    this.comboBox.deselectOption(option.getOption());
                });
                this.validate(false);
            });

            this.selectedOptionsView.onValueChanged((event: api.form.inputtype.ValueChangedEvent) => {
                this.propertyArray.set(event.getArrayIndex(), event.getNewValue());
                this.validate(false);
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

            comboBox.onOptionDeselected((removed: SelectedOption<ImageSelectorDisplayValue>) => {
                this.propertyArray.remove(removed.getIndex());
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

                    this.setContentIdProperty(contentId);
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

        private setContentIdProperty(contentId: api.content.ContentId) {
            var reference = api.util.Reference.from(contentId);

            var value = new Value(reference, ValueTypes.REFERENCE);

            if (this.comboBox.countSelectedOptions() == 1) { // overwrite initial value
                this.propertyArray.set(0, value);
            }
            else {
                this.propertyArray.add(value);
            }
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
