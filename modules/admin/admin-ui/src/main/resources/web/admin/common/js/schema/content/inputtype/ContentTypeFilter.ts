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
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import ApplicationKey = api.application.ApplicationKey;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
    import FocusSwitchEvent = api.ui.FocusSwitchEvent;

    export class ContentTypeFilter extends api.form.inputtype.support.BaseInputTypeManagingAdd<string> {

        private combobox: ContentTypeComboBox;

        private context: ContentInputTypeViewContext;

        private onContentTypesLoadedHandler: (contentTypeArray: ContentTypeSummary[]) => void;

        constructor(context: ContentInputTypeViewContext) {
            super('content-type-filter');
            this.context = context;
            this.onContentTypesLoadedHandler = this.onContentTypesLoaded.bind(this);
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
                comboBox = new ContentTypeComboBox(this.getInput().getOccurrences().getMaximum(), loader);

            comboBox.onLoaded(this.onContentTypesLoadedHandler);

            comboBox.onOptionSelected((event: SelectedOptionEvent<ContentTypeSummary>) => {
                if (event.getKeyCode() === 13) {
                    new FocusSwitchEvent(this).fire();
                }
                this.onContentTypeSelected(event.getSelectedOption());
            });

            comboBox.onOptionDeselected((event: SelectedOptionEvent<ContentTypeSummary>) =>
                this.onContentTypeDeselected(event.getSelectedOption()));

            return comboBox;
        }

        private onContentTypesLoaded(): void {

            this.combobox.getComboBox().setValue(this.getValueFromPropertyArray(this.getPropertyArray()));

            this.setLayoutInProgress(false);
            this.combobox.unLoaded(this.onContentTypesLoadedHandler);

            this.validate(false);
        }

        private onContentTypeSelected(selectedOption: api.ui.selector.combobox.SelectedOption<ContentTypeSummary>): void {
            if (this.isLayoutInProgress()) {
                return;
            }
            this.ignorePropertyChange = true;
            var value = new Value(selectedOption.getOption().displayValue.getContentTypeName().toString(), ValueTypes.STRING);
            if (this.combobox.countSelected() == 1) { // overwrite initial value
                this.getPropertyArray().set(0, value);
            }
            else {
                this.getPropertyArray().add(value);
            }

            this.validate(false);
            this.ignorePropertyChange = false;
        }

        private onContentTypeDeselected(option: SelectedOption<ContentTypeSummary>): void {
            this.ignorePropertyChange = true;
            this.getPropertyArray().remove(option.getIndex());
            this.validate(false);
            this.ignorePropertyChange = false;
        }

        layout(input: Input, propertyArray: PropertyArray): wemQ.Promise<void> {
            if (!ValueTypes.STRING.equals(propertyArray.getType())) {
                propertyArray.convertValues(ValueTypes.STRING);
            }
            super.layout(input, propertyArray);

            this.appendChild(this.combobox = this.createComboBox());

            return wemQ<void>(null);
        }


        update(propertyArray: api.data.PropertyArray, unchangedOnly: boolean): Q.Promise<void> {
            var superPromise = super.update(propertyArray, unchangedOnly);

            if (!unchangedOnly || !this.combobox.isDirty()) {
                return superPromise.then(() => {

                    return this.combobox.getLoader().load().then(this.onContentTypesLoadedHandler);
                });
            } else {
                return superPromise;
            }
        }

        private getValues(): Value[] {
            return this.combobox.getSelectedDisplayValues().map((contentType: ContentTypeSummary) => {
                return new Value(contentType.getContentTypeName().toString(), ValueTypes.STRING);
            });
        }

        protected getNumberOfValids(): number {
            return this.getValues().length;
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