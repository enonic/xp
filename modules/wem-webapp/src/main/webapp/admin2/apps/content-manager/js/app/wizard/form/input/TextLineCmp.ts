module app_wizard_form_input {

    export class TextLine extends BaseInputCmp{

        constructor(input:api_schema_content_form.Input) {
            super(input);

        }

        setValue(value:string, arrayIndex:number) {

            super.setValue(value, arrayIndex)
        }
    }
}