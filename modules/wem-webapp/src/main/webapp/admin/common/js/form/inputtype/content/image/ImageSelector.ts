module api_form_inputtype_content_image {

    export interface ImageSelectorConfig {

        relationshipType: {
            name: string
        }
    }

    export class ImageSelector extends api_dom.DivEl implements api_form_inputtype.InputTypeView {

        private config:api_form_inputtype.InputTypeViewConfig<ImageSelectorConfig>;

        private input:api_form.Input;

        private comboBox:api_ui_combobox.ComboBox<api_content.ContentSummary>;

        private libraryButton:api_ui.Button;

        private uploadButton:api_ui.Button;

        private selectedOptionsView:SelectedOptionsView;

        private contentSummaryLoader:api_form_inputtype_content.ContentSummaryLoader;

        private contentRequestsAllowed:boolean;

        private uploadDialog:UploadDialog;

        constructor(config:api_form_inputtype.InputTypeViewConfig<ImageSelectorConfig>) {
            super("ImageSelector", "image-selector");

            this.config = config;
            this.contentSummaryLoader = new api_form_inputtype_content.ContentSummaryLoader();
            this.contentSummaryLoader.addListener({
                onLoading: () => {
                    this.comboBox.setLabel("Searching...");
                },
                onLoaded: (contentSummaries:api_content.ContentSummary[]) => {
                    var options = this.createOptions(contentSummaries);

                    this.comboBox.setOptions(options);
                }
            });

            // requests aren't allowed until allowed contentTypes are specified
            this.contentRequestsAllowed = false;

            var name = new api_schema_relationshiptype.RelationshipTypeName("default");
            if( config.inputConfig.relationshipType.name != null ) {
                name = new api_schema_relationshiptype.RelationshipTypeName(config.inputConfig.relationshipType.name);
            }
            new api_schema_relationshiptype.GetRelationshipTypeByNameRequest(name).send()
                .done((jsonResponse:api_rest.JsonResponse<api_schema_relationshiptype_json.RelationshipTypeJson>) => {
                    var relationshipType = jsonResponse.getResult();
                    this.comboBox.setInputIconUrl(relationshipType.iconUrl);
                    this.contentSummaryLoader.setAllowedContentTypes(relationshipType.allowedToTypes);
                    this.contentRequestsAllowed = true;
                    this.loadOptions("");
                });

            this.uploadDialog = new UploadDialog();
            this.uploadDialog.addListener({
                onImageUploaded: (id:string, fileName:string, mimeType:string) => {

                    this.createTemporaryImageContent(id, fileName, mimeType);
                }
            });
        }

        layout(input:api_form.Input, properties:api_data.Property[]) {

            this.input = input;

            this.comboBox = this.createComboBox(input);

            if (properties != null) {
                properties.forEach((property:api_data.Property) => {
                    new api_content.GetContentByIdRequest(property.getString())
                        .setExpand(api_content.ContentResourceRequest.EXPAND_SUMMARY)
                        .send()
                        .done((jsonResponse:api_rest.JsonResponse<api_content_json.ContentSummaryJson>) => {
                            var contentSummary = new api_content.ContentSummary(jsonResponse.getResult());
                            this.comboBox.selectOption(<api_ui_combobox.Option<api_content.ContentSummary>>{
                                value: contentSummary.getId(),
                                displayValue: contentSummary
                            });
                        });
                });
            }

            this.appendChild(this.comboBox);

            this.libraryButton = new api_ui.Button("");
            this.libraryButton.addClass("open-library-button");
            this.appendChild(this.libraryButton);

            this.uploadButton = new api_ui.Button("");
            this.uploadButton.addClass("upload-button");
            this.uploadButton.setClickListener((event:any) => {
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

        getValues(): api_data.Value[] {
            var values:api_data.Value[] = [];
            this.comboBox.getSelectedData().forEach((option:api_ui_combobox.Option<api_content.ContentSummary>) => {

                var value = new api_data.Value(option.value, api_data.ValueTypes.CONTENT_ID);

                values.push(value);
            });
            return values;
        }

        getAttachments():api_content.Attachment[] {
            return [];
        }

        getHTMLElement(): HTMLElement {
            return super.getHTMLElement();
        }

        validate(validationRecorder:api_form.ValidationRecorder) {
            // TODO:
        }

        createAndAddOccurrence() {
            throw new Error("ImageSelector manages occurrences self");
        }

        isManagingAdd():boolean {
            return true;
        }

        maximumOccurrencesReached():boolean {
            return this.input.getOccurrences().maximumReached(this.comboBox.countSelected());
        }

        addFormItemOccurrencesListener(listener:api_form.FormItemOccurrencesListener) {
            throw new Error("ImageSelector manages occurrences self");
        }

        removeFormItemOccurrencesListener(listener:api_form.FormItemOccurrencesListener) {
            throw new Error("ImageSelector manages occurrences self");
        }

        private createComboBox(input:api_form.Input):api_ui_combobox.ComboBox<api_content.ContentSummary> {

            this.selectedOptionsView = new SelectedOptionsView();

            var comboBoxConfig = <api_ui_combobox.ComboBoxConfig<api_content.ContentSummary>> {
                rowHeight: 50,
                optionFormatter: this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum(),
                hideComboBoxWhenMaxReached: true
            };

            var comboBox = new api_ui_combobox.ComboBox<api_content.ContentSummary>(input.getName(), comboBoxConfig);

            this.loadOptions("");

            comboBox.addSelectedOptionRemovedListener(()=>{
                if (!comboBox.maximumOccurrencesReached()) {
                    this.uploadButton.setEnabled(true);
                }
            });

            comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    this.loadOptions(newValue);
                },
                onOptionSelected: (item:api_ui_combobox.Option<api_content.ContentSummary>) => {
                    if (this.comboBox.maximumOccurrencesReached()) {
                        this.uploadButton.setEnabled(false);
                    }
                }
            });

            return comboBox;
        }

        private loadOptions(searchString:string):JQueryPromise<api_rest.Response> {
            if (!this.contentRequestsAllowed || !this.comboBox) {
                return;
            }

            this.contentSummaryLoader.search(searchString);
        }

        private createOptions(contents:api_content.ContentSummary[]):api_ui_combobox.Option<api_content.ContentSummary>[] {
            var options:api_ui_combobox.Option<api_content.ContentSummary>[] = [];
            contents.forEach((content:api_content.ContentSummary) => {
                options.push({
                    value: content.getId(),
                    displayValue: content
                });
            });
            return options;
        }

        private optionFormatter(row:number, cell:number, content:api_content.ContentSummary, columnDef:any, dataContext:api_ui_combobox.Option<api_content.ContentSummary>):string {

            var imgEl = new api_dom.ImgEl();
            imgEl.setClass("icon");
            imgEl.getEl().setSrc(content.getIconUrl());

            var contentEl = new api_dom.DivEl();
            contentEl.setClass("content-summary");

            var displayNameEl = new api_dom.DivEl();
            displayNameEl.setClass("display-name");
            displayNameEl.getEl().setAttribute("title", content.getDisplayName());
            displayNameEl.getEl().setInnerHtml(content.getDisplayName());

            var pathEl = new api_dom.DivEl();
            pathEl.setClass("path");
            pathEl.getEl().setAttribute("title", content.getPath().toString());
            pathEl.getEl().setInnerHtml(content.getPath().toString());

            contentEl.appendChild(displayNameEl);
            contentEl.appendChild(pathEl);

            return imgEl.toString() + contentEl.toString();

        }

        private createTemporaryImageContent(uploadID:string, attachmentNameAsString:string, mimeType:string) {

            new api_schema_content.GetContentTypeByNameRequest(new api_schema_content.ContentTypeName( "image" ) ).send().done((response:api_rest.JsonResponse<api_schema_content_json.ContentTypeJson>)=>{
                var contentType = new api_schema_content.ContentType(response.getResult());
                var attachmentName = new api_content.AttachmentName(attachmentNameAsString);

                var contentDataFactory = new api_content_image.ImageContentDataFactory();
                contentDataFactory.setImage(attachmentName);
                contentDataFactory.setMimeType(mimeType);
                var contentData = contentDataFactory.create();

                var attachment = new api_content.Attachment(uploadID, attachmentName);

                new api_content.CreateContentRequest()
                    .setDraft(true)
                    .setName(attachmentName.toString())
                    .setContentType(contentType.getContentTypeName())
                    .setDisplayName(attachmentName.toString())
                    .setForm(contentType.getForm())
                    .setContentData(contentData)
                    .addAttachment(attachment)
                    .send()
                    .done((createResponse:api_rest.JsonResponse<any>) => {
                          var jsonResult = createResponse.getJson();

                          if (jsonResult.result != null) {
                              var content = new api_content.Content(jsonResult.result);
                              this.comboBox.selectOption({
                                                             value: content.getId(),
                                                             displayValue: content
                                                         });
                          } else if (jsonResult.failure) {
                              api_notify.showError(jsonResult.failure);
                          }
                     });
            });
        }
    }

    api_form_input.InputTypeManager.register("ImageSelector", ImageSelector);

}