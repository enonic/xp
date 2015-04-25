module api.content.form.inputtype.image {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import ContentId = api.content.ContentId;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryLoader = api.content.ContentSummaryLoader;
    import ContentComboBox = api.content.form.inputtype.image.ImageContentComboBox;
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

        private contentComboBox: ImageContentComboBox;

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

            if (config.inputConfig.relationshipType) {
                this.relationshipTypeName = new RelationshipTypeName(config.inputConfig.relationshipType);
            } else {
                this.relationshipTypeName = RelationshipTypeName.REFERENCE;
            }

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                this.availableSizeChanged();
            });

            // Don't forget to clean up the modal dialog on remove
            this.onRemoved((event) => {
                this.uploadDialog.remove();
                ResponsiveManager.unAvailableSizeChanged(this);
            });

            this.onShown(() => {
                this.updateSelectedItemsIcons();
            });
        }

        private updateSelectedItemsIcons() {
            if (this.contentComboBox.getSelectedOptions().length > 0) {
                this.doLoadContent(this.propertyArray).then((contents: ContentSummary[]) => {
                    contents.forEach((content: ContentSummary) => {
                        this.selectedOptionsView.updateUploadedOption(<Option<ImageSelectorDisplayValue>>{
                            value: content.getId(),
                            displayValue: ImageSelectorDisplayValue.fromContentSummary(content)
                        });
                    });

                    this.layoutInProgress = false;
                });
            }
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

        private countSelectedOptions(): number {
            return this.selectedOptionsView.count();
        }

        private getRemainingOccurrences(): number {
            var inputMaximum = this.input.getOccurrences().getMaximum();
            var countSelected = this.countSelectedOptions();
            var rest = -1;
            if (inputMaximum == 0) {
                rest = 0;
            } else {
                rest = inputMaximum - countSelected;
                rest = (rest == 0) ? -1 : rest;
            }

            return rest;
        }

        private onUploadButtonClicked() {
            if (!this.uploadDialog) {
                this.uploadDialog = this.createUploadDialog();
            }

            this.uploadDialog.setMaximumOccurrences(this.getRemainingOccurrences());
            this.uploadDialog.open();
        }

        private createUploadButton(): api.dom.ButtonEl {
            var uploadButton = new api.dom.ButtonEl();
            uploadButton.addClass("upload-button");
            uploadButton.onClicked((event: MouseEvent) => this.onUploadButtonClicked());

            return uploadButton;
        }

        private createSelectedOptionsView(): ImageSelectorSelectedOptionsView {
            var selectedOptionsView = new ImageSelectorSelectedOptionsView();

            selectedOptionsView.onEditSelectedOptions((options: SelectedOption<ImageSelectorDisplayValue>[]) => {
                options.forEach((option: SelectedOption<ImageSelectorDisplayValue>) => {
                    this.notifyEditContentRequested(option.getOption().displayValue.getContentSummary());
                });
            });

            selectedOptionsView.onRemoveSelectedOptions((options: SelectedOption<ImageSelectorDisplayValue>[]) => {
                options.forEach((option: SelectedOption<ImageSelectorDisplayValue>) => {
                    this.contentComboBox.deselect(option.getOption().displayValue);
                });
                this.validate(false);
            });

            selectedOptionsView.onValueChanged((event: api.form.inputtype.ValueChangedEvent) => {
                this.propertyArray.set(event.getArrayIndex(), event.getNewValue());
                this.validate(false);
            });

            return selectedOptionsView;
        }

        createContentComboBox(maximumOccurrences: number, inputIconUrl: string, allowedContentTypes: string[]): ContentComboBox {
            var contentComboBox: ImageContentComboBox
                    = ImageContentComboBox.create().
                    setMaximumOccurrences(maximumOccurrences).
                    setAllowedContentTypes(allowedContentTypes.length ? allowedContentTypes : [ContentTypeName.IMAGE.toString()]).
                    setLoader(this.contentSummaryLoader = new ContentSummaryLoader()).
                    setSelectedOptionsView(this.selectedOptionsView = this.createSelectedOptionsView()).
                    build(),
                comboBox: ComboBox<ImageSelectorDisplayValue> = contentComboBox.getComboBox();

            comboBox.onHidden((event: api.dom.ElementHiddenEvent) => {
                // hidden on max occurrences reached
                this.uploadButton.hide();
            });
            comboBox.onShown((event: api.dom.ElementShownEvent) => {
                // shown on occurrences between min and max
                this.uploadButton.show();
            });
            comboBox.setInputIconUrl(inputIconUrl);

            comboBox.onOptionDeselected((removed: SelectedOption<ImageSelectorDisplayValue>) => {
                this.propertyArray.remove(removed.getIndex());
                this.validate(false);
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

            return contentComboBox;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {
            this.layoutInProgress = true;
            this.input = input;
            this.propertyArray = propertyArray;

            return new api.schema.relationshiptype.GetRelationshipTypeByNameRequest(this.relationshipTypeName).sendAndParse()
                .then((relationshipType: api.schema.relationshiptype.RelationshipType) => {
                    this.contentComboBox = this.createContentComboBox(
                        input.getOccurrences().getMaximum(), relationshipType.getIconUrl(), relationshipType.getAllowedToTypes() || []
                    );

                    var comboboxWrapper = new api.dom.DivEl("combobox-wrapper");

                    comboboxWrapper.appendChild(this.contentComboBox);
                    comboboxWrapper.appendChild(this.uploadButton = this.createUploadButton());

                    this.appendChild(comboboxWrapper);
                    this.appendChild(this.selectedOptionsView);

                    this.contentRequestsAllowed = true;

                    if (this.config.contentId) {
                        this.uploadDialog = this.createUploadDialog();
                    }

                    return this.doLoadContent(this.propertyArray).then((contents: ContentSummary[]) => {
                        contents.forEach((content: ContentSummary) => {
                            this.contentComboBox.selectOption(<Option<ImageSelectorDisplayValue>>{
                                value: content.getId(),
                                displayValue: ImageSelectorDisplayValue.fromContentSummary(content)
                            });
                        });

                        this.layoutInProgress = false;
                    });
                });
        }

        private createUploadDialog(): ImageUploadDialog {
            var multiSelection = (this.input.getOccurrences().getMaximum() != 1);

            var uploadDialog = new ImageUploadDialog(this.config.contentId, multiSelection);

            uploadDialog.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                uploadDialog.close();

                event.getUploadItems().forEach((uploadItem: UploadItem<Content>) => {
                    var value = ImageSelectorDisplayValue.fromUploadItem(uploadItem);

                    var option = <api.ui.selector.Option<ImageSelectorDisplayValue>>{
                        value: value.getId(),
                        displayValue: value
                    };
                    this.contentComboBox.selectOption(option);
                });
            });

            uploadDialog.onUploadProgress((event: FileUploadProgressEvent<Content>) => {
                var item = event.getUploadItem();

                var selectedOption = this.selectedOptionsView.getById(item.getId());
                (<ImageSelectorSelectedOptionView> selectedOption.getOptionView()).setProgress(item.getProgress());
            });

            uploadDialog.onImageUploaded((event: FileUploadedEvent<Content>) => {
                var item = event.getUploadItem();
                var createdContent = item.getModel();

                new api.content.ContentUpdatedEvent(this.config.contentId).fire();

                var selectedOption = this.selectedOptionsView.getById(item.getId());
                var option = selectedOption.getOption();
                option.displayValue.setContentSummary(createdContent);

                selectedOption.getOptionView().setOption(option);

                // checks newly uploaded image in Selected Options view
                var optionView: ImageSelectorSelectedOptionView = <ImageSelectorSelectedOptionView>selectedOption.getOptionView();
                optionView.getCheckbox().setChecked(true);

                this.setContentIdProperty(createdContent.getContentId());
                this.validate(false);
            });

            uploadDialog.onUploadFailed((event: FileUploadFailedEvent<Content>) => {
                var item = event.getUploadItem();

                var selectedOption = this.selectedOptionsView.getById(item.getId());
                (<ImageSelectorSelectedOptionView> selectedOption.getOptionView()).showError("Upload failed");
            })

            return uploadDialog;
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

            var numberOfValids = this.contentComboBox.countSelected();
            if (numberOfValids < this.input.getOccurrences().getMinimum() || this.input.getOccurrences().maximumBreached(numberOfValids)) {
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
            if (this.contentComboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.contentComboBox.giveFocus();
        }

        private setContentIdProperty(contentId: api.content.ContentId) {
            var reference = api.util.Reference.from(contentId);

            var value = new Value(reference, ValueTypes.REFERENCE);

            if (this.contentComboBox.countSelected() == 1) { // overwrite initial value
                this.propertyArray.set(0, value);
            }
            else {
                this.propertyArray.add(value);
            }
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.contentComboBox.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.contentComboBox.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.contentComboBox.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.contentComboBox.unBlur(listener);
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
