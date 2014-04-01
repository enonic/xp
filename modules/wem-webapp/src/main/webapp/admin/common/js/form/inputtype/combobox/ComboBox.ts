module api.form.inputtype.combobox {

    export interface ComboBoxConfig {
        options: ComboBoxOption[]
    }

    export class ComboBox extends api.dom.DivEl implements api.form.inputtype.InputTypeView {

        private config: api.form.inputtype.InputTypeViewConfig<ComboBoxConfig>;

        private comboBoxConfig: ComboBoxConfig;

        private input: api.form.Input;

        private comboBox: api.ui.selector.combobox.ComboBox<string>;

        private selectedOptionsView: api.ui.selector.combobox.SelectedOptionsView<string>;

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        private valueAddedListeners: {(event: api.form.inputtype.ValueAddedEvent) : void}[] = [];

        private valueChangedListeners: {(event: api.form.inputtype.ValueChangedEvent) : void}[] = [];

        private valueRemovedListeners: {(event: api.form.inputtype.ValueRemovedEvent) : void}[] = [];

        constructor(config: api.form.inputtype.InputTypeViewConfig<ComboBoxConfig>) {
            super("combo-box");
            this.addClass("input-type-view");
            this.config = config;
            this.comboBoxConfig = config.inputConfig;
        }

        availableSizeChanged(newWidth: number, newHeight: number) {
            console.log("ComboBox.availableSizeChanged(" + newWidth + "x" + newHeight + ")");
        }

        getElement(): api.dom.Element {
            return this;
        }

        isManagingAdd(): boolean {
            return true;
        }

        newInitialValue(): api.data.Value {
            return null;
        }

        layout(input: api.form.Input, properties: api.data.Property[]) {

            this.input = input;

            this.selectedOptionsView = new api.ui.selector.combobox.SelectedOptionsView<string>();
            this.comboBox = this.createComboBox(input);

            this.comboBoxConfig.options.forEach((option: ComboBoxOption) => {
                this.comboBox.addOption({value: option.value, displayValue: option.label})
            });

            if (properties != null) {
                var valueArray: string[] = [];
                properties.forEach((property: api.data.Property) => {
                    valueArray.push(property.getString());
                });
                this.comboBox.setValues(valueArray);
            }

            this.appendChild(this.comboBox);
            this.appendChild(this.selectedOptionsView);
        }

        createComboBox(input: api.form.Input): api.ui.selector.combobox.ComboBox<string> {
            var comboBox = new api.ui.selector.combobox.ComboBox<string>(name, {
                filter: this.comboboxFilter,
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum(),
                optionDisplayValueViewer: new ComboBoxDisplayValueViewer(),
                hideComboBoxWhenMaxReached: true
            });

            comboBox.onOptionFilterInputValueChanged((event: api.ui.selector.OptionFilterInputValueChangedEvent<string>) => {
                this.comboBox.setFilterArgs({searchString: event.getNewValue()});
            });
            comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<string>) => {

                var value = new api.data.Value(event.getOption().displayValue, api.data.ValueTypes.STRING);
                this.notifyValueAdded(value);

                this.validate(false);
            });
            comboBox.addSelectedOptionRemovedListener((removed: api.ui.selector.combobox.SelectedOption<string>) => {

                this.notifyValueRemoved(removed.getIndex());

                this.validate(false);
            });

            return comboBox;
        }

        getValues(): api.data.Value[] {

            var values: api.data.Value[] = [];
            this.comboBox.getSelectedOptions().forEach((option: api.ui.selector.Option<string>)  => {
                var value = new api.data.Value(option.value, api.data.ValueTypes.STRING);
                values.push(value);
            });
            return values;
        }

        getAttachments(): api.content.attachment.Attachment[] {
            return [];
        }

        giveFocus(): boolean {
            if (this.comboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.comboBox.giveFocus();
        }

        valueBreaksRequiredContract(value: api.data.Value): boolean {
            if (api.util.isStringBlank(value.asString())) {
                return true;
            } else {
                return false;
            }
        }

        addEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseIntputTypeView
        }

        removeEditContentRequestListener(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseIntputTypeView
        }

        private comboboxFilter(item: api.ui.selector.Option<string>, args) {
            return !(args && args.searchString && item.displayValue.toUpperCase().indexOf(args.searchString.toUpperCase()) == -1);
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {

            var recording = new api.form.inputtype.InputValidationRecording();

            var numberOfValids = this.comboBox.countSelectedOptions();
            if (numberOfValids < this.input.getOccurrences().getMinimum()) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (this.input.getOccurrences().maximumBreached(numberOfValids)) {
                recording.setBreaksMaximumOccurrences(true);
            }

            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
                }
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        onValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
            this.valueAddedListeners.push(listener);
        }

        unValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
            this.valueAddedListeners.filter((currentListener: (event: api.form.inputtype.ValueAddedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValueAdded(value: api.data.Value) {
            var event = new api.form.inputtype.ValueAddedEvent(value);
            this.valueAddedListeners.forEach((listener: (event: api.form.inputtype.ValueAddedEvent)=>void) => {
                listener(event);
            });
        }

        onValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            this.valueChangedListeners.filter((currentListener: (event: api.form.inputtype.ValueChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValueChanged(event: api.form.inputtype.ValueChangedEvent) {
            this.valueChangedListeners.forEach((listener: (event: api.form.inputtype.ValueChangedEvent)=>void) => {
                listener(event);
            });
        }

        onValueRemoved(listener: (event: api.form.inputtype.ValueRemovedEvent) => void) {
            this.valueRemovedListeners.push(listener);
        }

        unValueRemoved(listener: (event: api.form.inputtype.ValueRemovedEvent) => void) {
            this.valueRemovedListeners.filter((currentListener: (event: api.form.inputtype.ValueRemovedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValueRemoved(index: number) {
            var event = new api.form.inputtype.ValueRemovedEvent(index);
            this.valueRemovedListeners.forEach((listener: (event: api.form.inputtype.ValueRemovedEvent)=>void) => {
                listener(event);
            });
        }

        onValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.filter((currentListener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValidityChanged(event: api.form.inputtype.InputValidityChangedEvent) {
            this.inputValidityChangedListeners.forEach((listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                listener(event);
            });
        }

    }

    api.form.inputtype.InputTypeManager.register("ComboBox", ComboBox);
}