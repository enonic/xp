module api.form.inputtype.content.image {

    export interface ImageSelectorConfig {

        relationshipType: {
            name: string
        }
    }

    export class ImageSelector extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private config: api.form.inputtype.InputTypeViewConfig<ImageSelectorConfig>;

        private input: api.form.Input;

        private comboBox: api.ui.combobox.ComboBox<api.content.ContentSummary>;

        private libraryButton: api.ui.Button;

        private uploadButton: api.ui.Button;

        private selectedOptionsView: SelectedOptionsView;

        private contentSummaryLoader: api.form.inputtype.content.ContentSummaryLoader;

        private contentRequestsAllowed: boolean;

        private uploadDialog: UploadDialog;

        private editContentRequestListeners: {(content: api.content.ContentSummary): void}[] = [];

        constructor(config: api.form.inputtype.InputTypeViewConfig<ImageSelectorConfig>) {
            super("image-selector");

            this.config = config;
            this.contentSummaryLoader = new api.form.inputtype.content.ContentSummaryLoader();
            this.contentSummaryLoader.addListener({
                onLoading: () => {
                    this.comboBox.setLabel("Searching...");
                },
                onLoaded: (contentSummaries: api.content.ContentSummary[]) => {
                    var options = this.createOptions(contentSummaries);

                    this.comboBox.setOptions(options);
                }
            });

            // requests aren't allowed until allowed contentTypes are specified
            this.contentRequestsAllowed = false;

            var name = new api.schema.relationshiptype.RelationshipTypeName("default");
            if (config.inputConfig.relationshipType.name != null) {
                name = new api.schema.relationshiptype.RelationshipTypeName(config.inputConfig.relationshipType.name);
            }
            new api.schema.relationshiptype.GetRelationshipTypeByNameRequest(name).sendAndParse()
                .done((relationshipType: api.schema.relationshiptype.RelationshipType) => {
                    this.comboBox.setInputIconUrl(relationshipType.getIconUrl());
                    this.contentSummaryLoader.setAllowedContentTypes(relationshipType.getAllowedToTypes());
                    this.contentRequestsAllowed = true;
                    this.loadOptions("");
                });

            this.uploadDialog = new UploadDialog();
            this.uploadDialog.addListener({
                onImageUploaded: (uploadItem: api.ui.UploadItem) => {

                    this.createEmbeddedImageContent(uploadItem);
                }
            });
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
                            this.comboBox.selectOption(<api.ui.combobox.Option<api.content.ContentSummary>>{
                                value: contentSummary.getId(),
                                displayValue: contentSummary
                            });
                        });
                });
            }

            this.appendChild(this.comboBox);

            this.libraryButton = new api.ui.Button("");
            this.libraryButton.addClass("open-library-button");
            this.appendChild(this.libraryButton);

            this.uploadButton = new api.ui.Button("");
            this.uploadButton.addClass("upload-button");
            this.uploadButton.setClickListener((event: any) => {
                var inputMaximum = input.getOccurrences().getMaximum();
                var countSelected = this.comboBox.countSelected();
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
            this.appendChild(this.uploadButton);

            this.appendChild(this.selectedOptionsView);
        }

        getValues(): api.data.Value[] {
            var values: api.data.Value[] = [];
            this.comboBox.getSelectedData().forEach((option: api.ui.combobox.Option<api.content.ContentSummary>) => {

                var value = new api.data.Value(option.value, api.data.ValueTypes.CONTENT_ID);

                values.push(value);
            });
            return values;
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return [];
        }

        getHTMLElement(): HTMLElement {
            return super.getHTMLElement();
        }

        validate(validationRecorder: api.form.ValidationRecorder) {
            // TODO:
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

        maximumOccurrencesReached(): boolean {
            return this.input.getOccurrences().maximumReached(this.comboBox.countSelected());
        }

        addFormItemOccurrencesListener(listener: api.form.FormItemOccurrencesListener) {
            throw new Error("ImageSelector manages occurrences self");
        }

        removeFormItemOccurrencesListener(listener: api.form.FormItemOccurrencesListener) {
            throw new Error("ImageSelector manages occurrences self");
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

        private createComboBox(input: api.form.Input): api.ui.combobox.ComboBox<api.content.ContentSummary> {

            this.selectedOptionsView = new SelectedOptionsView();
            this.selectedOptionsView.addEditSelectedOptionListener((option: api.ui.combobox.SelectedOption<api.content.ContentSummary>) => {
                this.notifyEditContentRequestListeners(option.getOption().displayValue)
            });

            this.selectedOptionsView.addRemoveSelectedOptionListener((option) => {
                this.comboBox.removeSelectedItem(option.getOption());
            });

            var comboBoxConfig = <api.ui.combobox.ComboBoxConfig<api.content.ContentSummary>> {
                rowHeight: 50,
                optionFormatter: this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum()
            };

            var comboBox = new api.ui.combobox.ComboBox<api.content.ContentSummary>(input.getName(), comboBoxConfig);

            this.loadOptions("");

            comboBox.addSelectedOptionRemovedListener(()=> {
                if (!comboBox.maximumOccurrencesReached()) {
                    this.uploadButton.setEnabled(true);
                }
            });

            comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    this.loadOptions(newValue);
                },
                onOptionSelected: (item: api.ui.combobox.Option<api.content.ContentSummary>) => {
                    if (this.comboBox.maximumOccurrencesReached()) {
                        this.uploadButton.setEnabled(false);
                    }
                }
            });

            return comboBox;
        }

        private loadOptions(searchString: string): JQueryPromise<api.rest.Response> {
            if (!this.contentRequestsAllowed || !this.comboBox) {
                return;
            }

            this.contentSummaryLoader.search(searchString);
        }

        private createOptions(contents: api.content.ContentSummary[]): api.ui.combobox.Option<api.content.ContentSummary>[] {
            var options: api.ui.combobox.Option<api.content.ContentSummary>[] = [];
            contents.forEach((content: api.content.ContentSummary) => {
                options.push({
                    value: content.getId(),
                    displayValue: content
                });
            });
            return options;
        }

        private optionFormatter(row: number, cell: number, content: api.content.ContentSummary, columnDef: any,
                                dataContext: api.ui.combobox.Option<api.content.ContentSummary>): string {

            var imgEl = new api.dom.ImgEl();
            imgEl.setClass("icon");
            imgEl.getEl().setSrc(content.getIconUrl());

            var contentEl = new api.dom.DivEl();
            contentEl.setClass("content-summary");

            var displayNameEl = new api.dom.DivEl();
            displayNameEl.setClass("display-name");
            displayNameEl.getEl().setAttribute("title", content.getDisplayName());
            displayNameEl.getEl().setInnerHtml(content.getDisplayName());

            var pathEl = new api.dom.DivEl();
            pathEl.setClass("path");
            pathEl.getEl().setAttribute("title", content.getPath().toString());
            pathEl.getEl().setInnerHtml(content.getPath().toString());

            contentEl.appendChild(displayNameEl);
            contentEl.appendChild(pathEl);

            return imgEl.toString() + contentEl.toString();

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

    api.form.input.InputTypeManager.register("ImageSelector", ImageSelector);

}