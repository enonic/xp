module api_form {

    export class FormItemOccurrences {

        private occurrences:FormItemOccurrence[] = [];

        private occurrenceViews:FormItemOccurrenceView[] = [];

        private occurrenceViewContainer:api_dom.Element;

        private formItem:FormItem;

        private allowedOccurrences:Occurrences;

        private listeners:FormItemOccurrencesListener[] = [];

        constructor(formItem:FormItem, occurrenceViewContainer:api_dom.Element,
                    allowedOccurrences?:Occurrences) {
            this.formItem = formItem;
            this.occurrenceViewContainer = occurrenceViewContainer;
            this.allowedOccurrences = allowedOccurrences;
        }

        getAllowedOccurrences():Occurrences {
            throw new Error("Must be implemented by inheritor");
        }

        addListener(listener:FormItemOccurrencesListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:FormItemOccurrencesListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyOccurrenceAdded(occurrence:FormItemOccurrence) {
            this.listeners.forEach((listener:FormItemOccurrencesListener) => {
                listener.onOccurrenceAdded(occurrence);
            });
        }

        private notifyOccurrenceRemoved(occurrence:FormItemOccurrence) {
            this.listeners.forEach((listener:FormItemOccurrencesListener) => {
                listener.onOccurrenceRemoved(occurrence);
            });
        }

        constructOccurrencesForNoData() {
            if (this.getAllowedOccurrences().getMinimum() > 0) {

                for (var i = 0; i < this.getAllowedOccurrences().getMinimum(); i++) {
                    this.addOccurrence(this.createNewOccurrence(this, i));
                }
            }
            else {
                this.addOccurrence(this.createNewOccurrence(this, 0));
            }
        }

        addOccurrence(occurrence:FormItemOccurrence) {
            this.occurrences.push(occurrence);
        }

        getFormItem():FormItem {
            return this.formItem;
        }

        maximumOccurrencesReached():boolean {
            return this.allowedOccurrences.maximumReached(this.countOccurrences());
        }

        layout() {
            this.occurrences.forEach((occurrence:FormItemOccurrence) => {
                var occurrenceView:FormItemOccurrenceView = this.createNewOccurrenceView(occurrence);
                this.occurrenceViews.push(occurrenceView);
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
            this.occurrenceViews = this.occurrenceViews.filter((curr:FormItemOccurrenceView) => {
                return curr != occurrenceViewToRemove;
            });
            var occurrenceToRemove = this.occurrences[indexToRemove];
            this.occurrences = this.occurrences.filter((curr:FormItemOccurrence) => {
                return curr.getIndex() != indexToRemove;
            });

            this.resetOccurrenceIndexes();
            this.refreshOccurrenceViews();
            this.notifyOccurrenceRemoved(occurrenceToRemove);
        }

        createAndAddOccurrence() {

            var insertAtIndex:number = this.countOccurrences();
            var occurrence:FormItemOccurrence = this.createNewOccurrence(this, insertAtIndex);

            this.doAddOccurrence(occurrence);
        }

        addOccurrenceAfter(fromOccurrence:FormItemOccurrenceView) {

            var insertAtIndex:number = fromOccurrence.getIndex() + 1;
            var occurrence:FormItemOccurrence = this.createNewOccurrence(this, insertAtIndex);

            this.doAddOccurrence(occurrence);
        }

        private doAddOccurrence(occurrence:FormItemOccurrence) {

            if (this.allowedOccurrences.maximumReached(this.countOccurrences())) {
                return;
            }
            var occurrenceView:FormItemOccurrenceView = this.createNewOccurrenceView(occurrence);
            var insertAtIndex = occurrence.getIndex();
            this.occurrences.splice(insertAtIndex, 0, occurrence);

            var occurrenceViewBefore:api_dom.Element = this.getOccurrenceViewElementBefore(insertAtIndex);
            if (occurrenceViewBefore != null) {
                occurrenceView.insertAfterEl(occurrenceViewBefore);
            }
            else {
                this.occurrenceViewContainer.appendChild(occurrenceView);
            }

            this.occurrenceViews.splice(insertAtIndex, 0, occurrenceView);

            this.resetOccurrenceIndexes();
            this.refreshOccurrenceViews();
            this.notifyOccurrenceAdded(occurrence);
        }

        resetOccurrenceIndexes() {
            this.occurrences.forEach((currOccurrence:FormItemOccurrence, index:number) => {
                currOccurrence.setIndex(index);
            });
        }

        refreshOccurrenceViews() {
            this.occurrenceViews.forEach((currOccurrenceView:FormItemOccurrenceView) => {
                currOccurrenceView.refresh();
            });
        }

        getOccurrenceViewElementBefore(index:number):FormItemOccurrenceView {
            if (index < 1) {
                return null;
            }
            return this.occurrenceViews[index - 1];
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

        getOccurrenceViews():FormItemOccurrenceView[] {
            return this.occurrenceViews;
        }
    }
}