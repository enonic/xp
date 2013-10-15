module app_wizard_form_input_type {

    export interface RelationshipConfig {

        relationshipType:string;
    }

    export class Relationship extends api_dom.DivEl implements InputTypeView {

        private input:api_schema_content_form.Input;

        private comboBox:api_ui_combobox.ComboBox<api_content.ContentSummary>;

        private findContentRequest:api_content.FindContentRequest;

        constructor(config?:RelationshipConfig) {
            super("Relationship", "relationship");

            this.findContentRequest = new api_content.FindContentRequest().setExpand("summary").setCount(100);
            // TODO: fetch RelationshipType specified in config
            // use RelationshipType.icon for icon to be displayed in text input
            // use RelationshipType.getAllowedToTypes to restrict search to only content types listed here (if getAllowedToTypes() is empty it means every type of content)
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

        public maximumOccurrencesReached():boolean {
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
            var comboboxConfig:api_ui_combobox.ComboBoxConfig<api_content.ContentSummary> = <api_ui_combobox.ComboBoxConfig<api_content.ContentSummary>>{
                iconUrl: "../../../admin/resources/images/default_content.png",
                rowHeight: 50,
                optionFormatter: this.optionFormatter,
                selectedOptionFormatter: this.selectedOptionFormatter,
                maximumOccurrences: input.getOccurrences().getMaximum()
            };
            var comboBox = new api_ui_combobox.ComboBox<api_content.ContentSummary>(input.getName(), comboboxConfig);

            this.findContentRequest.setFulltext("").send()
                .done((jsonResponse:api_rest.JsonResponse) => {
                    var response = jsonResponse.getJson();
                    var options = this.convertToComboBoxData(api_content.ContentSummary.fromJsonArray(response.contents));
                    this.comboBox.setOptions(options);
                })
            ;

            comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    this.findContentRequest.setFulltext(newValue.trim()).send()
                        .done((jsonResponse:api_rest.JsonResponse) => {
                            var response = jsonResponse.getJson();
                            var options = this.convertToComboBoxData(api_content.ContentSummary.fromJsonArray(response.contents));
                            this.comboBox.setOptions(options);
                            this.comboBox.showDropdown();
                        })
                    ;
                }
            });

            return comboBox;
        }

        getValues():api_data.Value[] {

            var values:api_data.Value[] = [];
            this.comboBox.getSelectedData().forEach((option:api_ui_combobox.OptionData<api_content.ContentSummary>)  => {
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

        private optionFormatter(row, cell, content:api_content.ContentSummary, columnDef, dataContext):string {
            return '<img src="' + content.getIconUrl() + '" class="icon"/>' +
                   '<div class="info">' +
                   '<div class="title" title="' + content.getDisplayName() + '">' + content.getDisplayName() + '</div>' +
                   '<div class="description" title="' + content.getPath().toString() + '">' + content.getPath().toString() + '</div>' +
                   '</div>';
        }

        private selectedOptionFormatter(content:api_content.ContentSummary) {
            return '<img src="' + content.getIconUrl() + '" class="icon"/>' +
                   '<div class="info">' +
                   '<div class="title" title="' + content.getDisplayName() + '">' + content.getDisplayName() + '</div>' +
                   '<div class="description" title="' + content.getPath().toString() + '">' + content.getPath().toString() + '</div>' +
                   '</div>';
        }

    }

    app_wizard_form_input.InputTypeManager.register("Relationship", Relationship);
}