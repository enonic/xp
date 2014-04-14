module api.form.inputtype.support {

    export interface InputOccurrencesConfig {

        baseInputTypeView: BaseInputTypeView<any>;

        input: api.form.Input;

        properties: api.data.Property[];

    }

    /*
     * A kind of a controller, which add/remove InputOccurrenceView-s to the BaseInputTypeView
     */
    export class InputOccurrences extends api.form.FormItemOccurrences<InputOccurrenceView> {

        private baseInputTypeView: BaseInputTypeView<any>;

        private input: api.form.Input;

        private properties: api.data.Property[];

        private valueAddedListeners: {(event: api.form.inputtype.ValueAddedEvent) : void}[] = [];

        private valueChangedListeners: {(event: api.form.inputtype.ValueChangedEvent) : void}[] = [];

        private valueRemovedListeners: {(event: api.form.inputtype.ValueRemovedEvent) : void}[] = [];

        constructor(config: InputOccurrencesConfig) {
            super(<FormItemOccurrencesConfig>{
                formItem: config.input,
                occurrenceViewContainer: config.baseInputTypeView,
                allowedOccurrences: config.input.getOccurrences()
            });

            this.baseInputTypeView = config.baseInputTypeView;
            this.input = config.input;
            this.properties = config.properties;

            this.onOccurrenceRemoved((event: api.form.OccurrenceRemovedEvent) => {
                this.handleOccurrenceRemoved(event.getOccurrence().getIndex());
            });

            if (this.properties.length > 0) {
                this.constructOccurrencesForData();
            }
            else {
                this.constructOccurrencesForNoData();
            }
        }

        private handleOccurrenceAdded(value: api.data.Value) {
            if (this.isLayingOut()) {
                return;
            }

            var property = new api.data.Property(this.input.getName(), value);
            this.properties.push(property);

            this.notifyValueAdded(new api.form.inputtype.ValueAddedEvent(value));
        }

        private handleOccurrenceRemoved(index: number) {
            if (this.isLayingOut()) {
                return;
            }

            this.properties.splice(index, 1);

            this.notifyValueRemoved(new api.form.inputtype.ValueRemovedEvent(index));
        }

        private handleOccurrenceChanged(event: api.form.inputtype.ValueChangedEvent) {
            if (this.isLayingOut()) {
                return;
            }

            var property = this.properties[event.getArrayIndex()];
            if (property) {
                property.setValue(event.getNewValue());
            }

            this.notifyValueChanged(event);
        }

        reorderOccurrences(occurrenceIndexes: number[]) {

            var values = this.getValues();

            occurrenceIndexes.forEach((index:number) => {

                var newValue = values[index];
                var event = new api.form.inputtype.ValueChangedEvent(newValue, index);
                this.notifyValueChanged(event);
            });
        }

        getInput(): api.form.Input {
            return this.input;
        }

        getAllowedOccurrences(): api.form.Occurrences {
            return this.input.getOccurrences();
        }

        private constructOccurrencesForData() {
            this.properties.forEach((property: api.data.Property, index: number) => {
                this.addOccurrence(new InputOccurrence(this, index));
            });

            if (this.countOccurrences() < this.input.getOccurrences().getMinimum()) {
                for (var index: number = this.countOccurrences();
                     index < this.input.getOccurrences().getMinimum(); index++) {
                    this.addOccurrence(this.createNewOccurrence(this, index));
                }
            }
        }

        createNewOccurrence(formItemOccurrences: api.form.FormItemOccurrences<InputOccurrenceView>,
                            insertAtIndex: number): api.form.FormItemOccurrence<InputOccurrenceView> {
            return new InputOccurrence(<InputOccurrences>formItemOccurrences, insertAtIndex);
        }

        createNewOccurrenceView(occurrence: InputOccurrence): InputOccurrenceView {

            var property: api.data.Property = this.properties != null ? this.properties[occurrence.getIndex()] : null;
            var inputOccurrenceView: InputOccurrenceView = new InputOccurrenceView(occurrence, this.baseInputTypeView, property);

            var inputOccurrences: InputOccurrences = this;
            inputOccurrenceView.onRemoveButtonClicked((event: api.form.RemoveButtonClickedEvent<InputOccurrenceView>) => {
                inputOccurrences.doRemoveOccurrence(event.getView(), event.getIndex());
            });

            inputOccurrenceView.onValueChanged((event: api.form.inputtype.ValueChangedEvent) => {
                this.handleOccurrenceChanged(event);
            });

            this.handleOccurrenceAdded(new api.data.Value("", api.data.ValueTypes.STRING));

            return inputOccurrenceView;
        }

        getValues(): api.data.Value[] {

            var values: api.data.Value[] = [];
            this.getOccurrenceViews().forEach((occurrenceView: InputOccurrenceView) => {
                var value = this.baseInputTypeView.getValue(occurrenceView.getInputElement());
                if (value != null) {
                    values.push(value);
                }
            });
            return values;
        }

        giveFocus(): boolean {

            var focusGiven = false;
            var occurrenceViews = this.getOccurrenceViews();
            if (occurrenceViews.length > 0) {
                for (var i = 0; i < occurrenceViews.length; i++) {
                    if (occurrenceViews[i].giveFocus()) {
                        focusGiven = true;
                        break;
                    }
                }
            }
            return focusGiven;
        }

        onValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
            this.valueAddedListeners.push(listener);
        }

        unValueAdded(listener: (event: api.form.inputtype.ValueAddedEvent) => void) {
            this.valueAddedListeners.filter((currentListener: (event: api.form.inputtype.ValueAddedEvent)=>void) => {
                return listener == currentListener;
            });
        }

        private notifyValueAdded(event: api.form.inputtype.ValueAddedEvent) {
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

        private notifyValueRemoved(event: api.form.inputtype.ValueRemovedEvent) {
            this.valueRemovedListeners.forEach((listener: (event: api.form.inputtype.ValueRemovedEvent)=>void) => {
                listener(event);
            });
        }

    }
}