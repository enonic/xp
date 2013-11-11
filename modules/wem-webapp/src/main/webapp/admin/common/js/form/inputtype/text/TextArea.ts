module api_form_inputtype_text {

    export class TextArea extends api_form_inputtype_support.BaseInputTypeView {

        private rows:number;
        private columns:number;

        constructor(config:api_form_inputtype.InputTypeViewConfig<TextAreaConfig>) {
            super("TextArea");
            this.rows = config.inputConfig.rows;
            this.columns = config.inputConfig.columns;
        }

        createInputOccurrenceElement(index:number, property:api_data.Property):api_dom.Element {

            var inputEl = new api_ui.TextArea(this.getInput().getName() + "-" + index);
            if (this.rows) {
                inputEl.setRows(this.rows);
            }
            if (this.columns) {
                inputEl.setColumns(this.columns);
            }
            if (property != null) {
                inputEl.setValue(property.getString());
            }
            return inputEl;
        }

        getValue(occurrence:api_dom.Element):api_data.Value {
            var inputEl = <api_ui.TextArea>occurrence;
            return new api_data.Value(inputEl.getValue(), api_data.ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value:api_data.Value):boolean {
            // TODO:
            return false;
        }
    }

    api_form_input.InputTypeManager.register("TextArea", TextArea);
}