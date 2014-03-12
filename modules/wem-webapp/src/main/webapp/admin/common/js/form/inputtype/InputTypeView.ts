module api.form.inputtype {

    export interface InputTypeView {

        getElement(): api.dom.Element;

        layout(input: api.form.Input, properties: api.data.Property[]);

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
        onOccurrenceAdded(listener: (event: api.form.OccurrenceAddedEvent)=>void);

        /*
         * Is only invoked if InputTypeView is not managing add.
         */
        onOccurrenceRemoved(listener: (event: api.form.OccurrenceRemovedEvent)=>void);

        /*
         * Is only invoked if InputTypeView is not managing add.
         */
        unOccurrenceAdded(listener: (event: api.form.OccurrenceAddedEvent)=>void);

        /*
         * Is only invoked if InputTypeView is not managing add.
         */
        unOccurrenceRemoved(listener: (event: api.form.OccurrenceRemovedEvent)=>void)

        /*
         * Invoked when input wants to edit embedded content
         */
        addEditContentRequestListener(listener: (content: api.content.ContentSummary) => void);

        /*
         * Invoked when input wants to edit embedded content
         */
        removeEditContentRequestListener(listener: (content: api.content.ContentSummary) => void);

        /*
         * Returns true if focus was successfully given.
         */
        giveFocus(): boolean;

        validate(silent: boolean) : InputValidationRecording;

        onValidityChanged(listener: (event: InputValidityChangedEvent)=>void);

        unValidityChanged(listener: (event: InputValidityChangedEvent)=>void);

        availableSizeChanged(newWidth:number, newHeight:number);

    }
}