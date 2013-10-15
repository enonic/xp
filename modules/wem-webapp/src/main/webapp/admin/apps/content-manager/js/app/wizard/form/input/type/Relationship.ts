module app_wizard_form_input_type {

    export class Relationship extends ComboBox {

        private findContentRequest:api_content.FindContentRequest;

        constructor() {
            super({options:[]});
            this.addClass("relationship");

            this.findContentRequest = new api_content.FindContentRequest().setExpand("summary").setCount(100);
        }

        createComboBox(input:api_schema_content_form.Input):api_ui_combobox.ComboBox {
            var comboboxConfig = {
                iconUrl: "../../../admin/resources/images/default_content.png",
                rowHeight: 50,
                optionFormatter: this.optionFormatter,
                selectedOptionFormatter: this.selectedOptionFormatter,
                maximumOccurrences: input.getOccurrences().getMaximum()
            };
            var comboBox = new api_ui_combobox.ComboBox(input.getName(), comboboxConfig);

            this.findContentRequest.setFulltext("").send()
                .done((jsonResponse:api_rest.JsonResponse) => {
                    var response = jsonResponse.getJson();
                    var options = this.convertToComboBoxData(response.contents);
                    this.getComboBox().setOptions(options);
                })
            ;

            comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    this.findContentRequest.setFulltext(newValue.trim()).send()
                        .done((jsonResponse:api_rest.JsonResponse) => {
                            var response = jsonResponse.getJson();
                            var options = this.convertToComboBoxData(response.contents);
                            this.getComboBox().setOptions(options);
                            this.getComboBox().showDropdown();
                        })
                    ;
                }
            });

            return comboBox;
        }

        private convertToComboBoxData(contents:any[]): api_ui_combobox.OptionData[] {
            var options = [];
            contents.forEach((content:any) => {
                options.push({
                    value: content.id,
                    displayValue: {
                        iconUrl: content.iconUrl,
                        title: content.displayName,
                        description: content.path
                    }
                });
            });
            return options;
        }

        private optionFormatter(row, cell, value, columnDef, dataContext):string {
            return '<img src="'+value.iconUrl+'" class="icon"/>' +
                   '<div class="info">' +
                   '<div class="title" title="'+value.title+'">'+value.title+'</div>' +
                   '<div class="description" title="'+value.description+'">'+value.description+'</div>' +
                   '</div>';
        }

        private selectedOptionFormatter(value) {
            return '<img src="'+value.iconUrl+'" class="icon"/>' +
                   '<div class="info">' +
                   '<div class="title" title="'+value.title+'">'+value.title+'</div>' +
                   '<div class="description" title="'+value.description+'">'+value.description+'</div>' +
                   '</div>';
        }

    }

    app_wizard_form_input.InputTypeManager.register("Relationship", Relationship);
}