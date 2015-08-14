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

            if (this.propertyArray.getSize() > 0) {
                this.constructOccurrencesForData();
            }
            else {
                this.constructOccurrencesForNoData();
            }
        }

        hasValidUserInput(): boolean {
            var result = true;
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

        private constructOccurrencesForData() {
            this.propertyArray.forEach((property: Property, index: number) => {
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

            var newInitialValue = this.baseInputTypeView.newInitialValue();
            api.util.assertNotNull(newInitialValue,
                "InputTypeView-s extending BaseInputTypeNotManagingAdd must must return a Value from newInitialValue");
            var property = this.propertyArray.get(occurrence.getIndex());
            if (!property) {
                property = this.propertyArray.add(newInitialValue);
            }
            var inputOccurrenceView: InputOccurrenceView = new InputOccurrenceView(occurrence, this.baseInputTypeView, property);

            var inputOccurrences: InputOccurrences = this;
            inputOccurrenceView.onRemoveButtonClicked((event: api.form.RemoveButtonClickedEvent<InputOccurrenceView>) => {
                inputOccurrences.doRemoveOccurrence(event.getView(), event.getIndex());
            });

            return inputOccurrenceView;
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

        public static create(): InputOccurrencesBuilder {
            return new InputOccurrencesBuilder();
        }
    }
}