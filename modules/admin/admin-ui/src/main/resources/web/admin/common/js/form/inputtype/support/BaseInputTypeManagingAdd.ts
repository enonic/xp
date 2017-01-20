module api.form.inputtype.support {

    import PropertyPath = api.data.PropertyPath;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
    import FocusSwitchEvent = api.ui.FocusSwitchEvent;
    import InputTypeView = api.form.inputtype.InputTypeView;

    export class BaseInputTypeManagingAdd<RAW_VALUE_TYPE> extends api.dom.DivEl implements InputTypeView<RAW_VALUE_TYPE> {

        private inputValidityChangedListeners: {(event: api.form.inputtype.InputValidityChangedEvent) : void}[] = [];

        private inputValueChangedListeners: {(event: api.form.inputtype.ValueChangedEvent): void}[] = [];

        private input: api.form.Input;

        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        private layoutInProgress: boolean;

        private propertyArray: PropertyArray;

        private propertyArrayListener: () => void;

        protected ignorePropertyChange: boolean;

        public static debug: boolean = false;

        constructor(className: string) {
            super('input-type-view' + (className ? ' ' + className : ''));

            this.propertyArrayListener = (...args: any[]) => {
                if (!this.ignorePropertyChange) {
                    if (BaseInputTypeManagingAdd.debug) {
                        console.debug('BaseInputTypeManagingAdd: propertyArrayListener', args);
                    }
                    this.update(this.propertyArray, true).done();
                }
            };
        }

        protected fireFocusSwitchEvent(event: SelectedOptionEvent<any>) {
            if (event.getKeyCode() === 13) {
                new FocusSwitchEvent(this).fire();
            }
        }

        protected getValueFromPropertyArray(propertyArray: api.data.PropertyArray): string {
            return propertyArray.getProperties().map((property) => {
                if (property.hasNonNullValue()) {
                    return property.getString();
                }
            }).join(';');
        }

        availableSizeChanged() {
            // must be implemented by children
        }

        getElement(): api.dom.Element {
            return this;
        }

        isManagingAdd(): boolean {
            return true;
        }

        getValueType(): ValueType {
            throw new Error('Must be implemented by inheritor: ' + api.ClassHelper.getClassName(this));
        }

        /**
         * Must be overridden by inheritors.
         */
        newInitialValue(): Value {
            throw new Error('Must be overridden by inheritor: ' + api.ClassHelper.getClassName(this));
        }

        /**
         * Must be resolved by inheritors.
         */
        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {
            if (BaseInputTypeManagingAdd.debug) {
                console.log('BaseInputTypeManagingAdd.layout', input, propertyArray);
            }
            this.input = input;
            this.layoutInProgress = true;

            this.registerPropertyArray(propertyArray);

            return wemQ<void>(null);
        }

        /**
         * Must be resolved by inheritors.
         */
        update(propertyArray: PropertyArray, unchangedOnly?: boolean): wemQ.Promise<void> {
            if (BaseInputTypeManagingAdd.debug) {
                console.log('BaseInputTypeManagingAdd.update', propertyArray, unchangedOnly);
            }
            this.registerPropertyArray(propertyArray);

            return wemQ<void>(null);
        }

        reset() {
            throw Error('Must be implemented in inheritors');
        }

        private registerPropertyArray(propertyArray: PropertyArray) {
            if (this.propertyArray) {
                if (BaseInputTypeManagingAdd.debug) {
                    console.debug('BaseInputTypeManagingAdd.registerPropertyArray: unregister old first ', this.propertyArray);
                }
                this.propertyArray.unPropertyValueChanged(this.propertyArrayListener);
                this.propertyArray.unPropertyAdded(this.propertyArrayListener);
                this.propertyArray.unPropertyRemoved(this.propertyArrayListener);
                this.propertyArray.unPropertyIndexChanged(this.propertyArrayListener);
            }
            if (propertyArray) {
                if (BaseInputTypeManagingAdd.debug) {
                    console.debug('BaseInputTypeManagingAdd.registerPropertyArray: register new one ', propertyArray);
                }
                this.ensureOccurrenceLimits(propertyArray);

                propertyArray.onPropertyValueChanged(this.propertyArrayListener);
                propertyArray.onPropertyAdded(this.propertyArrayListener);
                propertyArray.onPropertyRemoved(this.propertyArrayListener);
                propertyArray.onPropertyIndexChanged(this.propertyArrayListener);
            }
            this.propertyArray = propertyArray;
        }

        private ensureOccurrenceLimits(propertyArray: PropertyArray) {

            let max = this.input.getOccurrences().getMaximum();
            let actual = propertyArray.getSize();

            if (max > 0 && max < actual) {
                if (BaseInputTypeManagingAdd.debug) {
                    console.info(`BaseInputTypeManagingAdd: expected max ${max} occurrences, but found ${actual}, dropping extra`);
                }
                for (let i = actual - 1; i > max - 1; i--) {
                    propertyArray.remove(i);
                }
            }
        }

        hasValidUserInput(): boolean {
            return true;
        }

        displayValidationErrors(value: boolean) {
            // must be implemented by children
        }

        validate(silent: boolean = true,
                 rec: api.form.inputtype.InputValidationRecording = null): api.form.inputtype.InputValidationRecording {

            let recording = rec || new api.form.inputtype.InputValidationRecording();

            if (this.layoutInProgress) {
                this.previousValidationRecording = recording;
                return recording;
            }

            let numberOfValids = this.getNumberOfValids();

            if (this.input.getOccurrences().minimumBreached(numberOfValids)) {
                recording.setBreaksMinimumOccurrences(true);
            }

            if (this.input.getOccurrences().maximumBreached(numberOfValids)) {
                recording.setBreaksMaximumOccurrences(true);
            }

            if (!silent && recording.validityChanged(this.previousValidationRecording)) {
                this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        onValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) {
            this.inputValidityChangedListeners.filter((currentListener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                return listener === currentListener;
            });
        }

        notifyValidityChanged(event: api.form.inputtype.InputValidityChangedEvent) {

            this.inputValidityChangedListeners.forEach((listener: (event: api.form.inputtype.InputValidityChangedEvent)=>void) => {
                listener(event);
            });
        }

        onValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            this.inputValueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: api.form.inputtype.ValueChangedEvent) => void) {
            this.inputValueChangedListeners = this.inputValueChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        protected notifyValueChanged(event: api.form.inputtype.ValueChangedEvent) {
            this.inputValueChangedListeners.forEach((listener: (event: api.form.inputtype.ValueChangedEvent)=>void) => {
                listener(event);
            });
        }

        /**
         * Must be overridden by inheritors.
         */
        giveFocus(): boolean {
            throw new Error('Must be overridden by inheritor: ' + api.ClassHelper.getClassName(this));
        }

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseInputTypeView
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            // Have to use stub here because it doesn't extend BaseInputTypeView
        }

        protected getInput(): api.form.Input {
            return this.input;
        }

        protected getNumberOfValids(): number {
            throw new Error('Must be overridden by inheritor: ' + api.ClassHelper.getClassName(this));
        }

        protected isLayoutInProgress(): boolean {
            return this.layoutInProgress;
        }

        protected setLayoutInProgress(layoutInProgress: boolean) {
            this.layoutInProgress = layoutInProgress;
        }

        protected getPropertyArray(): PropertyArray {
            return this.propertyArray;
        }
    }
}
