module api.form {

    export class FormItemOccurrenceView extends api.dom.DivEl {

        private formItemOccurrence:FormItemOccurrence<FormItemOccurrenceView>;

        private listeners:FormItemOccurrenceViewListener[] = [];

        constructor(generateId:boolean, className, formItemOccurrence:FormItemOccurrence<FormItemOccurrenceView>) {
            super(generateId, className);
            this.formItemOccurrence = formItemOccurrence;
        }

        addListener(listener:FormItemOccurrenceViewListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:FormItemOccurrenceViewListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifyRemoveButtonClicked() {
            this.listeners.forEach((listener:FormItemOccurrenceViewListener) => {
                listener.onRemoveButtonClicked(this, this.formItemOccurrence.getIndex());
            });
        }

        getIndex():number {
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