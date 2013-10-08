module app_wizard_form_input {

    export class InputView extends app_wizard_form.FormItemView {

        private input:api_schema_content_form.Input;

        private properties:api_data.Property[];

        private baseInputTypeView:app_wizard_form_input_type.BaseInputTypeView;

        private bottomButtonRow:api_dom.DivEl;

        private addButton:api_ui.Button;

        constructor(input:api_schema_content_form.Input, properties?:api_data.Property[]) {
            super("InputView", "input-view", input);

            this.input = input;
            this.properties = properties != null ? properties : [];

            this.doLayout();
            this.refresh();
        }

        private doLayout() {

            var label = new InputLabel(this.input);
            this.appendChild(label);

            var inputType:api_schema_content_form.InputTypeName = this.input.getInputType();

            var newInputPrototype;
            if (InputTypeManager.isRegistered(inputType.getName())) {
                var inputTypeConfig = this.input.getInputTypeConfig();
                newInputPrototype = InputTypeManager.createView(inputType.getName(), inputTypeConfig);
            }
            else {
                console.log("Input type [" + inputType.getName() + "] need to be registered first.");
                newInputPrototype = InputTypeManager.createView("NoInputTypeFound");
            }

            this.baseInputTypeView = newInputPrototype;
            this.baseInputTypeView.layout(this.input, this.properties);
            this.appendChild(this.baseInputTypeView);
            this.baseInputTypeView.getInputOccurrences().addListener(<app_wizard_form.FormItemOccurrencesListener>{
                onOccurrenceAdded: (occurrenceAdded:app_wizard_form.FormItemOccurrence) => {
                    this.refresh();
                },
                onOccurrenceRemoved: (occurrenceRemoved:app_wizard_form.FormItemOccurrence) => {
                    this.refresh();
                }
            });

            this.addButton = new api_ui.Button("Add");
            this.addButton.setClass("add-button");
            this.addButton.setClickListener(() => {
                this.baseInputTypeView.getInputOccurrences().createAndAddOccurrence();
            });

            this.bottomButtonRow = new api_dom.DivEl(null, "bottom-button-row");
            this.appendChild(this.bottomButtonRow);
            this.bottomButtonRow.appendChild(this.addButton);
        }

        refresh() {

            this.addButton.setVisible(this.baseInputTypeView.getInputOccurrences().showAddButton());
        }

        getData():api_data.Data[] {
            return this.getProperties();
        }

        getProperties():api_data.Property[] {

            var properties:api_data.Property[] = [];
            this.baseInputTypeView.getValues().forEach((value:api_data.Value, index:number) => {
                properties[index] = new api_data.Property(this.input.getName(), value);
            });
            return properties;
        }
    }
}