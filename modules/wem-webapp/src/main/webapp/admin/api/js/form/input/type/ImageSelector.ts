module api_form_input_type {

    export interface ImageSelectorConfig {

        relationshipType: {
            name: string
        }

    }

    export class ImageSelector extends api_dom.DivEl implements InputTypeView {

        private input:api_form.Input;

        private comboBox:api_ui_combobox.ComboBox<api_content.ContentSummary>;

        private libraryButton:api_ui.Button;

        private uploadButton:api_ui.Button;

        private selectedOptionsView:ImageSelectorSelectedOptionsView;

        private contentSummaryLoader:ContentSummaryLoader;

        private contentRequestsAllowed:boolean;

        private uploadDialog:ImageSelectorUploadDialog;

        constructor(config:ImageSelectorConfig) {
            super("ImageSelector", "image-selector");

            this.contentSummaryLoader = new ContentSummaryLoader();
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

            new api_schema_relationshiptype.GetRelationshipTypeByQualifiedNameRequest(config.relationshipType.name || "default").send()
                .done((jsonResponse:api_rest.JsonResponse<api_schema_relationshiptype_json.RelationshipTypeJson>) => {
                    var relationshipType = jsonResponse.getResult();
                    this.comboBox.setInputIconUrl(relationshipType.iconUrl);
                    this.contentSummaryLoader.setAllowedContentTypes(relationshipType.allowedToTypes);
                    this.contentRequestsAllowed = true;
                    this.loadOptions("");
                })
            ;

            this.uploadDialog = new api_form_input_type.ImageSelectorUploadDialog();
            this.uploadDialog.addListener({
                onImageUploaded: (id:string, name:string, mimeType:string) => {
                    this.createTemporaryImageContent(id, name, mimeType);
                }
            });
        }

        layout(input:api_form.Input, properties:api_data.Property[]) {

            this.input = input;

            this.selectedOptionsView = new ImageSelectorSelectedOptionsView();
            this.comboBox = this.createComboBox(input);

            if (properties != null) {
                var valueArray:string[] = [];
                properties.forEach((property:api_data.Property) => {
                    valueArray.push(property.getString());
                });
                this.comboBox.setValues(valueArray);
            }

            this.appendChild(this.comboBox);

            this.libraryButton = new api_ui.Button("");
            this.libraryButton.addClass("open-library-button");
            this.appendChild(this.libraryButton);

            this.uploadButton = new api_ui.Button("");
            this.uploadButton.addClass("upload-button");
            this.uploadButton.setClickListener((event:any) => {
                this.uploadDialog.open();
            });
            this.appendChild(this.uploadButton);

            this.appendChild(this.selectedOptionsView);
        }

        getValues(): api_data.Value[] {
            var values:api_data.Value[] = [];
            this.comboBox.getSelectedData().forEach((option:api_ui_combobox.OptionData<api_content.ContentSummary>) => {
                var value = new api_data.Value(option.value, api_data.ValueTypes.STRING);
                values.push(value);
            });
            return values;
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
            var comboBoxConfig = <api_ui_combobox.ComboBoxConfig<api_content.ContentSummary>> {
                rowHeight: 50,
                optionFormatter:  this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum()
            };

            var comboBox = new api_ui_combobox.ComboBox<api_content.ContentSummary>(input.getName(), comboBoxConfig);

            this.loadOptions("");

            comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    this.loadOptions(newValue);
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

        private createOptions(contents:api_content.ContentSummary[]):api_ui_combobox.OptionData<api_content.ContentSummary>[] {
            var options = [];
            contents.forEach((content:api_content.ContentSummary) => {
                options.push({
                    value: content.getId(),
                    displayValue: content
                });
            });
            return options;
        }

        private optionFormatter(row:number, cell:number, content:api_content.ContentSummary, columnDef:any, dataContext:api_ui_combobox.OptionData<api_content.ContentSummary>):string {
            var img = new api_dom.ImgEl();
            img.setClass("icon");
            img.getEl().setSrc(content.getIconUrl());

            var contentSummary = new api_dom.DivEl();
            contentSummary.setClass("content-summary");

            var displayName = new api_dom.DivEl();
            displayName.setClass("display-name");
            displayName.getEl().setAttribute("title", content.getDisplayName());
            displayName.getEl().setInnerHtml(content.getDisplayName());

            var path = new api_dom.DivEl();
            path.setClass("path");
            path.getEl().setAttribute("title", content.getPath().toString());
            path.getEl().setInnerHtml(content.getPath().toString());

            contentSummary.appendChild(displayName);
            contentSummary.appendChild(path);

            return img.toString() + contentSummary.toString();
        }

        private createTemporaryImageContent(id:string, name:string, mimeType:string) {
            var contentData = new api_content.ContentData();
            contentData.addData(api_data.Property.fromStrings("image", name, "String"));
            contentData.addData(api_data.Property.fromStrings("mimeType", mimeType, "String"));
            new api_content.CreateContentRequest()
                .setTemporary(true)
                .setContentName(name)
                .setContentType("image")
                .setDisplayName(name)
                .setContentData(contentData)
                .setAttachments([{
                    uploadId: id,
                    attachmentName: name
                }])
                .send()
                .done((createResponse:api_rest.JsonResponse<any>) => {
                    var responseObj = createResponse.getJson();

                    if (responseObj.created) {
                        this.loadTemporaryImageContent(responseObj.contentId);
                    } else if (responseObj.failure) {
                        api_notify.showError(responseObj.failure);
                    }
                })
            ;
        }

        private loadTemporaryImageContent(contentId:string) {
            new api_content.GetContentByIdRequest(contentId).send()
                .done((getResponse:api_rest.JsonResponse<any>) => {
                    var responseObj = getResponse.getJson();
                    var contentSummary = new api_content.ContentSummary(responseObj);
                    this.comboBox.selectOption({
                        value: contentSummary.getId(),
                        displayValue: contentSummary
                    });
                })
            ;
        }

    }

    api_form_input.InputTypeManager.register("ImageSelector", ImageSelector);

}