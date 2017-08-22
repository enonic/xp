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

        protected occurrenceViews: V[] = [];

        private occurrenceViewContainer: api.dom.Element;

        private formItem: FormItem;

        protected propertyArray: PropertyArray;

        private allowedOccurrences: Occurrences;

        private occurrenceAddedListeners: {(event: OccurrenceAddedEvent): void}[] = [];

        private occurrenceRenderedListeners: {(event: OccurrenceRenderedEvent): void}[] = [];

        private occurrenceRemovedListeners: {(event: OccurrenceRemovedEvent): void}[] = [];

        private focusListeners: {(event: FocusEvent): void}[] = [];

        private blurListeners: {(event: FocusEvent): void}[] = [];

        private focusListener: (event: FocusEvent) => void;

        private blurListener: (event: FocusEvent) => void;

        public static debug: boolean = false;

        constructor(config: FormItemOccurrencesConfig) {
            this.formItem = config.formItem;
            this.propertyArray = config.propertyArray;
            this.occurrenceViewContainer = config.occurrenceViewContainer;
            this.allowedOccurrences = config.allowedOccurrences;

            this.focusListener = (event: FocusEvent) => this.notifyFocused(event);
            this.blurListener = (event: FocusEvent) => this.notifyBlurred(event);
        }

        protected constructOccurrencesForNoData(): FormItemOccurrence<V>[] {
            throw new Error('Must be implemented by inheritor');
        }

        protected  constructOccurrencesForData(): FormItemOccurrence<V>[] {
            throw new Error('Must be implemented by inheritor');
        }

        getAllowedOccurrences(): Occurrences {
            throw new Error('Must be implemented by inheritor');
        }

        onOccurrenceRendered(listener: (event: OccurrenceRenderedEvent)=>void) {
            this.occurrenceRenderedListeners.push(listener);
        }

        refreshOccurence(index: number) {
            //to be implemented on demand in inheritors
        }

        unOccurrenceRendered(listener: (event: OccurrenceRenderedEvent)=>void) {
            this.occurrenceRenderedListeners =
                this.occurrenceRenderedListeners.filter((currentListener: (event: OccurrenceRenderedEvent)=>void)=> {
                    return listener !== currentListener;
                });
        }

        private notifyOccurrenceRendered(occurrence: FormItemOccurrence<V>, occurrenceView: V, validate: boolean) {
            this.occurrenceRenderedListeners.forEach((listener: (event: OccurrenceRenderedEvent)=>void)=> {
                listener.call(this, new OccurrenceRenderedEvent(occurrence, occurrenceView, validate));
            });
        }

        onOccurrenceAdded(listener: (event: OccurrenceAddedEvent)=>void) {
            this.occurrenceAddedListeners.push(listener);
        }

        unOccurrenceAdded(listener: (event: OccurrenceAddedEvent)=>void) {
            this.occurrenceAddedListeners = this.occurrenceAddedListeners.filter((currentListener: (event: OccurrenceAddedEvent)=>void)=> {
                return listener !== currentListener;
            });
        }

        private notifyOccurrenceAdded(occurrence: FormItemOccurrence<V>, occurrenceView: V) {
            this.occurrenceAddedListeners.forEach((listener: (event: OccurrenceAddedEvent)=>void)=> {
                listener.call(this, new OccurrenceAddedEvent(occurrence, occurrenceView));
            });
        }

        onOccurrenceRemoved(listener: (event: OccurrenceRemovedEvent)=>void) {
            this.occurrenceRemovedListeners.push(listener);
        }

        unOccurrenceRemoved(listener: (event: OccurrenceRemovedEvent)=>void) {
            this.occurrenceRemovedListeners =
                this.occurrenceRemovedListeners.filter((currentListener: (event: OccurrenceRemovedEvent)=>void)=> {
                    return listener !== currentListener;
                });
        }

        private notifyOccurrenceRemoved(occurrence: FormItemOccurrence<V>, occurrenceView: V) {
            this.occurrenceRemovedListeners.forEach((listener: (event: OccurrenceRemovedEvent)=>void)=> {
                listener.call(this, new OccurrenceRemovedEvent(occurrence, occurrenceView));
            });
        }

        getFormItem(): FormItem {
            return this.formItem;
        }

        maximumOccurrencesReached(): boolean {
            return this.allowedOccurrences.maximumReached(this.countOccurrences());
        }

        layout(validate: boolean = true): wemQ.Promise<void> {

            let occurrences;
            if (this.propertyArray.getSize() > 0) {
                occurrences = this.constructOccurrencesForData();
            } else {
                occurrences = this.constructOccurrencesForNoData();
            }

            let layoutPromises: wemQ.Promise<V>[] = [];
            occurrences.forEach((occurrence: FormItemOccurrence<V>) => {
                layoutPromises.push(this.addOccurrence(occurrence, validate));
            });

            return wemQ.all(layoutPromises).spread<void>(() => wemQ<void>(null));
        }

        update(propertyArray: PropertyArray, unchangedOnly?: boolean): wemQ.Promise<void> {
            if (FormItemOccurrences.debug) {
                console.debug('FormItemOccurrences.update:', propertyArray);
            }

            // first trim existing occurrences if there are too many
            let arraySize = propertyArray.getSize();
            let occurrencesViewClone = [].concat(this.occurrenceViews);
            if (occurrencesViewClone.length > arraySize) {
                for (let i = arraySize; i < occurrencesViewClone.length; i++) {
                    this.removeOccurrenceView(occurrencesViewClone[i]);
                }
            }

            // next update propertyArray because it's used for creation of new occurrences
            this.propertyArray = propertyArray;

            let promises = [];
            // next update existing occurrences and add missing ones if there are not enough
            this.propertyArray.forEach((property: api.data.Property, index: number) => {
                let occurrenceView = this.occurrenceViews[index];
                let occurrence = this.occurrences[index];
                if (occurrenceView && occurrence) {
                    // update occurrence index
                    occurrence.setIndex(index);
                    // update occurence view
                    promises.push(this.updateOccurrenceView(occurrenceView, propertyArray, unchangedOnly));
                } else {
                    promises.push(this.createAndAddOccurrence(index));
                }
            });

            return wemQ.all(promises).spread<void>(() => wemQ<void>(null));
        }

        reset() {
            this.propertyArray.forEach((property: api.data.Property, i: number) => {
                let occurrenceView = this.occurrenceViews[i];
                let occurrence = this.occurrences[i];
                if (occurrenceView && occurrence) {
                    this.resetOccurrenceView(occurrenceView);
                }
            });
        }

        createNewOccurrenceView(occurrence: FormItemOccurrence<V>): V {
            throw new Error('Must be implemented by inheritor');
        }

        updateOccurrenceView(occurrenceView: V, propertyArray: PropertyArray, unchangedOnly?: boolean): wemQ.Promise<void> {
            throw new Error('Must be implemented by inheritor');
        }

        resetOccurrenceView(occurrenceView: V) {
            throw new Error('Must be implemented by inheritor');
        }

        createNewOccurrence(formItemOccurrences: FormItemOccurrences<V>, insertAtIndex: number): FormItemOccurrence<V> {
            throw new Error('Must be implemented by inheritor');
        }

        public createAndAddOccurrence(insertAtIndex: number = this.countOccurrences(), validate: boolean = true): wemQ.Promise<V> {

            let occurrence: FormItemOccurrence<V> = this.createNewOccurrence(this, insertAtIndex);

            return this.addOccurrence(occurrence, validate);
        }

        protected addOccurrence(occurrence: FormItemOccurrence<V>, validate: boolean = true): wemQ.Promise<V> {
            if (FormItemOccurrences.debug) {
                console.debug('FormItemOccurrences.addOccurrence:', occurrence);
            }

            let countOccurrences = this.countOccurrences();
            if (this.allowedOccurrences.maximumReached(countOccurrences)) {
                return;
            }

            let occurrenceView: V = this.createNewOccurrenceView(occurrence);
            occurrenceView.onFocus(this.focusListener);
            occurrenceView.onBlur(this.blurListener);

            let insertAtIndex = occurrence.getIndex();
            this.occurrences.splice(insertAtIndex, 0, occurrence);

            let occurrenceViewBefore: api.dom.Element = this.getOccurrenceViewElementBefore(insertAtIndex);
            if (insertAtIndex === countOccurrences || !occurrenceViewBefore) {
                this.occurrenceViewContainer.appendChild(occurrenceView);
            } else {
                occurrenceView.insertAfterEl(occurrenceViewBefore);
            }

            this.occurrenceViews.splice(insertAtIndex, 0, occurrenceView);

            this.notifyOccurrenceAdded(occurrence, occurrenceView);

            return occurrenceView.layout(validate).then(() => {
                this.resetOccurrenceIndexes();
                this.refreshOccurrenceViews();
                occurrenceView.giveFocus();
                this.notifyOccurrenceRendered(occurrence, occurrenceView, validate);
                return occurrenceView;
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
                return null;
            });
        }

        protected removeOccurrenceView(occurrenceViewToRemove: V) {
            if (FormItemOccurrences.debug) {
                console.debug('FormItemOccurrences.removeOccurrenceView:', occurrenceViewToRemove);
            }

            if (!this.canRemove()) {
                return;
            }

            let indexToRemove = occurrenceViewToRemove.getIndex();

            occurrenceViewToRemove.unFocus(this.focusListener);
            occurrenceViewToRemove.unBlur(this.blurListener);

            occurrenceViewToRemove.remove();
            this.occurrenceViews = this.occurrenceViews.filter((curr: V) => {
                return curr !== occurrenceViewToRemove;
            });
            let occurrenceToRemove = this.occurrences[indexToRemove];
            this.occurrences = this.occurrences.filter((curr: FormItemOccurrence<V>) => {
                return curr.getIndex() !== indexToRemove;
            });

            this.resetOccurrenceIndexes();
            this.refreshOccurrenceViews();

            if (this.propertyArray.get(indexToRemove)) { // if not already removed
                this.propertyArray.remove(indexToRemove);
            }

            this.notifyOccurrenceRemoved(occurrenceToRemove, occurrenceViewToRemove);
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners.push(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners = this.focusListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners.push(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners = this.blurListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocused(event: FocusEvent) {
            this.focusListeners.forEach((listener) => {
                listener(event);
            });
        }

        private notifyBlurred(event: FocusEvent) {
            this.blurListeners.forEach((listener) => {
                listener(event);
            });
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
                return occurrenceView.getIndex() === index - 1;
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

            this.propertyArray.move(fromIndex, toIndex);

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
