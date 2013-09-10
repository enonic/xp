module app_wizard_form_input {

    export interface InputTypeView {

        layout(input:api_schema_content_form.Input, properties?:api_data.Property[]);

        getValues(): string[];

        getHTMLElement():HTMLElement;
    }
}