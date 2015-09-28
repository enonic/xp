module api.form.inputtype {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;

    export interface InputTypeView<RAW_VALUE_TYPE> {

        getValueType(): ValueType;

        getElement(): api.dom.Element;

        layout(input: api.form.Input, propertyArray: PropertyArray) : wemQ.Promise<void>;

        newInitialValue(): Value;

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

        displayValidationErrors(value: boolean);

        hasValidUserInput() : boolean;

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