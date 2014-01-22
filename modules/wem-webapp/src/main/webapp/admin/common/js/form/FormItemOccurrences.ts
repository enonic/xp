module api.form {

    export class FormItemOccurrences<V extends FormItemOccurrenceView> {

        private occurrences: FormItemOccurrence<V>[] = [];

        private occurrenceViews: V[] = [];

        private occurrenceViewContainer: api.dom.Element;

        private formItem: FormItem;

        private allowedOccurrences: Occurrences;

        private listeners: FormItemOccurrencesListener[] = [];

        constructor(formItem: FormItem, occurrenceViewContainer: api.dom.Element, allowedOccurrences?: Occurrences) {
            this.formItem = formItem;
            this.occurrenceViewContainer = occurrenceViewContainer;
            this.allowedOccurrences = allowedOccurrences;
        }

        getAllowedOccurrences(): Occurrences {
            throw new Error("Must be implemented by inheritor");
        }

        addListener(listener: FormItemOccurrencesListener) {
            this.listeners.push(listener);
        }

        removeListener(listener: FormItemOccurrencesListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyOccurrenceAdded(occurrence: FormItemOccurrence<V>) {
            this.listeners.forEach((listener: FormItemOccurrencesListener) => {
                listener.onOccurrenceAdded(occurrence);
            });
        }

        private notifyOccurrenceRemoved(occurrence: FormItemOccurrence<V>) {
            this.listeners.forEach((listener: FormItemOccurrencesListener) => {
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

        addOccurrence(occurrence: FormItemOccurrence<V>) {
            this.occurrences.push(occurrence);
        }

        getFormItem(): FormItem {
            return this.formItem;
        }

        maximumOccurrencesReached(): boolean {
            return this.allowedOccurrences.maximumReached(this.countOccurrences());
        }

        layout() {
            this.occurrences.forEach((occurrence: FormItemOccurrence<V>) => {
                var occurrenceView: V = this.createNewOccurrenceView(occurrence);
                this.occurrenceViews.push(occurrenceView);
                this.occurrenceViewContainer.appendChild(occurrenceView);
            });
        }

        createNewOccurrenceView(occurrence: FormItemOccurrence<V>): V {
            throw new Error("Must be implemented by inheritor");
        }

        createNewOccurrence(formItemOccurrences: FormItemOccurrences<V>, insertAtIndex: number): FormItemOccurrence<V> {
            throw new Error("Must be implemented by inheritor");
        }

        doRemoveOccurrence(occurrenceViewToRemove: V, indexToRemove: number) {

            if (!this.canRemove()) {
                return;
            }

            occurrenceViewToRemove.remove();
            this.occurrenceViews = this.occurrenceViews.filter((curr: V) => {
                return curr != occurrenceViewToRemove;
            });
            var occurrenceToRemove = this.occurrences[indexToRemove];
            this.occurrences = this.occurrences.filter((curr: FormItemOccurrence<V>) => {
                return curr.getIndex() != indexToRemove;
            });

            this.resetOccurrenceIndexes();
            this.refreshOccurrenceViews();
            this.notifyOccurrenceRemoved(occurrenceToRemove);
        }

        createAndAddOccurrence() {

            var insertAtIndex: number = this.countOccurrences();
            var occurrence: FormItemOccurrence<V> = this.createNewOccurrence(this, insertAtIndex);

            this.doAddOccurrence(occurrence);
        }

        addOccurrenceAfter(fromOccurrence: V) {

            var insertAtIndex: number = fromOccurrence.getIndex() + 1;
            var occurrence: FormItemOccurrence<V> = this.createNewOccurrence(this, insertAtIndex);

            this.doAddOccurrence(occurrence);
        }

        private doAddOccurrence(occurrence: FormItemOccurrence<V>) {

            if (this.allowedOccurrences.maximumReached(this.countOccurrences())) {
                return;
            }
            var occurrenceView: V = this.createNewOccurrenceView(occurrence);
            var insertAtIndex = occurrence.getIndex();
            this.occurrences.splice(insertAtIndex, 0, occurrence);

            var occurrenceViewBefore: api.dom.Element = this.getOccurrenceViewElementBefore(insertAtIndex);
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
            this.occurrences.forEach((currOccurrence: FormItemOccurrence<V>, index: number) => {
                currOccurrence.setIndex(index);
            });
        }

        refreshOccurrenceViews() {
            this.occurrenceViews.forEach((currOccurrenceView: V) => {
                currOccurrenceView.refresh();
            });
        }

        getOccurrenceViewElementBefore(index: number): V {
            if (index < 1) {
                return null;
            }
            return this.occurrenceViews.filter((occurrenceView: V) => {
                return occurrenceView.getIndex() == index - 1
            })[0];
        }

        countOccurrences(): number {
            return this.occurrences.length;
        }

        sortOccurrences(compareFunction: (a: form.FormItemOccurrence<V>, b: form.FormItemOccurrence<V>) => number) {

            /*console.log("this.occurrences before sort:");
            this.occurrences.forEach((o: FormItemOccurrence<V>, index: number)=> {
                console.log("  " + index + ": index=[" + o.getIndex() + "], dataId=[" + o.getDataId().toString() + "]");
            });*/

            this.occurrences.sort(compareFunction);

            /*console.log("this.occurrences after sort:");
            this.occurrences.forEach((o: FormItemOccurrence<V>, index: number)=> {
                console.log("  " + index + ": index=[" + o.getIndex() + "], dataId=[" + o.getDataId().toString() + "]");
            });*/



            /*console.log("this.occurrenceViews before sort:");
            this.occurrenceViews.forEach((ov: FormItemOccurrenceView, index: number)=> {
                console.log("  " + index + ": index=[" + ov.getIndex() + "], id=[" + ov.getId() + "]");
            });*/
            this.occurrenceViews.sort((a: FormItemOccurrenceView, b: FormItemOccurrenceView)=> {
                return a.getIndex() - b.getIndex();
            });
            /*console.log("this.occurrenceViews after sort:");
            this.occurrenceViews.forEach((ov: FormItemOccurrenceView, index: number)=> {
                console.log("  " + index + ": index=[" + ov.getIndex() + "], id=[" + ov.getId() + "]");
            });*/

            this.refreshOccurrenceViews();
        }

        getOccurrences(): FormItemOccurrence<V>[] {
            return this.occurrences;
        }

        canRemove() {
            return this.occurrences.length > Math.max(1, this.allowedOccurrences.getMinimum());
        }

        getOccurrenceViews(): V[] {
            return this.occurrenceViews;
        }

        getOccurrenceViewById(elementId: string): V {
            for (var i = 0 ; i < this.occurrenceViews.length ; i++ ) {
                if (this.occurrenceViews[i].getId() == elementId) {
                    return this.occurrenceViews[i];
                }
            }
            return null;
        }
    }
}