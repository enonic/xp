module app_wizard_form_input_type {

    /*
     * A kind of a controller, which add/remove InputOccurrenceView-s to the BaseInputTypeView
     */
    export class InputOccurrences extends app_wizard_form.FormItemOccurrences {

        private baseInputTypeView:BaseInputTypeView;

        private input:api_schema_content_form.Input;

        private properties:api_data.Property[];

        private occurrenceViews:InputOccurrenceView[] = [];

        constructor(baseInputTypeView:BaseInputTypeView, input:api_schema_content_form.Input, properties:api_data.Property[]) {
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

        getInput():api_schema_content_form.Input {
            return this.input;
        }

        private constructOccurrencesForNoData() {
            if (this.input.getOccurrences().getMinimum() > 0) {

                for (var i = 0; i < this.input.getOccurrences().getMinimum(); i++) {
                    this.addOccurrence(new InputOccurrence(this, i));
                }
            }
            else {
                this.addOccurrence(new InputOccurrence(this, 0));
            }
        }

        private constructOccurrencesForData() {
            this.properties.forEach((property:api_data.Property, index:number) => {
                this.addOccurrence(new InputOccurrence(this, index));
            });

            if (this.countOccurrences() < this.input.getOccurrences().getMinimum()) {
                for (var index:number = this.countOccurrences();
                     index < this.input.getOccurrences().getMinimum(); index++) {
                    this.addOccurrence(new InputOccurrence(this, index));
                }
            }
        }

        createNewOccurrence(formItemOccurrences:app_wizard_form.FormItemOccurrences,
                            insertAtIndex:number):app_wizard_form.FormItemOccurrence {
            return new InputOccurrence(<InputOccurrences>formItemOccurrences, insertAtIndex)
        }

        createNewOccurrenceView(occurrence:InputOccurrence):InputOccurrenceView {

            var inputOccurrenceView:InputOccurrenceView = new InputOccurrenceView(occurrence,
                this.baseInputTypeView.createInputOccurrenceElement(occurrence.getIndex()));

            var inputOccurrences:InputOccurrences = this;
            inputOccurrenceView.addListener(<InputOccurrenceViewListener>{
                onRemoveButtonClicked: (toBeRemoved:InputOccurrenceView, index:number) => {
                    inputOccurrences.doRemoveOccurrence(toBeRemoved, index);
                },

                onAddButtonClicked: (fromOccurrence:InputOccurrenceView) => {
                    inputOccurrences.doAddOccurrenceAfter(fromOccurrence);
                }
            });
            return inputOccurrenceView;
        }

        getValues():string[] {

            var values:string[] = [];
            this.occurrenceViews.forEach((inputOccurrenceView:InputOccurrenceView) => {
                values.push(this.baseInputTypeView.getValue(inputOccurrenceView.getInputElement()));
            });
            return values;
        }

    }
}