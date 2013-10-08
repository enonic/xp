module app_wizard_form_input_type {

    export interface InputTypeView {

        getHTMLElement():HTMLElement;

        layout(input:api_schema_content_form.Input, properties?:api_data.Property[]);

        getValues(): api_data.Value[];

        createAndAddOccurrence();

        maximumOccurrencesReached():boolean;

        addFormItemOccurrencesListener(listener:app_wizard_form.FormItemOccurrencesListener);

        removeFormItemOccurrencesListener(listener:app_wizard_form.FormItemOccurrencesListener);
    }
}