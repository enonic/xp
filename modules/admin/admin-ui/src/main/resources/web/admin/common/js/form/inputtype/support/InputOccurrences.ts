module api.form.inputtype.support {

    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class InputOccurrencesBuilder {

        baseInputTypeView: BaseInputTypeNotManagingAdd<any>;

        input: api.form.Input;

        propertyArray: PropertyArray;

        setBaseInputTypeView(value: BaseInputTypeNotManagingAdd<any>): InputOccurrencesBuilder {
            this.baseInputTypeView = value;
            return this;
        }

        setInput(value: api.form.Input): InputOccurrencesBuilder {
            this.input = value;
            return this;
        }

        setPropertyArray(value: PropertyArray): InputOccurrencesBuilder {
            this.propertyArray = value;
            return this;
        }

        build(): InputOccurrences {
            return new InputOccurrences(this);
        }
    }

    /*
     * A kind of a controller, which add/remove InputOccurrenceView-s to the BaseInputTypeView
     */
    export class InputOccurrences extends api.form.FormItemOccurrences<InputOccurrenceView> {

        private baseInputTypeView: BaseInputTypeNotManagingAdd<any>;

        private input: api.form.Input;

        constructor(config: InputOccurrencesBuilder) {
            super(<FormItemOccurrencesConfig>{
                formItem: config.input,
                propertyArray: config.propertyArray,
                occurrenceViewContainer: config.baseInputTypeView,
                allowedOccurrences: config.input.getOccurrences()
            });
            this.baseInputTypeView = config.baseInputTypeView;
            this.input = config.input;
        }

        hasValidUserInput(): boolean {
            let result = true;
            this.getOccurrenceViews().forEach((formItemOccurrenceView: FormItemOccurrenceView) => {

                if (!formItemOccurrenceView.hasValidUserInput()) {
                    result = false;
                }
            });
            return result;
        }

        moveOccurrence(fromIndex: number, toIndex: number) {

            super.moveOccurrence(fromIndex, toIndex);
        }

        getInput(): api.form.Input {
            return this.input;
        }

        getAllowedOccurrences(): api.form.Occurrences {
            return this.input.getOccurrences();
        }

        protected constructOccurrencesForNoData(): api.form.FormItemOccurrence<InputOccurrenceView>[] {
            let occurrences: api.form.FormItemOccurrence<InputOccurrenceView>[] = [];
            if (this.getAllowedOccurrences().getMinimum() > 0) {

                for (let i = 0; i < this.getAllowedOccurrences().getMinimum(); i++) {
                    occurrences.push(this.createNewOccurrence(this, i));
                }
            } else {
                occurrences.push(this.createNewOccurrence(this, 0));
            }
            return occurrences;
        }

        protected constructOccurrencesForData(): api.form.FormItemOccurrence<InputOccurrenceView>[] {
            let occurrences: api.form.FormItemOccurrence<InputOccurrenceView>[] = [];

            this.propertyArray.forEach((property: Property, index: number) => {
                occurrences.push(this.createNewOccurrence(this, index));
            });

            if (occurrences.length < this.input.getOccurrences().getMinimum()) {
                for (let index: number = occurrences.length; index < this.input.getOccurrences().getMinimum(); index++) {
                    occurrences.push(this.createNewOccurrence(this, index));
                }
            }
            return occurrences;
        }

        createNewOccurrence(formItemOccurrences: api.form.FormItemOccurrences<InputOccurrenceView>,
                            insertAtIndex: number): api.form.FormItemOccurrence<InputOccurrenceView> {
            return new InputOccurrence(<InputOccurrences>formItemOccurrences, insertAtIndex);
        }

        createNewOccurrenceView(occurrence: InputOccurrence): InputOccurrenceView {

            let property = this.getPropertyFromArray(occurrence.getIndex());
            let inputOccurrenceView: InputOccurrenceView = new InputOccurrenceView(occurrence, this.baseInputTypeView, property);

            let inputOccurrences: InputOccurrences = this;
            inputOccurrenceView.onRemoveButtonClicked((event: api.form.RemoveButtonClickedEvent<InputOccurrenceView>) => {
                inputOccurrences.removeOccurrenceView(event.getView());
            });

            return inputOccurrenceView;
        }

        updateOccurrenceView(occurrenceView: InputOccurrenceView, propertyArray: PropertyArray,
                             unchangedOnly?: boolean): wemQ.Promise<void> {
            this.propertyArray = propertyArray;

            return occurrenceView.update(propertyArray, unchangedOnly);
        }

        resetOccurrenceView(occurrenceView: InputOccurrenceView) {
            occurrenceView.reset();
        }

        private getPropertyFromArray(index: number): Property {
            let property = this.propertyArray.get(index);
            if (!property) {
                let newInitialValue = this.baseInputTypeView.newInitialValue();
                api.util.assertNotNull(newInitialValue,
                    "InputTypeView-s extending BaseInputTypeNotManagingAdd must must return a Value from newInitialValue");
                property = this.propertyArray.add(newInitialValue);
            }
            return property;
        }

        giveFocus(): boolean {

            let focusGiven = false;
            let occurrenceViews = this.getOccurrenceViews();
            if (occurrenceViews.length > 0) {
                for (let i = 0; i < occurrenceViews.length; i++) {
                    if (occurrenceViews[i].giveFocus()) {
                        focusGiven = true;
                        break;
                    }
                }
            }
            return focusGiven;
        }

        public static create(): InputOccurrencesBuilder {
            return new InputOccurrencesBuilder();
        }
    }
}
