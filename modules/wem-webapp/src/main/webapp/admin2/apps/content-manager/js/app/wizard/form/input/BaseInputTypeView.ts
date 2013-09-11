module app_wizard_form_input {

    export class BaseInputTypeView extends api_dom.DivEl implements InputTypeView {

        private input:api_schema_content_form.Input;

        private occurrences:InputOccurrenceView[];

        constructor(idPrefix:string) {
            super(idPrefix, "input-type-view");
        }

        getInput():api_schema_content_form.Input {
            return this.input;
        }

        getHTMLElement():HTMLElement {
            return super.getHTMLElement();
        }

        layout(input:api_schema_content_form.Input, properties?:api_data.Property[]) {

            this.input = input;
            this.occurrences = [];

            if (properties != null && properties.length > 0) {
                this.doLayoutWithData(properties);
            }
            else {
                this.doLayoutWithoutData();
            }
        }

        private doLayoutWithoutData() {

            var firstOccurrence = this.createInputOccurrence(0);
            this.doAddOccurrenceView(new InputOccurrenceView(this.input, firstOccurrence, 0));

            if (this.input.getOccurrences().getMinimum() > 0) {

                for (var i = 1; i < this.input.getOccurrences().getMinimum(); i++) {

                    var nextOccorrence = this.createInputOccurrence(i);
                    this.doAddOccurrenceView(new InputOccurrenceView(this.input, nextOccorrence, i));
                }
            }
        }

        private doLayoutWithData(properties:api_data.Property[]) {
            properties.forEach((property:api_data.Property, index:number) => {
                var occurrence = this.createInputOccurrence(index, property);
                this.doAddOccurrenceView(new InputOccurrenceView(this.input, occurrence, index));
            });
        }

        private doAddOccurrenceView(toBeAdded:InputOccurrenceView) {

            this.appendChild(toBeAdded);
            var baseInputTypeView:BaseInputTypeView = this;

            toBeAdded.addListener(<InputOccurrenceViewListener>{
                onRemoveButtonClicked: (toBeRemoved:InputOccurrenceView) => {
                    baseInputTypeView.doRemoveOccurrence(toBeRemoved);
                },

                onAddButtonClicked: (fromOccurrence:InputOccurrenceView) => {
                    baseInputTypeView.doAddOccurrence(fromOccurrence);
                }
            });
            this.occurrences.push(toBeAdded);
        }

        private doRemoveOccurrence(occurrenceToRemove:InputOccurrenceView) {
            if (this.occurrences.length > Math.max(1, this.input.getOccurrences().getMinimum())) {
                occurrenceToRemove.remove();
                this.occurrences = this.occurrences.filter(function (curr) {
                    return curr != occurrenceToRemove;
                });
                this.occurrences.forEach((currOccurrenceView:InputOccurrenceView, index:number) => {
                    currOccurrenceView.setIndex(index);

                    // Hide remove button when one and only occurrence
                    if (index == 0 && this.occurrences.length == 1) {
                        currOccurrenceView.showRemoveButton(false);
                    }
                    // Hide all remove buttons when having only minimum occurrences or less
                    else if (this.occurrences.length <= this.input.getOccurrences().getMinimum()) {
                        currOccurrenceView.showRemoveButton(false);
                    }

                });
            }
        }

        private doAddOccurrence(fromOccurrence:InputOccurrenceView) {

            var fromIndex:number = fromOccurrence.getIndex();
            var insertNewAtIndex = fromIndex + 1;
            var newOccurrenceView = new InputOccurrenceView(this.input, this.createInputOccurrence(insertNewAtIndex), insertNewAtIndex);

            this.doInsertAt(insertNewAtIndex, newOccurrenceView);

            var baseInputTypeView:BaseInputTypeView = this;
            newOccurrenceView.addListener(<InputOccurrenceViewListener>{
                onRemoveButtonClicked: (toBeRemoved:InputOccurrenceView) => {
                    baseInputTypeView.doRemoveOccurrence(toBeRemoved);
                },

                onAddButtonClicked: (fromOccurrence:InputOccurrenceView) => {
                    baseInputTypeView.doAddOccurrence(fromOccurrence);
                }
            });

            if (this.occurrences.length > this.input.getOccurrences().getMinimum()) {
                this.occurrences.forEach((currOccurrence:InputOccurrenceView, index:number) => {
                    currOccurrence.showRemoveButton(true);
                });
            }
        }

        private doInsertAt(index:number, occurrenceView:InputOccurrenceView) {

            var occurrenceBefore:InputOccurrenceView = index > 0 ? this.occurrences[index - 1] : null;
            var insertLast:boolean = index == this.occurrences.length;

            if (!insertLast) {
                // copy last to be the "new last"
                this.occurrences.push(this.occurrences[this.occurrences.length - 1]);
                this.occurrences[this.occurrences.length - 1].setIndex(this.occurrences.length - 1);

                // move those after index "one down"
                if (this.occurrences.length - 2 > index) {
                    for (var i = this.occurrences.length - 2; i >= index; i--) {
                        this.occurrences[i] = this.occurrences[i - 1];
                        this.occurrences[i].setIndex(i);
                    }
                }

                this.occurrences[index] = occurrenceView;
                occurrenceView.insertAfterEl(occurrenceBefore);
            }
            else {
                this.occurrences.push(occurrenceView);
                occurrenceView.insertAfterEl(occurrenceBefore);
            }
        }


        createInputOccurrence(index:number, property?:api_data.Property):api_dom.Element {
            throw new Error("Must be implemented by inheritor");
        }

        getValue(occurrence:api_dom.Element):string {
            throw new Error("Must be implemented by inheritor");
        }

        getValues():string[] {

            var values:string[] = [];
            this.occurrences.forEach((inputOccurrenceView:InputOccurrenceView) => {
                values.push(this.getValue(inputOccurrenceView.getInputElement()));
            });
            return values;
        }
    }
}