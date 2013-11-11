module api_form_inputtype {

    export interface InputTypeView {

        getHTMLElement():HTMLElement;

        layout(input:api_form.Input, properties:api_data.Property[]);

        getValues(): api_data.Value[];

        getAttachments(): api_content.Attachment[];

        validate(validationRecorder:api_form.ValidationRecorder);

        /*
         * Whether the InputTypeView it self is managing adding new occurrences or not.
         */
        isManagingAdd():boolean;

        /*
         * Is only invoked if InputTypeView is not managing add.
         */
        createAndAddOccurrence();

        /*
         * Is only invoked if InputTypeView is not managing add.
         */
        maximumOccurrencesReached():boolean;

        /*
         * Is only invoked if InputTypeView is not managing add.
         */
        addFormItemOccurrencesListener(listener:api_form.FormItemOccurrencesListener);

        /*
         * Is only invoked if InputTypeView is not managing add.
         */
        removeFormItemOccurrencesListener(listener:api_form.FormItemOccurrencesListener);
    }
}