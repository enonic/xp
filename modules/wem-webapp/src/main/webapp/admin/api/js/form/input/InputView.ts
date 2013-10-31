module api_form_input {

    export class InputView extends api_form.FormItemView {

        private input:api_form.Input;

        private properties:api_data.Property[];

        private inputTypeView:api_form_input_type.InputTypeView;

        private bottomButtonRow:api_dom.DivEl;

        private addButton:api_ui.Button;

        constructor(input:api_form.Input, properties?:api_data.Property[]) {
            super("InputView", "input-view", input);

            this.input = input;
            this.properties = properties;

            this.doLayout();
            this.refresh();
        }

        private doLayout() {

            var label = new InputLabel(this.input);
            this.appendChild(label);

            var inputType:api_form.InputTypeName = this.input.getInputType();

            if (InputTypeManager.isRegistered(inputType.getName())) {
                var inputTypeConfig = this.input.getInputTypeConfig();
                this.inputTypeView = InputTypeManager.createView(inputType.getName(), inputTypeConfig);
            }
            else {
                console.log("Input type [" + inputType.getName() + "] need to be registered first.");
                this.inputTypeView = InputTypeManager.createView("NoInputTypeFound");
            }

            this.inputTypeView.layout(this.input, this.properties);
            if (this.inputTypeView instanceof api_form_input_type.BaseInputTypeView) {
                this.appendChild(<api_form_input_type.BaseInputTypeView>this.inputTypeView);
            }
            else {
                this.appendChild(api_dom.Element.fromHtmlElement(this.inputTypeView.getHTMLElement()))
            }

            if (!this.inputTypeView.isManagingAdd()) {

                this.inputTypeView.addFormItemOccurrencesListener(<api_form.FormItemOccurrencesListener>{
                    onOccurrenceAdded: (occurrenceAdded:api_form.FormItemOccurrence) => {
                        this.refresh();
                    },
                    onOccurrenceRemoved: (occurrenceRemoved:api_form.FormItemOccurrence) => {
                        this.refresh();
                    }
                });

                this.addButton = new api_ui.Button("Add");
                this.addButton.setClass("add-button");

                this.addButton.setClickListener(() => {
                    this.inputTypeView.createAndAddOccurrence();
                });

                this.bottomButtonRow = new api_dom.DivEl(null, "bottom-button-row");
                this.appendChild(this.bottomButtonRow);
                this.bottomButtonRow.appendChild(this.addButton);
            }
        }

        refresh() {
            if (!this.inputTypeView.isManagingAdd()) {
                this.addButton.setVisible(!this.inputTypeView.maximumOccurrencesReached());
            }
        }

        getData():api_data.Data[] {
            return this.getProperties();
        }

        getValue(index:number):api_data.Value {
            return this.inputTypeView.getValues()[index];
        }

        getProperties():api_data.Property[] {
            var properties:api_data.Property[] = [];
            this.inputTypeView.getValues().forEach((value:api_data.Value, index:number) => {
                properties[index] = new api_data.Property(this.input.getName(), value);
            });
            return properties;
        }

        validate(validationRecorder:api_form.ValidationRecorder) {

            this.inputTypeView.validate(validationRecorder);
        }

        hasValidOccurrences():boolean {

            return this.getData().length >= this.input.getOccurrences().getMaximum();
        }
    }
}