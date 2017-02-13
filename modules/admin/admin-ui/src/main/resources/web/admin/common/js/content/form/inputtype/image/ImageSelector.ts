module api.content.form.inputtype.image {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import ContentId = api.content.ContentId;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryLoader = api.content.resource.ContentSummaryLoader;
    import ContentComboBox = api.content.form.inputtype.image.ImageContentComboBox;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Option = api.ui.selector.Option;
    import RelationshipTypeName = api.schema.relationshiptype.RelationshipTypeName;

    import ContentDeletedEvent = api.content.event.ContentDeletedEvent;
    import UploadItem = api.ui.uploader.UploadItem;
    import FileUploadedEvent = api.ui.uploader.FileUploadedEvent;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import FileUploadProgressEvent = api.ui.uploader.FileUploadProgressEvent;
    import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
    import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;

    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;

    import FocusSwitchEvent = api.ui.FocusSwitchEvent;

    export class ImageSelector extends api.form.inputtype.support.BaseInputTypeManagingAdd<ContentId> {

        private config: api.content.form.inputtype.ContentInputTypeViewContext;

        private relationshipTypeName: RelationshipTypeName;

        private contentComboBox: ImageContentComboBox;

        private selectedOptionsView: ImageSelectorSelectedOptionsView;

        private contentRequestsAllowed: boolean;

        private uploader: api.content.image.ImageUploaderEl;

        private editContentRequestListeners: {(content: ContentSummary): void }[] = [];

        private relationshipType: string;

        private allowedContentTypes: string[];

        private allowedContentPaths: string[];

        private contentDeletedListener: (event: ContentDeletedEvent) => void;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super('image-selector');
            this.addClass('input-type-view');

            this.config = config;

            // requests aren't allowed until allowed contentTypes are specified
            this.contentRequestsAllowed = false;

            this.readConfig(config.inputConfig);

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                this.availableSizeChanged();
            });

            // Don't forget to clean up the modal dialog on remove
            this.onRemoved((event) => {
                ResponsiveManager.unAvailableSizeChanged(this);
            });

            this.onShown(() => {
                this.updateSelectedItemsIcons();
            });

            this.handleContentDeletedEvent();
        }

        private handleContentDeletedEvent() {
            this.contentDeletedListener = (event) => {
                if (this.selectedOptionsView.count() === 0) {
                    return;
                }

                let selectedContentIdsMap: {} = {};
                this.selectedOptionsView.getSelectedOptions().forEach(
                    (selectedOption: any) => {
                        if (!!selectedOption.getOption().displayValue && !!selectedOption.getOption().displayValue.getContentId()) {
                            selectedContentIdsMap[selectedOption.getOption().displayValue.getContentId().toString()] = '';
                        }
                    });

                event.getDeletedItems().filter(deletedItem => !deletedItem.isPending() &&
                                                              selectedContentIdsMap.hasOwnProperty(
                                                                  deletedItem.getContentId().toString())).forEach((deletedItem) => {
                        let option = this.selectedOptionsView.getById(deletedItem.getContentId().toString());
                        if (option != null) {
                            this.selectedOptionsView.removeSelectedOptions([option]);
                        }
                    });
            };

            ContentDeletedEvent.on(this.contentDeletedListener);

            this.onRemoved((event) => {
                ContentDeletedEvent.un(this.contentDeletedListener);
            });
        }

        public getContentComboBox(): ImageContentComboBox {
            return this.contentComboBox;
        }

        private readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): void {
            let relationshipTypeConfig = inputConfig['relationshipType'] ? inputConfig['relationshipType'][0] : {};
            this.relationshipType = relationshipTypeConfig['value'];

            if (this.relationshipType) {
                this.relationshipTypeName = new RelationshipTypeName(this.relationshipType);
            } else {
                this.relationshipTypeName = RelationshipTypeName.REFERENCE;
            }

            let allowContentTypeConfig = inputConfig['allowContentType'] || [];
            this.allowedContentTypes = allowContentTypeConfig.map((cfg) => cfg['value']).filter((val) => !!val);

            let allowContentPathConfig = inputConfig['allowPath'] || [];
            this.allowedContentPaths = allowContentPathConfig.map((cfg) => cfg['value']).filter((val) => !!val);
        }

        private updateSelectedItemsIcons() {
            if (this.contentComboBox.getSelectedOptions().length > 0) {
                this.doLoadContent(this.getPropertyArray()).then((contents: ContentSummary[]) => {
                    contents.forEach((content: ContentSummary) => {
                        this.selectedOptionsView.updateUploadedOption(<Option<ImageSelectorDisplayValue>>{
                            value: content.getId(),
                            displayValue: ImageSelectorDisplayValue.fromContentSummary(content)
                        });
                    });

                    this.setLayoutInProgress(false);
                });
            }
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
            let inputMaximum = this.getInput().getOccurrences().getMaximum();
            let countSelected = this.countSelectedOptions();
            let rest = -1;
            if (inputMaximum === 0) {
                rest = 0;
            } else {
                rest = inputMaximum - countSelected;
                rest = (rest === 0) ? -1 : rest;
            }

            return rest;
        }

        private createSelectedOptionsView(): ImageSelectorSelectedOptionsView {
            let selectedOptionsView = new ImageSelectorSelectedOptionsView();

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

            return selectedOptionsView;
        }

        createContentComboBox(maximumOccurrences: number, inputIconUrl: string, relationshipAllowedContentTypes: string[],
                              inputName: string): ContentComboBox {

            let value = this.getPropertyArray().getProperties().map((property) => {
                return property.getString();
            }).join(';');

            let contentTypes = this.allowedContentTypes.length ? this.allowedContentTypes :
                               relationshipAllowedContentTypes.length ? relationshipAllowedContentTypes :
                                   [ContentTypeName.IMAGE.toString(), ContentTypeName.MEDIA_VECTOR.toString()];

            let imageSelectorLoader = ImageSelectorLoader.create().setContent(this.config.content).
                setInputName(inputName).
                setAllowedContentPaths(this.allowedContentPaths).
                setContentTypeNames(contentTypes).
                setRelationshipType(this.relationshipType).
                build();

            let contentComboBox: ImageContentComboBox
                    = ImageContentComboBox.create().setMaximumOccurrences(maximumOccurrences).setLoader(imageSelectorLoader).
                    setSelectedOptionsView(this.selectedOptionsView = this.createSelectedOptionsView()).
                    setValue(value).
                    build();
            let comboBox: ComboBox<ImageSelectorDisplayValue> = contentComboBox.getComboBox();

            comboBox.onHidden((event: api.dom.ElementHiddenEvent) => {
                // hidden on max occurrences reached
                if (this.uploader) {
                    this.uploader.hide();
                }
            });
            comboBox.onShown((event: api.dom.ElementShownEvent) => {
                // shown on occurrences between min and max
                if (this.uploader) {
                    this.uploader.show();
                }
            });
            comboBox.setInputIconUrl(inputIconUrl);

            comboBox.onOptionDeselected((event: SelectedOptionEvent<ImageSelectorDisplayValue>) => {
                // property not found.
                const option = event.getSelectedOption();
                if (option.getOption().displayValue.getContentSummary()) {
                    this.getPropertyArray().remove(option.getIndex());
                }
                this.validate(false);
            });

            comboBox.onContentMissing((ids: string[]) => {
                ids.forEach(id => this.removePropertyWithId(id));
                this.validate(false);
            });

            comboBox.onOptionSelected((event: SelectedOptionEvent<ImageSelectorDisplayValue>) => {
                this.fireFocusSwitchEvent(event);

                if (!this.isLayoutInProgress()) {
                    let contentId = event.getSelectedOption().getOption().displayValue.getContentId();
                    if (!contentId) {
                        return;
                    }

                    this.setContentIdProperty(contentId);
                }
                this.validate(false);
            });

            comboBox.onOptionMoved((moved: SelectedOption<ImageSelectorDisplayValue>) => {

                this.getPropertyArray().set(moved.getIndex(), ValueTypes.REFERENCE.newValue(moved.getOption().value));
                this.validate(false);
            });

            return contentComboBox;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {
            if (!ValueTypes.REFERENCE.equals(propertyArray.getType())) {
                propertyArray.convertValues(ValueTypes.REFERENCE);
            }
            return super.layout(input, propertyArray).then(() => {
                return new api.schema.relationshiptype.GetRelationshipTypeByNameRequest(this.relationshipTypeName).sendAndParse()
                    .then((relationshipType: api.schema.relationshiptype.RelationshipType) => {

                        this.contentComboBox = this.createContentComboBox(
                            input.getOccurrences().getMaximum(), relationshipType.getIconUrl(), relationshipType.getAllowedToTypes() || [],
                            input.getName()
                        );

                        let comboBoxWrapper = new api.dom.DivEl('combobox-wrapper');

                        comboBoxWrapper.appendChild(this.contentComboBox);

                        this.contentRequestsAllowed = true;

                        if (this.config.content) {
                            comboBoxWrapper.appendChild(this.createUploader());
                        }

                        this.appendChild(comboBoxWrapper);
                        this.appendChild(this.selectedOptionsView);

                        this.setLayoutInProgress(false);
                    });
            });
        }

        private removePropertyWithId(id: string) {
            let length = this.getPropertyArray().getSize();
            for (let i = 0; i < length; i++) {
                if (this.getPropertyArray().get(i).getValue().getString() === id) {
                    this.getPropertyArray().remove(i);
                    api.notify.NotifyManager.get().showWarning('Failed to load image with id ' + id +
                                                               '. The reference will be removed upon save.');
                    break;
                }
            }
        }

        update(propertyArray: PropertyArray, unchangedOnly?: boolean): wemQ.Promise<void> {
            return super.update(propertyArray, unchangedOnly).then(() => {
                if ((!unchangedOnly || !this.contentComboBox.isDirty()) && this.contentComboBox.isRendered()) {
                    this.contentComboBox.setValue(this.getValueFromPropertyArray(propertyArray));
                }
            });
        }

        reset() {
            this.contentComboBox.resetBaseValues();
        }

        private createUploader(): api.content.image.ImageUploaderEl {
            let multiSelection = (this.getInput().getOccurrences().getMaximum() !== 1);

            this.uploader = new api.content.image.ImageUploaderEl({
                params: {
                    parent: this.config.content.getContentId().toString()
                },
                operation: api.ui.uploader.MediaUploaderElOperation.create,
                name: 'image-selector-upload-dialog',
                showCancel: false,
                showResult: false,
                maximumOccurrences: this.getRemainingOccurrences(),
                allowMultiSelection: multiSelection,
                deferred: true
            });

            this.uploader.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                event.getUploadItems().forEach((uploadItem: UploadItem<Content>) => {
                    let value = ImageSelectorDisplayValue.fromUploadItem(uploadItem);

                    let option = <api.ui.selector.Option<ImageSelectorDisplayValue>>{
                        value: value.getId(),
                        displayValue: value
                    };
                    this.contentComboBox.selectOption(option);
                });
            });

            this.uploader.onUploadProgress((event: FileUploadProgressEvent<Content>) => {
                let item = event.getUploadItem();

                let selectedOption = this.selectedOptionsView.getById(item.getId());
                if (!!selectedOption) {
                    (<ImageSelectorSelectedOptionView> selectedOption.getOptionView()).setProgress(item.getProgress());
                }

                this.uploader.setMaximumOccurrences(this.getRemainingOccurrences());
            });

            this.uploader.onFileUploaded((event: FileUploadedEvent<Content>) => {
                let item = event.getUploadItem();
                let createdContent = item.getModel();

                //new api.content.ContentUpdatedEvent(this.config.contentId).fire();

                let selectedOption = this.selectedOptionsView.getById(item.getId());
                let option = selectedOption.getOption();
                option.displayValue.setContentSummary(createdContent);
                option.value = createdContent.getContentId().toString();

                selectedOption.getOptionView().setOption(option);

                // checks newly uploaded image in Selected Options view
                let optionView: ImageSelectorSelectedOptionView = <ImageSelectorSelectedOptionView>selectedOption.getOptionView();
                optionView.getCheckbox().setChecked(true);

                this.setContentIdProperty(createdContent.getContentId());
                this.validate(false);

                this.uploader.setMaximumOccurrences(this.getRemainingOccurrences());
            });

            this.uploader.onUploadFailed((event: FileUploadFailedEvent<Content>) => {
                let item = event.getUploadItem();

                let selectedOption = this.selectedOptionsView.getById(item.getId());
                if (!!selectedOption) {
                    this.selectedOptionsView.removeSelectedOptions([selectedOption]);
                }

                this.uploader.setMaximumOccurrences(this.getRemainingOccurrences());
            });

            this.uploader.onClicked(() => {
                this.uploader.setMaximumOccurrences(this.getRemainingOccurrences());
            });

            //Drag N' Drop
            // in order to toggle appropriate class during drag event
            // we catch drag enter on this element and trigger uploader to appear,
            // then catch drag leave on uploader's dropzone to get back to previous state
            this.onDragEnter((event: DragEvent) => {
                event.stopPropagation();
                this.uploader.giveFocus();
                this.uploader.setDefaultDropzoneVisible(true, true);
            });

            this.uploader.onDropzoneDragLeave((event: DragEvent) => {
                this.uploader.giveBlur();
                this.uploader.setDefaultDropzoneVisible(false);
            });

            this.uploader.onDropzoneDrop((event) => {
                this.uploader.setMaximumOccurrences(this.getRemainingOccurrences());
                this.uploader.setDefaultDropzoneVisible(false);
            });

            return this.uploader;
        }

        private doLoadContent(propertyArray: PropertyArray): wemQ.Promise<ContentSummary[]> {

            let contentIds: ContentId[] = [];
            propertyArray.forEach((property: Property) => {
                if (property.hasNonNullValue()) {
                    contentIds.push(ContentId.fromReference(property.getReference()));
                }
            });
            return new api.content.resource.GetContentSummaryByIds(contentIds).sendAndParse();
        }

        protected getNumberOfValids(): number {
            return this.getPropertyArray().getSize();
        }

        giveFocus(): boolean {
            if (this.contentComboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.contentComboBox.giveFocus();
        }

        private setContentIdProperty(contentId: api.content.ContentId) {
            let reference = api.util.Reference.from(contentId);

            let value = new Value(reference, ValueTypes.REFERENCE);

            if (!this.getPropertyArray().containsValue(value)) {
                this.ignorePropertyChange = true;
                if (this.contentComboBox.countSelected() === 1) { // overwrite initial value
                    this.getPropertyArray().set(0, value);
                } else {
                    this.getPropertyArray().add(value);
                }
                this.ignorePropertyChange = false;
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
            this.editContentRequestListeners = this.editContentRequestListeners
                .filter(function (curr: (content: ContentSummary) => void) {
                    return curr !== listener;
                });
        }

        private notifyEditContentRequested(content: ContentSummary) {
            this.editContentRequestListeners.forEach((listener) => {
                listener(content);
            });
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class('ImageSelector', ImageSelector));

}
