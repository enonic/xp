module api_form_inputtype_content_image {

    export interface ImageSelectorConfig {

        relationshipType: {
            name: string
        }
    }

    export class ImageSelector extends api_dom.DivEl implements api_form_inputtype.InputTypeView {

        private config:api_form_inputtype.InputTypeViewConfig<ImageSelectorConfig>;

        private input:api_form.Input;

        private comboBox:api_ui_combobox.ComboBox<SelectedOption>;

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

            new api_schema_relationshiptype.GetRelationshipTypeByQualifiedNameRequest(config.inputConfig.relationshipType.name || "default").send()
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
                    //this.createTemporaryImageContent(id, fileName, mimeType);

                    var dataPath = api_data.DataPath.fromString(this.config.dataPath.toString() + "[" + this.comboBox.countSelected() + "]");
                    var attachmentName = new api_content.AttachmentName(dataPath, fileName);
                    var imageSelectorOption = SelectedOption.fromUpload(id, attachmentName);
                    this.comboBox.selectOption(<api_ui_combobox.OptionData<SelectedOption>>{
                        value: id,
                        displayValue: imageSelectorOption
                    });
                }
            });
        }

        layout(input:api_form.Input, properties:api_data.Property[]) {

            this.input = input;

            this.selectedOptionsView = new SelectedOptionsView();
            this.comboBox = this.createComboBox(input);

            if (properties != null) {
                properties.forEach((property:api_data.Property) => {
                    new api_content.GetContentByIdRequest(property.getString())
                        .setExpand(api_content.ContentResourceRequest.EXPAND_SUMMARY)
                        .send()
                        .done((jsonResponse:api_rest.JsonResponse<api_content_json.ContentSummaryJson>) => {
                            var contentSummary = new api_content.ContentSummary(jsonResponse.getResult());
                            var imageSelectorOption = SelectedOption.fromContent(contentSummary);
                            this.comboBox.selectOption(<api_ui_combobox.OptionData<SelectedOption>>{
                                value: contentSummary.getId(),
                                displayValue: imageSelectorOption
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
            this.comboBox.getSelectedData().forEach((option:api_ui_combobox.OptionData<SelectedOption>) => {

                // Value is a string either containing: contentId=xx
                // Value is a string either containing: attachmentName=xx
                var value = new api_data.Value(option.value, api_data.ValueTypes.STRING);

                values.push(value);
            });
            return values;
        }

        getAttachments():api_content.Attachment[] {
            var attachments:api_content.Attachment[] = [];

            return attachments;
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

        private createComboBox(input:api_form.Input):api_ui_combobox.ComboBox<SelectedOption> {

            var comboBoxConfig = <api_ui_combobox.ComboBoxConfig<SelectedOption>> {
                rowHeight: 50,
                optionFormatter:  this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum()
            };

            var comboBox = new api_ui_combobox.ComboBox<SelectedOption>(input.getName(), comboBoxConfig);

            this.loadOptions("");

            comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    this.loadOptions(newValue);
                },
                onSelectedOptionRemoved: (item:api_ui_combobox.OptionData<SelectedOption>) => {
                    if (!comboBox.maximumOccurrencesReached()) {
                        this.uploadButton.setEnabled(true);
                    }
                },
                onOptionSelected: (item:api_ui_combobox.OptionData<SelectedOption>) => {
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

        private createOptions(contents:api_content.ContentSummary[]):api_ui_combobox.OptionData<SelectedOption>[] {
            var options:api_ui_combobox.OptionData<SelectedOption>[] = [];
            contents.forEach((content:api_content.ContentSummary) => {
                var imageSelectorSelectedOption:SelectedOption = SelectedOption.fromContent(content);

                options.push({
                    value: content.getId(),
                    displayValue: imageSelectorSelectedOption
                });
            });
            return options;
        }

        private optionFormatter(row:number, cell:number, option:SelectedOption, columnDef:any, dataContext:api_ui_combobox.OptionData<SelectedOption>):string {

            if( option.hasContent() ) {
                var content = option.getContent();

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
            else {
                throw new Error("No column formatter supported for ImageSelectorOption without content");
            }
        }

    }

    api_form_input.InputTypeManager.register("ImageSelector", ImageSelector);

}