module api.form.inputtype {

    import ValidityChangedEvent = api.form.inputtype.support.ValidityChangedEvent;

    export interface InputTypeView {

        getHTMLElement():HTMLElement;

        layout(input:api.form.Input, properties:api.data.Property[]);

        getValues(): api.data.Value[];

        getAttachments(): api.content.attachment.Attachment[];

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
        addFormItemOccurrencesListener(listener:api.form.FormItemOccurrencesListener);

        /*
         * Is only invoked if InputTypeView is not managing add.
         */
        removeFormItemOccurrencesListener(listener:api.form.FormItemOccurrencesListener);

        /*
         * Invoked when input wants to edit embedded content
         */
        addEditContentRequestListener(listener:(content:api.content.ContentSummary) => void);

        /*
         * Invoked when input wants to edit embedded content
         */
        removeEditContentRequestListener(listener: (content:api.content.ContentSummary) => void);

        /*
         * Returns true if focus was successfully given.
         */
        giveFocus(): boolean;

        validate(silent:boolean) : api.form.ValidationRecording ;

        onValidityChanged(listener:(event:ValidityChangedEvent)=>void);

        unValidityChanged(listener:(event:ValidityChangedEvent)=>void);
    }
}