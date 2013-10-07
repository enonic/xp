module app_wizard_form_input_type {

    export interface InputTypeView {

        layout(input:api_schema_content_form.Input, properties?:api_data.Property[]);

        getValues(): api_data.Value[];

        getHTMLElement():HTMLElement;
    }
}