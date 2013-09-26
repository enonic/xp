module app_wizard_form_input_type {

    /*
    * A kind of a controller, which add/remove InputOccurrenceView-s to the BaseInputTypeView
    */
    export class InputOccurrences {

        private baseInputTypeView:BaseInputTypeView;

        private input:api_schema_content_form.Input;

        private properties:api_data.Property[];

        private occurrences:InputOccurrence[] = [];

        private occurrenceViews:InputOccurrenceView[] = [];

        constructor(baseInputTypeView:BaseInputTypeView, input:api_schema_content_form.Input, properties:api_data.Property[]) {
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
                    this.occurrences.push(new InputOccurrence(this, i));
                }
            }
            else {
                this.occurrences.push(new InputOccurrence(this, 0));
            }
        }

        private constructOccurrencesForData() {
            this.properties.forEach((property:api_data.Property, index:number) => {
                this.occurrences.push(new InputOccurrence(this, index));
            });

            if (this.occurrences.length < this.input.getOccurrences().getMinimum()) {
                for (var index:number = this.occurrences.length;
                     index < this.input.getOccurrences().getMinimum(); index++) {
                    this.occurrences.push(new InputOccurrence(this, index));
                }
            }
        }

        layout() {
            this.occurrences.forEach((inputOccurrence:InputOccurrence) => {
                var inputOccurrenceView:InputOccurrenceView = this.createNewOccurrenceView(inputOccurrence);
                this.occurrenceViews.push(inputOccurrenceView);
                this.baseInputTypeView.appendChild(inputOccurrenceView);
            });
        }

        private createNewOccurrenceView(occurrence:InputOccurrence):InputOccurrenceView {

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

        private doRemoveOccurrence(occurrenceViewToRemove:InputOccurrenceView, indexToRemove:number) {

            if (!this.canRemove()) {
                return;
            }

            occurrenceViewToRemove.remove();
            this.occurrenceViews = this.occurrenceViews.filter((curr:InputOccurrenceView) => {
                return curr != occurrenceViewToRemove;
            });
            this.occurrences = this.occurrences.filter((curr:InputOccurrence) => {
                return curr.getIndex() != indexToRemove;
            });

            this.resetOccurrenceIndexes();
            this.refreshOccurrenceViews();
        }

        private doAddOccurrenceAfter(fromOccurrence:InputOccurrenceView) {

            var insertAtIndex:number = fromOccurrence.getIndex() + 1;
            var newInputOccurrence:InputOccurrence = new InputOccurrence(this, insertAtIndex);
            var newInputOccurrenceView:InputOccurrenceView = this.createNewOccurrenceView(newInputOccurrence);

            this.occurrences.splice(insertAtIndex, 0, newInputOccurrence);

            var occurrenceViewBefore:InputOccurrenceView = this.getInputOccurrenceViewBefore(insertAtIndex);
            if (occurrenceViewBefore != null) {
                newInputOccurrenceView.insertAfterEl(occurrenceViewBefore);
            }
            else {
                this.baseInputTypeView.appendChild(newInputOccurrenceView);
            }

            this.occurrenceViews.splice(insertAtIndex, 0, newInputOccurrenceView);

            this.resetOccurrenceIndexes();
            this.refreshOccurrenceViews();
        }

        private resetOccurrenceIndexes() {
            this.occurrences.forEach((currOccurrence:InputOccurrence, index:number) => {
                currOccurrence.setIndex(index);
            });
        }

        private refreshOccurrenceViews() {
            this.occurrenceViews.forEach((currOccurrenceView:InputOccurrenceView) => {
                currOccurrenceView.refresh();
            });
        }

        private getInputOccurrenceViewBefore(index:number) {
            if (index < 1) {
                return null;
            }
            return this.occurrenceViews[index - 1];
        }

        countOccurrences():number {
            return this.occurrences.length;
        }

        getOccurrences():InputOccurrence[] {
            return this.occurrences;
        }

        canRemove() {
            return this.occurrences.length > Math.max(1, this.input.getOccurrences().getMinimum());
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