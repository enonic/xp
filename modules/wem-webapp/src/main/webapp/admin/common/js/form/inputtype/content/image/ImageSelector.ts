module api.form.inputtype.content.image {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export interface ImageSelectorConfig {
        relationshipType: string
    }

    export class ImageSelector extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private config: api.form.inputtype.InputTypeViewConfig<ImageSelectorConfig>;

        private input: api.form.Input;

        private comboBox: api.ui.selector.combobox.ComboBox<api.content.ContentSummary>;

        private uploadButton: api.ui.Button;

        private selectedOptionsView: SelectedOptionsView;

        private contentSummaryLoader: api.form.inputtype.content.ContentSummaryLoader;

        private contentRequestsAllowed: boolean;

        private uploadDialog: UploadDialog;

        private editContentRequestListeners: {(content: api.content.ContentSummary): void }[] = [];

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        constructor(config: api.form.inputtype.InputTypeViewConfig<ImageSelectorConfig>) {
            super("image-selector");
            this.addClass("input-type-view");

            this.config = config;
            this.contentSummaryLoader = new api.form.inputtype.content.ContentSummaryLoader();
            this.contentSummaryLoader.onLoadingData((event: LoadingDataEvent) => {
                this.comboBox.setEmptyDropdownText("Searching...");
            });
            this.contentSummaryLoader.onLoadedData((event: LoadedDataEvent<api.content.ContentSummary>) => {
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
                this.createEmbeddedImageContent(event.getUploadedItem());
            });

            // Don't forget to clean up the modal dialog on remove
            this.onRemoved((event) => {
                this.uploadDialog.remove();
            })
        }

        availableSizeChanged() {
            console.log("ImageSelector.availableSizeChanged("+this.getEl().getWidth()+"x"+this.getEl().getWidth()+")" );
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.input = input;

            this.comboBox = this.createComboBox(input);

            if (properties != null) {
                properties.forEach((property: api.data.Property) => {
                    new api.content.GetContentByIdRequest(new api.content.ContentId(property.getString()))
                        .setExpand(api.content.ContentResourceRequest.EXPAND_SUMMARY)
                        .send()
                        .done((jsonResponse: api.rest.JsonResponse<api.content.json.ContentSummaryJson>) => {
                            var contentSummary = new api.content.ContentSummary(jsonResponse.getResult());
                            this.comboBox.selectOption(<api.ui.selector.Option<api.content.ContentSummary>>{
                                value: contentSummary.getId(),
                                displayValue: contentSummary
                            });
                        });
                });
            }

            this.uploadButton = new api.ui.Button("");
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



            var comboboxWrapper = new api.dom.DivEl("combobox-wrapper");
            comboboxWrapper.appendChild(this.comboBox);
            this.appendChild(comboboxWrapper);

            this.appendChild(this.uploadButton);

            this.appendChild(this.selectedOptionsView);
        }

        getValues(): api.data.Value[] {
            var values: api.data.Value[] = [];
            this.comboBox.getSelectedOptions().forEach((option: api.ui.selector.Option<api.content.ContentSummary>) => {

                var value = new api.data.Value(option.value, api.data.ValueTypes.CONTENT_ID);

                values.push(value);
            });
            return values;
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

        addEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            this.editContentRequestListeners.push(listener);
        }

        removeEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            this.editContentRequestListeners = this.editContentRequestListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyEditContentRequestListeners(content: api.content.ContentSummary) {
            this.editContentRequestListeners.forEach((listener) => {
                listener(content);
            })
        }

        private createComboBox(input: api.form.Input): api.ui.selector.combobox.ComboBox<api.content.ContentSummary> {

            this.selectedOptionsView = new SelectedOptionsView();
            this.selectedOptionsView.addEditSelectedOptionListener((option: api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => {
                this.notifyEditContentRequestListeners(option.getOption().displayValue)
            });

            this.selectedOptionsView.addRemoveSelectedOptionListener((option) => {
                this.comboBox.removeSelectedOption(option.getOption());
                this.validate(false);
            });

            var comboBoxConfig = <api.ui.selector.combobox.ComboBoxConfig<api.content.ContentSummary>> {
                rowHeight: 50,
                optionDisplayValueViewer: new api.content.ContentSummaryViewer(),
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum()
            };

            var comboBox = new api.ui.selector.combobox.ComboBox<api.content.ContentSummary>(input.getName(), comboBoxConfig);

            this.loadOptions("");

            comboBox.addSelectedOptionRemovedListener((removed: api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>) => {
                if (!comboBox.maximumOccurrencesReached()) {
                    this.uploadButton.setEnabled(true);
                }

                this.validate(false);
            });

            comboBox.onValueChanged((event: api.ui.selector.combobox.ComboBoxValueChangedEvent<api.content.ContentSummary>) => {
                this.loadOptions(event.getNewValue());
            });
            comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.ContentSummary>) => {
                if (this.comboBox.maximumOccurrencesReached()) {
                    this.uploadButton.setEnabled(false);
                }

                this.validate(false);
            });

            return comboBox;
        }

        private loadOptions(searchString: string): Q.Promise<api.rest.Response> {
            if (!this.contentRequestsAllowed || !this.comboBox) {
                return;
            }

            this.contentSummaryLoader.search(searchString);
        }

        private createOptions(contents: api.content.ContentSummary[]): api.ui.selector.Option<api.content.ContentSummary>[] {
            var options: api.ui.selector.Option<api.content.ContentSummary>[] = [];
            contents.forEach((content: api.content.ContentSummary) => {
                options.push({
                    value: content.getId(),
                    displayValue: content
                });
            });
            return options;
        }

        private createEmbeddedImageContent(uploadItem: api.ui.UploadItem) {

            var attachmentName = new api.content.attachment.AttachmentName(uploadItem.getName());
            var attachment = new api.content.attachment.AttachmentBuilder().
                setBlobKey(uploadItem.getBlobKey()).
                setAttachmentName(attachmentName).
                setMimeType(uploadItem.getMimeType()).
                setSize(uploadItem.getSize()).
                build();
            var mimeType = uploadItem.getMimeType();

            new api.schema.content.GetContentTypeByNameRequest(new api.schema.content.ContentTypeName("image")).
                sendAndParse().
                done((contentType: api.schema.content.ContentType) => {

                    var contentData = new api.content.image.ImageContentDataFactory().
                        setImage(attachmentName).
                        setMimeType(mimeType).create();

                    var createContentRequest = new api.content.CreateContentRequest().
                        setDraft(false).
                        setParent(this.config.contentPath).
                        setEmbed(true).
                        setName(api.content.ContentName.fromString(api.content.ContentName.ensureValidContentName(attachmentName.toString()))).
                        setContentType(contentType.getContentTypeName()).
                        setDisplayName(attachmentName.toString()).
                        setForm(contentType.getForm()).
                        setContentData(contentData).
                        addAttachment(attachment);
                    createContentRequest.
                        sendAndParse().
                        done((createdContent: api.content.Content) => {

                            this.comboBox.selectOption({
                                value: createdContent.getId(),
                                displayValue: createdContent
                            });
                        });
                });
        }
    }

    api.form.inputtype.InputTypeManager.register("ImageSelector", ImageSelector);

}
