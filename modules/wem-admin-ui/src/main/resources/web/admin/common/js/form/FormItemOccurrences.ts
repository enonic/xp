module api.form {

    import PropertyArray = api.data.PropertyArray;

    export interface FormItemOccurrencesConfig {

        formItem: FormItem;

        propertyArray: PropertyArray;

        occurrenceViewContainer: api.dom.Element;

        allowedOccurrences?: Occurrences;

    }

    export class FormItemOccurrences<V extends FormItemOccurrenceView> {

        private occurrences: FormItemOccurrence<V>[] = [];

        private occurrenceViews: V[] = [];

        private occurrenceViewContainer: api.dom.Element;

        private formItem: FormItem;

        propertyArray: PropertyArray;

        private allowedOccurrences: Occurrences;

        private occurrenceAddedListeners: {(event: OccurrenceAddedEvent):void}[] = [];

        private occurrenceRemovedListeners: {(event: OccurrenceRemovedEvent):void}[] = [];

        private focusListeners: {(event: FocusEvent): void}[] = [];

        private blurListeners: {(event: FocusEvent): void}[] = [];

        constructor(config: FormItemOccurrencesConfig) {
            this.formItem = config.formItem;
            this.propertyArray = config.propertyArray;
            this.occurrenceViewContainer = config.occurrenceViewContainer;
            this.allowedOccurrences = config.allowedOccurrences;
        }

        getAllowedOccurrences(): Occurrences {
            throw new Error("Must be implemented by inheritor");
        }

        onOccurrenceAdded(listener: (event: OccurrenceAddedEvent)=>void) {
            this.occurrenceAddedListeners.push(listener);
        }

        unOccurrenceAdded(listener: (event: OccurrenceAddedEvent)=>void) {
            this.occurrenceAddedListeners = this.occurrenceAddedListeners.filter((currentListener: (event: OccurrenceAddedEvent)=>void)=> {
                return listener != currentListener;
            });
        }

        private notifyOccurrenceAdded(occurrence: FormItemOccurrence<V>, occurrenceView: V) {
            this.occurrenceAddedListeners.forEach((listener: (event: OccurrenceAddedEvent)=>void)=> {
                listener.call(this, new OccurrenceAddedEvent(occurrence, occurrenceView))
            });
        }

        onOccurrenceRemoved(listener: (event: OccurrenceRemovedEvent)=>void) {
            this.occurrenceRemovedListeners.push(listener);
        }

        unOccurrenceRemoved(listener: (event: OccurrenceRemovedEvent)=>void) {
            this.occurrenceRemovedListeners =
            this.occurrenceRemovedListeners.filter((currentListener: (event: OccurrenceRemovedEvent)=>void)=> {
                return listener != currentListener;
            });
        }

        private notifyOccurrenceRemoved(occurrence: FormItemOccurrence<V>, occurrenceView: V) {
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

        layout(): wemQ.Promise<void> {
            var layoutPromises: wemQ.Promise<void>[] = [];
            this.occurrences.forEach((occurrence: FormItemOccurrence<V>) => {
                var occurrenceView: V = this.createNewOccurrenceView(occurrence);

                this.occurrenceViewContainer.appendChild(occurrenceView);

                var layoutPromise = occurrenceView.layout();
                layoutPromises.push(layoutPromise);
                layoutPromise.then(() => {
                    occurrenceView.onFocus((event: FocusEvent) => this.notifyFocused(event));
                    occurrenceView.onBlur((event: FocusEvent) => this.notifyBlurred(event));
                    this.occurrenceViews.push(occurrenceView);
                });
            });

            return wemQ.all(layoutPromises).spread<void>(() => wemQ<void>(null));
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

            this.propertyArray.remove(indexToRemove);

            this.notifyOccurrenceRemoved(occurrenceToRemove, occurrenceViewToRemove);
        }

        createAndAddOccurrence() {

            var insertAtIndex: number = this.countOccurrences();
            var occurrence: FormItemOccurrence<V> = this.createNewOccurrence(this, insertAtIndex);

            this.doAddOccurrence(occurrence);

            var occurenceView = this.occurrenceViews[occurrence.getIndex()];
            occurenceView.onFocus((event: FocusEvent) => this.notifyFocused(event));
            occurenceView.onBlur((event: FocusEvent) => this.notifyBlurred(event));
            if (occurenceView) {
                occurenceView.giveFocus();
            }
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners.push(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners = this.focusListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners.push(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners = this.blurListeners.filter((curr) => {
                return curr != listener;
            })
        }

        private notifyFocused(event: FocusEvent) {
            this.focusListeners.forEach((listener) => {
                listener(event);
            })
        }

        private notifyBlurred(event: FocusEvent) {
            this.blurListeners.forEach((listener) => {
                listener(event);
            })
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
            occurrenceView.layout();

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


        moveOccurrence(fromIndex: number, toIndex: number) {

            // move FormItemSetOccurrence
            api.util.ArrayHelper.moveElement(fromIndex, toIndex, this.occurrences);
            // update FormItemSetOccurrence indexes
            this.occurrences.forEach((occurrence: FormItemOccurrence<V>, index: number) => {
                occurrence.setIndex(index);
            });

            // move FormItemOccurrenceView
            api.util.ArrayHelper.moveElement(fromIndex, toIndex, this.occurrenceViews);

            this.propertyArray.move(fromIndex, toIndex)

        }

        getOccurrences(): FormItemOccurrence<V>[] {
            return this.occurrences;
        }

        canRemove() {
            return this.occurrences.length > Math.max(0, this.allowedOccurrences.getMinimum());
        }

        getOccurrenceViews(): V[] {
            return this.occurrenceViews;
        }
    }
}