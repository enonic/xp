module app_wizard_form {

    export class FormItemOccurrences {

        private occurrences:FormItemOccurrence[] = [];

        private occurrenceViewElements:FormItemOccurrenceView[] = [];

        private occurrenceViewContainer:api_dom.Element;

        private formItem:api_schema_content_form.FormItem;

        private allowedOccurrences:api_schema_content_form.Occurrences;

        constructor(formItem:api_schema_content_form.FormItem, occurrenceViewContainer:api_dom.Element,
                    allowedOccurrences?:api_schema_content_form.Occurrences) {
            this.formItem = formItem;
            this.occurrenceViewContainer = occurrenceViewContainer;
            this.allowedOccurrences = allowedOccurrences;
        }

        addOccurrence(occurrence:FormItemOccurrence) {
            this.occurrences.push(occurrence);
        }

        getFormItem():api_schema_content_form.FormItem {
            return this.formItem;
        }

        layout() {
            this.occurrences.forEach((occurrence:FormItemOccurrence) => {
                var occurrenceView:FormItemOccurrenceView = this.createNewOccurrenceView(occurrence);
                this.occurrenceViewElements.push(occurrenceView);
                this.occurrenceViewContainer.appendChild(occurrenceView);
            });
        }

        createNewOccurrenceView(occurrence:FormItemOccurrence):FormItemOccurrenceView {
            throw new Error("Must be implemented by inheritor");
        }

        createNewOccurrence(formItemOccurrences:FormItemOccurrences, insertAtIndex:number):FormItemOccurrence {
            throw new Error("Must be implemented by inheritor");
        }

        doRemoveOccurrence(occurrenceViewToRemove:FormItemOccurrenceView, indexToRemove:number) {

            if (!this.canRemove()) {
                return;
            }

            occurrenceViewToRemove.remove();
            this.occurrenceViewElements = this.occurrenceViewElements.filter((curr:FormItemOccurrenceView) => {
                return curr != occurrenceViewToRemove;
            });
            this.occurrences = this.occurrences.filter((curr:FormItemOccurrence) => {
                return curr.getIndex() != indexToRemove;
            });

            this.resetOccurrenceIndexes();
            this.refreshOccurrenceViews();
        }

        doAddOccurrenceAfter(fromOccurrence:FormItemOccurrenceView) {

            var insertAtIndex:number = fromOccurrence.getIndex() + 1;
            var newInputOccurrence:FormItemOccurrence = this.createNewOccurrence(this, insertAtIndex);
            var newInputOccurrenceView:FormItemOccurrenceView = this.createNewOccurrenceView(newInputOccurrence);

            this.occurrences.splice(insertAtIndex, 0, newInputOccurrence);

            var occurrenceViewBefore:api_dom.Element = this.getOccurrenceViewElementBefore(insertAtIndex);
            if (occurrenceViewBefore != null) {
                newInputOccurrenceView.insertAfterEl(occurrenceViewBefore);
            }
            else {
                this.occurrenceViewContainer.appendChild(newInputOccurrenceView);
            }

            this.occurrenceViewElements.splice(insertAtIndex, 0, newInputOccurrenceView);

            this.resetOccurrenceIndexes();
            this.refreshOccurrenceViews();
        }

        resetOccurrenceIndexes() {
            this.occurrences.forEach((currOccurrence:FormItemOccurrence, index:number) => {
                currOccurrence.setIndex(index);
            });
        }

        refreshOccurrenceViews() {
            this.occurrenceViewElements.forEach((currOccurrenceView:FormItemOccurrenceView) => {
                currOccurrenceView.refresh();
            });
        }

        getOccurrenceViewElementBefore(index:number):FormItemOccurrenceView {
            if (index < 1) {
                return null;
            }
            return this.occurrenceViewElements[index - 1];
        }

        countOccurrences():number {
            return this.occurrences.length;
        }

        getOccurrences():FormItemOccurrence[] {
            return this.occurrences;
        }

        canRemove() {
            return this.occurrences.length > Math.max(1, this.allowedOccurrences.getMinimum());
        }
    }
}