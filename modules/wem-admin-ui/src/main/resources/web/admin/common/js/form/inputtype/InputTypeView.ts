module api.form.inputtype {

    export interface InputTypeView {

        getValueType(): api.data.type.ValueType;

        getElement(): api.dom.Element;

        layout(input: api.form.Input, properties: api.data.Property[]);

        newInitialValue(): any;

        getValues(): api.data.Value[];

        getAttachments(): api.content.attachment.Attachment[];

        /*
         * Whether the InputTypeView it self is managing adding new occurrences or not.
         * If false, then this is expected to implement interface InputTypeViewNotManagingOccurrences.
         */
        isManagingAdd():boolean;

        /*
         * Invoked when input wants to edit embedded content
         */
        onEditContentRequest(listener: (content: api.content.ContentSummary) => void);

        /*
         * Invoked when input wants to edit embedded content
         */
        unEditContentRequest(listener: (content: api.content.ContentSummary) => void);

        /*
         * Returns true if focus was successfully given.
         */
        giveFocus(): boolean;

        /**
         * Note: Event must never be thrown while function layout is being executed.
         */
        onValueAdded(listener: (event: ValueAddedEvent) => void);

        unValueAdded(listener: (event: ValueAddedEvent) => void);

        onValueChanged(listener: (event: ValueChangedEvent) => void);

        unValueChanged(listener: (event: ValueChangedEvent) => void);

        onValueRemoved(listener: (event: ValueRemovedEvent) => void);

        unValueRemoved(listener: (event: ValueRemovedEvent) => void);

        validate(silent: boolean) : InputValidationRecording;

        onValidityChanged(listener: (event: InputValidityChangedEvent)=>void);

        unValidityChanged(listener: (event: InputValidityChangedEvent)=>void);

        availableSizeChanged();

        onFocus(listener: (event: FocusEvent) => void);

        unFocus(listener: (event: FocusEvent) => void);

        onBlur(listener: (event: FocusEvent) => void);

        unBlur(listener: (event: FocusEvent) => void);

    }
}