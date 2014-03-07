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

        constructor(config: api.form.inputtype.InputTypeViewConfig<ComboBoxConfig>) {
            super("combo-box");
            this.addClass("input-type-view");
            this.config = config;
            this.comboBoxConfig = config.inputConfig;
        }

        availableSizeChanged(newSize: number) {
            console.log("ComboBox.availableSizeChanged("+newSize+")" );
        }

        getElement(): api.dom.Element {
            return this;
        }

        isManagingAdd(): boolean {
            return true;
        }

        onOccurrenceAdded(listener: (event: api.form.OccurrenceAddedEvent)=>void) {
            throw new Error("ComboBox manages occurrences self");
        }

        onOccurrenceRemoved(listener: (event: api.form.OccurrenceRemovedEvent)=>void) {
            throw new Error("ComboBox manages occurrences self");
        }

        unOccurrenceAdded(listener: (event: api.form.OccurrenceAddedEvent)=>void) {
            throw new Error("ComboBox manages occurrences self");
        }

        unOccurrenceRemoved(listener: (event: api.form.OccurrenceRemovedEvent)=>void) {
            throw new Error("ComboBox manages occurrences self");
        }

        public maximumOccurrencesReached(): boolean {
            return this.input.getOccurrences().maximumReached(this.comboBox.countSelectedOptions());
        }

        createAndAddOccurrence() {
            throw new Error("ComboBox manages occurrences self");
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
                rowHeight: 24,
                filter: this.comboboxFilter,
                selectedOptionsView: this.selectedOptionsView,
                maximumOccurrences: input.getOccurrences().getMaximum(),
                hideComboBoxWhenMaxReached: true
            });

            comboBox.onValueChanged((event: api.ui.selector.combobox.ComboBoxValueChangedEvent<string>) => {
                event.getGrid().getDataView().setFilterArgs({searchString: event.getNewValue()});
                event.getGrid().getDataView().refresh();
            });
            comboBox.onOptionSelected(() => {
                this.validate(false);
            });
            comboBox.addSelectedOptionRemovedListener((removed: api.ui.selector.combobox.SelectedOption<string>) => {
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

    api.form.input.InputTypeManager.register("ComboBox", ComboBox);
}