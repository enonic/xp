declare var plupload;

module api_form_input_type {


    export interface ImageConfig {
        rows:number;
        columns:number;
    }


    export class Image extends BaseInputTypeView {

        constructor(config?:ImageConfig) {
            super("Image");
            this.addClass("image");
        }

        createInputOccurrenceElement(index:number, property:api_data.Property):api_dom.Element {

            var inputEl = new api_ui.ImageUploader(this.getInput().getName() + "-" + index, api_util.getRestUri("upload"));
            if (property != null) {
                inputEl.setValue(property.getString());
            }
            return inputEl;
        }

        getValue(occurrence:api_dom.Element):api_data.Value {
            var inputEl = <api_ui.ImageUploader>occurrence;
            return new api_data.Value(inputEl.getValue(), api_data.ValueTypes.STRING);
        }

        valueBreaksRequiredContract(value:api_data.Value):boolean {
            // TODO:
            return false;
        }
    }

    api_form_input.InputTypeManager.register("Image", Image);

}