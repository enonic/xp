module api.form.inputtype.support {

    /*
     * A kind of a controller, which add/remove InputOccurrenceView-s to the BaseInputTypeView
     */
    export class InputOccurrences extends api.form.FormItemOccurrences<InputOccurrenceView> {

        private baseInputTypeView: BaseInputTypeView;

        private input: api.form.Input;

        private properties: api.data.Property[];

        constructor(baseInputTypeView: BaseInputTypeView, input: api.form.Input, properties: api.data.Property[]) {
            super(input, baseInputTypeView, input.getOccurrences());

            this.baseInputTypeView = baseInputTypeView;
            this.input = input;
            this.properties = properties;

            if (properties != null && properties.length > 0) {
                this.constructOccurrencesForData();
            }
            else {
                this.constructOccurrencesForNoData();
            }
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
            inputOccurrenceView.addListener(<api.form.FormItemOccurrenceViewListener>{
                onRemoveButtonClicked: (toBeRemoved: InputOccurrenceView, index: number) => {
                    inputOccurrences.doRemoveOccurrence(toBeRemoved, index);
                }
            });
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

    }
}