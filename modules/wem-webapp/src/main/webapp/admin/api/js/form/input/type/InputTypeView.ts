module api_form_input_type {

    export interface InputTypeView {

        getHTMLElement():HTMLElement;

        layout(input:api_form.Input, properties:api_data.Property[]);

        getValues(): api_data.Value[];

        validate(validationRecorder:api_form.ValidationRecorder);

        createAndAddOccurrence();

        isManagingAdd():boolean;

        maximumOccurrencesReached():boolean;

        addFormItemOccurrencesListener(listener:api_form.FormItemOccurrencesListener);

        removeFormItemOccurrencesListener(listener:api_form.FormItemOccurrencesListener);
    }
}