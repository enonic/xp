module app_wizard_form_input_type {

    export interface RelationshipConfig {

        relationshipType: {
            name: string
        };
    }

    export class Relationship extends api_dom.DivEl implements InputTypeView {

        private input:api_schema_content_form.Input;

        private comboBox:api_ui_combobox.ComboBox<api_content.ContentSummary>;

        private findContentRequest:api_content.FindContentRequest;

        private contentRequestsAllowed:boolean;

        constructor(config?:RelationshipConfig) {
            super("Relationship", "relationship");

            this.findContentRequest = new api_content.FindContentRequest().setExpand("summary").setCount(100);
            this.contentRequestsAllowed = false; // requests aren't allowed until allowed contentTypes are specified

            new api_schema_relationshiptype.GetRelationshipTypeByQualifiedNameRequest(config.relationshipType.name || "default").send()
                .done((jsonResponse:api_rest.JsonResponse) => {
                    var relationshipType = <api_schema_relationshiptype_json.RelationshipTypeJson> jsonResponse.getJson().relationshipType;
                    this.updateInputIcon(relationshipType.iconUrl);
                    this.findContentRequest.setContentTypes(relationshipType.allowedToTypes);
                    this.contentRequestsAllowed = true;
                    this.updateComboBoxData("");
                })
            ;
        }

        getHTMLElement():HTMLElement {
            return super.getHTMLElement();
        }

        isManagingAdd():boolean {
            return true;
        }

        addFormItemOccurrencesListener(listener:app_wizard_form.FormItemOccurrencesListener) {
            throw new Error("Relationship manages occurrences self");
        }

        removeFormItemOccurrencesListener(listener:app_wizard_form.FormItemOccurrencesListener) {
            throw new Error("Relationship manages occurrences self");
        }

        maximumOccurrencesReached():boolean {
            return this.input.getOccurrences().maximumReached(this.comboBox.countSelected());
        }

        createAndAddOccurrence() {
            throw new Error("Relationship manages occurrences self");
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
        }

        createComboBox(input:api_schema_content_form.Input):api_ui_combobox.ComboBox<api_content.ContentSummary> {
            var comboboxConfig = <api_ui_combobox.ComboBoxConfig<api_content.ContentSummary>>{
                rowHeight: 50,
                optionFormatter: (row:number, cell:number, content:api_content.ContentSummary, columnDef:any, dataContext:api_ui_combobox.OptionData<api_content.ContentSummary>):string => {
                    return this.optionFormatter(content);
                },
                selectedOptionFormatter: this.optionFormatter,
                maximumOccurrences: input.getOccurrences().getMaximum()
            };
            var comboBox = new api_ui_combobox.ComboBox<api_content.ContentSummary>(input.getName(), comboboxConfig);

            this.updateComboBoxData("");

            comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    this.updateComboBoxData(newValue).done((jsonResponse:api_rest.JsonResponse) => {
                        comboBox.showDropdown();
                    });
                }
            });

            return comboBox;
        }

        getValues():api_data.Value[] {

            var values:api_data.Value[] = [];
            this.comboBox.getSelectedData().forEach((option:api_ui_combobox.OptionData<api_content.ContentSummary>) => {
                var value = new api_data.Value(option.value, api_data.ValueTypes.STRING);
                values.push(value);
            });
            return values;
        }

        validate(validationRecorder:app_wizard_form.ValidationRecorder) {

            // TODO:
        }

        valueBreaksRequiredContract(value:api_data.Value):boolean {
            // TODO:
            return false;
        }

        private updateInputIcon(iconUrl:string) {
            this.comboBox.setInputIconUrl(iconUrl);
        }

        private updateComboBoxData(searchString:string):JQueryPromise<api_rest.Response> {
            if (!this.contentRequestsAllowed || !this.comboBox) {
                return;
            }

            return this.findContentRequest.setFulltext(searchString).send()
                .done((jsonResponse:api_rest.JsonResponse) => {
                    var response = jsonResponse.getJson();
                    var options = this.convertToComboBoxData(api_content.ContentSummary.fromJsonArray(response.contents));
                    this.comboBox.setOptions(options);
                })
            ;
        }

        private convertToComboBoxData(contents:api_content.ContentSummary[]):api_ui_combobox.OptionData<api_content.ContentSummary>[] {
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

    app_wizard_form_input.InputTypeManager.register("Relationship", Relationship);
}