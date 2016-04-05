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

    import ContentSelectorLoader = api.content.form.inputtype.contentselector.ContentSelectorLoader;

    export class ImageSelector extends api.form.inputtype.support.BaseInputTypeManagingAdd<ContentId> {

        private config: api.content.form.inputtype.ContentInputTypeViewContext;

        private relationshipTypeName: RelationshipTypeName;

        private contentComboBox: ImageContentComboBox;

        private selectedOptionsView: ImageSelectorSelectedOptionsView;

        private contentRequestsAllowed: boolean;

        private uploader: ImageUploaderEl;

        private editContentRequestListeners: {(content: ContentSummary): void }[] = [];

        private relationshipType: string;

        private allowedContentTypes: string[];

        private allowedContentPaths: string[];

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super("image-selector");
            this.addClass("input-type-view");

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

            api.content.event.ContentDeletedEvent.on((event) => {
                event.getDeletedItems().filter((deletedItem) => {
                    return !!deletedItem;
                }).forEach((deletedItem) => {

                    var option = this.selectedOptionsView.getById(deletedItem.getContentId().toString());
                    if (option != null) {
                        this.selectedOptionsView.removeSelectedOptions([option]);
                    }
                });
            });

        }

        public getContentComboBox(): ImageContentComboBox {
            return this.contentComboBox;
        }

        private readConfig(inputConfig: { [element: string]: { [name: string]: string }[]; }): void {
            var relationshipTypeConfig = inputConfig['relationshipType'] ? inputConfig['relationshipType'][0] : {};
            this.relationshipType = relationshipTypeConfig['value'];

            if (this.relationshipType) {
                this.relationshipTypeName = new RelationshipTypeName(this.relationshipType);
            } else {
                this.relationshipTypeName = RelationshipTypeName.REFERENCE;
            }

            var allowContentTypeConfig = inputConfig['allowContentType'] || [];
            this.allowedContentTypes = allowContentTypeConfig.map((cfg) => cfg['value']).filter((val) => !!val);

            var allowContentPathConfig = inputConfig['allowPath'] || [];
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
            var inputMaximum = this.getInput().getOccurrences().getMaximum();
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

            return selectedOptionsView;
        }

        createContentComboBox(maximumOccurrences: number, inputIconUrl: string, relationshipAllowedContentTypes: string[],
                              inputName: string): ContentComboBox {

            var value = this.getPropertyArray().getProperties().map((property) => {
                return property.getString();
            }).join(';');

            var contentTypes = this.allowedContentTypes.length ? this.allowedContentTypes :
                               relationshipAllowedContentTypes.length ? relationshipAllowedContentTypes :
                                   [ContentTypeName.IMAGE.toString()];

            var contentSelectorLoader = ContentSelectorLoader.create().
                setId(this.config.contentId).
                setInputName(inputName).
                setAllowedContentPaths(this.allowedContentPaths).
                setContentTypeNames(contentTypes).
                setRelationshipType(this.relationshipType).
                build();

            var contentComboBox: ImageContentComboBox
                    = ImageContentComboBox.create().
                    setMaximumOccurrences(maximumOccurrences).
                    setLoader(contentSelectorLoader).
                    setSelectedOptionsView(this.selectedOptionsView = this.createSelectedOptionsView()).
                    setPostLoad(contentSelectorLoader.postLoad.bind(contentSelectorLoader)).
                    setValue(value).
                    build(),
                comboBox: ComboBox<ImageSelectorDisplayValue> = contentComboBox.getComboBox();

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

            comboBox.onOptionDeselected((removed: SelectedOption<ImageSelectorDisplayValue>) => {
                // property not found.
                if (!!removed.getOption().displayValue.getContentSummary()) {
                    this.getPropertyArray().remove(removed.getIndex());
                }
                this.validate(false);
            });

            comboBox.onOptionSelected((added: SelectedOption<ImageSelectorDisplayValue>) => {
                if (!this.isLayoutInProgress()) {
                    var contentId = added.getOption().displayValue.getContentId();
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

                        var comboBoxWrapper = new api.dom.DivEl("combobox-wrapper");

                        comboBoxWrapper.appendChild(this.contentComboBox);

                        this.contentRequestsAllowed = true;

                        if (this.config.contentId) {
                            comboBoxWrapper.appendChild(this.createUploader());
                        }

                        this.appendChild(comboBoxWrapper);
                        this.appendChild(this.selectedOptionsView);

                        this.setLayoutInProgress(false);
                    });
            });
        }

        update(propertyArray: PropertyArray, unchangedOnly?: boolean): wemQ.Promise<void> {
            var superPromise = super.update(propertyArray, unchangedOnly);

            if (!unchangedOnly || !this.contentComboBox.isDirty()) {
                return superPromise.then(() => {

                    this.contentComboBox.setValue(this.getValueFromPropertyArray(propertyArray));
                })
            } else {
                return superPromise;
            }
        }

        private createUploader(): ImageUploaderEl {
            var multiSelection = (this.getInput().getOccurrences().getMaximum() != 1);

            this.uploader = new api.content.ImageUploaderEl({
                params: {
                    parent: this.config.contentId.toString()
                },
                operation: api.content.MediaUploaderElOperation.create,
                name: 'image-selector-upload-dialog',
                showCancel: false,
                showReset: false,
                showResult: false,
                maximumOccurrences: this.getRemainingOccurrences(),
                allowMultiSelection: multiSelection,
                scaleWidth: false,
                deferred: true
            });

            this.uploader.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                event.getUploadItems().forEach((uploadItem: UploadItem<Content>) => {
                    var value = ImageSelectorDisplayValue.fromUploadItem(uploadItem);

                    var option = <api.ui.selector.Option<ImageSelectorDisplayValue>>{
                        value: value.getId(),
                        displayValue: value
                    };
                    this.contentComboBox.selectOption(option);
                });
            });

            this.uploader.onUploadProgress((event: FileUploadProgressEvent<Content>) => {
                var item = event.getUploadItem();

                var selectedOption = this.selectedOptionsView.getById(item.getId());
                if (!!selectedOption) {
                    (<ImageSelectorSelectedOptionView> selectedOption.getOptionView()).setProgress(item.getProgress());
                }

                this.uploader.setMaximumOccurrences(this.getRemainingOccurrences());
            });

            this.uploader.onFileUploaded((event: FileUploadedEvent<Content>) => {
                var item = event.getUploadItem();
                var createdContent = item.getModel();

                //new api.content.ContentUpdatedEvent(this.config.contentId).fire();

                var selectedOption = this.selectedOptionsView.getById(item.getId());
                var option = selectedOption.getOption();
                option.displayValue.setContentSummary(createdContent);
                option.value = createdContent.getContentId().toString();

                selectedOption.getOptionView().setOption(option);

                // checks newly uploaded image in Selected Options view
                var optionView: ImageSelectorSelectedOptionView = <ImageSelectorSelectedOptionView>selectedOption.getOptionView();
                optionView.getCheckbox().setChecked(true);

                this.setContentIdProperty(createdContent.getContentId());
                this.validate(false);

                this.uploader.setMaximumOccurrences(this.getRemainingOccurrences());
            });

            this.uploader.onUploadFailed((event: FileUploadFailedEvent<Content>) => {
                var item = event.getUploadItem();

                var selectedOption = this.selectedOptionsView.getById(item.getId());
                if (!!selectedOption) {
                    (<ImageSelectorSelectedOptionView> selectedOption.getOptionView()).showError("Upload failed");
                }

                this.uploader.setMaximumOccurrences(this.getRemainingOccurrences());
            });

            this.uploader.onClicked(() => {
                this.uploader.setMaximumOccurrences(this.getRemainingOccurrences());
            });

            /*
             * Drag N' Drop
             */
            var body = api.dom.Body.get();

            this.uploader.addClass("minimized");
            var dragOverEl;
            // make use of the fact that when dragging
            // first drag enter occurs on the child element and after that
            // drag leave occurs on the parent element that we came from
            // meaning that to know when we left some element
            // we need to compare it to the one currently dragged over
            this.onDragEnter((event: DragEvent) => {
                var target = <HTMLElement> event.target;
                this.uploader.giveFocus();
                this.uploader.toggleClass("minimized", false);
                dragOverEl = target;
            });

            body.onDragLeave((event: DragEvent) => {
                var targetEl = <HTMLElement> event.target;
                if (dragOverEl == targetEl) {
                    this.uploader.giveBlur();
                    this.uploader.toggleClass("minimized", true);
                    dragOverEl = null;
                }
            });

            body.onDrop((event: DragEvent) => {
                this.uploader.setMaximumOccurrences(this.getRemainingOccurrences());
                this.uploader.toggleClass("minimized", true);
            });

            return this.uploader;
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
            var reference = api.util.Reference.from(contentId);

            var value = new Value(reference, ValueTypes.REFERENCE);

            if (!this.getPropertyArray().containsValue(value)) {
                this.ignorePropertyChange = true;
                if (this.contentComboBox.countSelected() == 1) { // overwrite initial value
                    this.getPropertyArray().set(0, value);
                }
                else {
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