module api.form {

    export class FormItemOccurrences<V extends FormItemOccurrenceView> {

        private occurrences: FormItemOccurrence<V>[] = [];

        private occurrenceViews: V[] = [];

        private occurrenceViewContainer: api.dom.Element;

        private formItem: FormItem;

        private allowedOccurrences: Occurrences;

        private occurrenceAddedListeners: {(event: OccurrenceAddedEvent):void}[] = [];

        private occurrenceRemovedListeners: {(event: OccurrenceRemovedEvent):void}[] = [];

        constructor(formItem: FormItem, occurrenceViewContainer: api.dom.Element, allowedOccurrences?: Occurrences) {
            this.formItem = formItem;
            this.occurrenceViewContainer = occurrenceViewContainer;
            this.allowedOccurrences = allowedOccurrences;
        }

        getAllowedOccurrences(): Occurrences {
            throw new Error("Must be implemented by inheritor");
        }

        onOccurrenceAdded(listener: (event: OccurrenceAddedEvent)=>void) {
            this.occurrenceAddedListeners.push(listener);
        }

        onOccurrenceRemoved(listener: (event: OccurrenceRemovedEvent)=>void) {
            this.occurrenceRemovedListeners.push(listener);
        }

        unOccurrenceAdded(listener: (event: OccurrenceAddedEvent)=>void) {
            this.occurrenceAddedListeners = this.occurrenceAddedListeners.filter((currentListener: (event: OccurrenceAddedEvent)=>void)=> {
                return listener != currentListener;
            });
        }

        unOccurrenceRemoved(listener: (event: OccurrenceRemovedEvent)=>void) {
            this.occurrenceRemovedListeners =
            this.occurrenceRemovedListeners.filter((currentListener: (event: OccurrenceRemovedEvent)=>void)=> {
                return listener != currentListener;
            });
        }

        private notifyOccurrenceAdded(occurrence: FormItemOccurrence<V>, occurrenceView:V) {
            this.occurrenceAddedListeners.forEach((listener: (event: OccurrenceAddedEvent)=>void)=> {
                listener.call(this, new OccurrenceAddedEvent(occurrence, occurrenceView))
            });
        }

        private notifyOccurrenceRemoved(occurrence: FormItemOccurrence<V>, occurrenceView:V) {
            this.occurrenceRemovedListeners.forEach((listener: (event: OccurrenceRemovedEvent)=>void)=> {
                listener.call(this, new OccurrenceRemovedEvent(occurrence, occurrenceView))
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
            this.notifyOccurrenceRemoved(occurrenceToRemove, occurrenceViewToRemove);
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
            this.notifyOccurrenceAdded(occurrence, occurrenceView);
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

            this.occurrences.sort(compareFunction);

            this.occurrenceViews.sort((a: FormItemOccurrenceView, b: FormItemOccurrenceView)=> {
                return a.getIndex() - b.getIndex();
            });

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