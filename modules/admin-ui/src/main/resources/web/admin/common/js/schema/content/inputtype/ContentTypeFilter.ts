module api.schema.content.inputtype {

    import ContentInputTypeViewContext = api.content.form.inputtype.ContentInputTypeViewContext;
    import InputValidationRecording = api.form.inputtype.InputValidationRecording;
    import InputValidityChangedEvent = api.form.inputtype.InputValidityChangedEvent;
    import ValueChangedEvent = api.form.inputtype.ValueChangedEvent;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import Input = api.form.Input;
    import ContentTypeComboBox = api.schema.content.ContentTypeComboBox;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import ModuleKey = api.module.ModuleKey;

    export class ContentTypeFilter extends api.form.inputtype.support.BaseInputTypeManagingAdd<string> {

        private input: Input;

        private propertyArray: PropertyArray;

        private combobox: ContentTypeComboBox;

        private validationRecording: InputValidationRecording;

        private layoutInProgress: boolean;

        private context: ContentInputTypeViewContext<any>;

        constructor(context: ContentInputTypeViewContext<any>) {
            super('content-type-filter');
            this.context = context;
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return null;
        }

        private createPageTemplateLoader(): PageTemplateContentTypeLoader {
            var contentId = this.context.site.getContentId(),
                loader = new api.schema.content.PageTemplateContentTypeLoader(contentId);

            loader.setComparator(new api.content.ContentSummaryByDisplayNameComparator());

            return loader;
        }

        private createComboBox(): ContentTypeComboBox {
            var loader = this.context.formContext.getContentTypeName().isPageTemplate() ? this.createPageTemplateLoader() : null,
                comboBox = new ContentTypeComboBox(this.input.getOccurrences().getMaximum(), loader);

            comboBox.onLoaded((contentTypeArray: ContentTypeSummary[]) => this.onContentTypesLoaded(contentTypeArray));
            comboBox.onOptionSelected((event: OptionSelectedEvent<ContentTypeSummary>) => this.onContentTypeSelected(event));
            comboBox.onOptionDeselected((option: SelectedOption<ContentTypeSummary>) => this.onContentTypeDeselected(option));

            return comboBox;
        }

        private onContentTypesLoaded(contentTypeArray: ContentTypeSummary[]): void {
            var contentTypes = [];
            this.propertyArray.forEach((property: Property) => {
                contentTypes.push(property.getString());
            });

            this.combobox.getComboBox().setValues(contentTypes);

            this.layoutInProgress = false;
            this.combobox.unLoaded(this.onContentTypesLoaded);

            this.validate(false);
        }

        private onContentTypeSelected(event: OptionSelectedEvent<ContentTypeSummary>): void {
            if (this.layoutInProgress) {
                return;
            }

            var value = new Value(event.getOption().displayValue.getContentTypeName().toString(), ValueTypes.STRING);
            if (this.combobox.countSelected() == 1) { // overwrite initial value
                this.propertyArray.set(0, value);
            }
            else {
                this.propertyArray.add(value);
            }

            this.validate(false);
        }

        private onContentTypeDeselected(option: SelectedOption<ContentTypeSummary>): void {
            this.propertyArray.remove(option.getIndex());
            this.validate(false);
        }

        layout(input: Input, propertyArray: PropertyArray): wemQ.Promise<void> {
            this.layoutInProgress = true;
            this.input = input;
            this.propertyArray = propertyArray;

            this.appendChild(this.combobox = this.createComboBox());

            return wemQ<void>(null);
        }

        private getValues(): Value[] {
            return this.combobox.getSelectedDisplayValues().map((contentType: ContentTypeSummary) => {
                return new Value(contentType.getContentTypeName().toString(), ValueTypes.STRING);
            });
        }

        validate(silent: boolean = true): InputValidationRecording {
            var recording = new InputValidationRecording();

            if (this.layoutInProgress) {
                this.validationRecording = recording;
                return recording;
            }

            var values = this.getValues(),
                occurrences = this.input.getOccurrences();

            recording.setBreaksMinimumOccurrences(occurrences.minimumBreached(values.length));
            recording.setBreaksMaximumOccurrences(occurrences.maximumBreached(values.length));

            if (!silent && recording.validityChanged(this.validationRecording)) {
                this.notifyValidityChanged(new InputValidityChangedEvent(recording, this.input.getName()));
            }

            this.validationRecording = recording;
            return recording;
        }

        giveFocus(): boolean {
            return this.combobox.maximumOccurrencesReached() ? false : this.combobox.giveFocus();
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.combobox.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.combobox.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.combobox.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.combobox.unBlur(listener);
        }
    }

    api.form.inputtype.InputTypeManager.register(new api.Class("ContentTypeFilter", ContentTypeFilter));

}