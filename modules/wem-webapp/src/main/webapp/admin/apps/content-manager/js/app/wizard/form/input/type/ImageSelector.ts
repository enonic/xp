module app_wizard_form_input_type {

    export interface ImageSelectorConfig {

        relationshipType: {
            name: string
        }

    }

    export class ImageSelector extends api_dom.DivEl implements InputTypeView {

        private input:api_schema_content_form.Input;

        private comboBox:api_ui_combobox.ComboBox<api_content.ContentSummary>;

        private libraryButton:api_ui.Button;

        private uploadButton:api_ui.Button;

        private findContentRequest:api_content.FindContentRequest<api_content_json.ContentSummaryJson>;

        private contentRequestsAllowed:boolean;

        constructor(config:ImageSelectorConfig) {
            super("ImageSelector", "image-selector");

            this.findContentRequest = new api_content.FindContentRequest().setExpand("summary").setCount(100);
            this.contentRequestsAllowed = false;  // requests aren't allowed until allowed contentTypes are specified

            new api_schema_relationshiptype.GetRelationshipTypeByQualifiedNameRequest(config.relationshipType.name || "default").send()
                .done((jsonResponse:api_rest.JsonResponse<api_schema_relationshiptype_json.RelationshipTypeJson>) => {
                    var relationshipType = <api_schema_relationshiptype_json.RelationshipTypeJson> jsonResponse.getJson().relationshipType;
                    this.comboBox.setInputIconUrl(relationshipType.iconUrl);
                    this.findContentRequest.setContentTypes(relationshipType.allowedToTypes);
                    this.contentRequestsAllowed = true;
                    this.loadOptions("");
                })
            ;
        }

        layout(input:api_schema_content_form.Input, properties:api_data.Property[]) {

            this.input = input;

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
            this.appendChild(this.uploadButton);
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

        validate(validationRecorder:app_wizard_form.ValidationRecorder) {
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

        addFormItemOccurrencesListener(listener:app_wizard_form.FormItemOccurrencesListener) {
            throw new Error("ImageSelector manages occurrences self");
        }

        removeFormItemOccurrencesListener(listener:app_wizard_form.FormItemOccurrencesListener) {
            throw new Error("ImageSelector manages occurrences self");
        }

        private createComboBox(input:api_schema_content_form.Input):api_ui_combobox.ComboBox<api_content.ContentSummary> {
            var comboBoxConfig = <api_ui_combobox.ComboBoxConfig<api_content.ContentSummary>> {
                rowHeight: 50,
                optionFormatter: (row:number, cell:number, content:api_content.ContentSummary, columnDef:any, dataContext:api_ui_combobox.OptionData<api_content.ContentSummary>):string => {
                    return this.optionFormatter(content);
                },
                selectedOptionFormatter: this.optionFormatter,
                maximumOccurrences: input.getOccurrences().getMaximum()
            };

            var comboBox = new api_ui_combobox.ComboBox<api_content.ContentSummary>(input.getName(), comboBoxConfig);

            this.loadOptions("");

            comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    this.loadOptions(newValue).done((jsonResponse:api_rest.JsonResponse) => {
                        this.comboBox.showDropdown();
                    });
                }
            });

            return comboBox;
        }

        private loadOptions(searchString:string):JQueryPromise<api_rest.Response> {
            if (!this.contentRequestsAllowed || !this.comboBox) {
                return;
            }

            return this.findContentRequest.setFulltext(searchString).send()
                .done((jsonResponse:api_rest.JsonResponse<api_content.FindContentResult<api_content_json.ContentSummaryJson>>) => {
                    var result = jsonResponse.getResult();
                    var options = this.createOptions(api_content.ContentSummary.fromJsonArray(result.contents));
                    this.comboBox.setOptions(options);
                })
            ;
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

        private optionFormatter(content:api_content.ContentSummary):string {
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

    }

    app_wizard_form_input.InputTypeManager.register("ImageSelector", ImageSelector);

}