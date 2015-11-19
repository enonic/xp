module api.form {

    import PropertyArray = api.data.PropertyArray;

    export class FormItemOccurrenceView extends api.dom.DivEl {

        private formItemOccurrence: FormItemOccurrence<FormItemOccurrenceView>;

        private removeButtonClickedListeners: {(event: RemoveButtonClickedEvent<FormItemOccurrenceView>):void}[] = [];

        constructor(className, formItemOccurrence: FormItemOccurrence<FormItemOccurrenceView>) {
            super(className);
            this.formItemOccurrence = formItemOccurrence;
        }

        getDataPath(): api.data.PropertyPath {
            throw new Error("Must be implemented by inheritor");
        }

        public layout(): wemQ.Promise<void> {
            return wemQ<void>(null);
        }

        public update(propertyArray: PropertyArray, unchangedOnly?: boolean): wemQ.Promise<void> {
            return wemQ<void>(null);
        }

        hasValidUserInput(): boolean {

            throw new Error("Must be implemented by inheritor");
        }

        onRemoveButtonClicked(listener: (event: RemoveButtonClickedEvent<FormItemOccurrenceView>)=>void) {
            this.removeButtonClickedListeners.push(listener);
        }

        unRemoveButtonClicked(listener: (event: RemoveButtonClickedEvent<FormItemOccurrenceView>)=>void) {
            this.removeButtonClickedListeners.filter((currentListener: (event: RemoveButtonClickedEvent<FormItemOccurrenceView>)=>void) => {
                return currentListener != listener;
            });
        }

        notifyRemoveButtonClicked() {
            this.removeButtonClickedListeners.forEach((listener: (event: RemoveButtonClickedEvent<FormItemOccurrenceView>)=>void) => {
                listener.call(this, new RemoveButtonClickedEvent(this, this.formItemOccurrence.getIndex()));
            });
        }

        getIndex(): number {
            return this.formItemOccurrence.getIndex();
        }

        refresh() {
            throw new Error("Must be implemented by inheritor");
        }

        giveFocus(): boolean {
            return false;
        }
    }
}