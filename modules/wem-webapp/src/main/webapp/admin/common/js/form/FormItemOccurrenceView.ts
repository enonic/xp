module api.form {

    export class FormItemOccurrenceView extends api.dom.DivEl {

        private formItemOccurrence:FormItemOccurrence<FormItemOccurrenceView>;

        private dragControl:api.dom.DivEl;

        private listeners:FormItemOccurrenceViewListener[] = [];

        constructor(idPrefix:string, className, formItemOccurrence:FormItemOccurrence<FormItemOccurrenceView>) {
            super(idPrefix, className);
            this.formItemOccurrence = formItemOccurrence;
            this.dragControl = new api.dom.DivEl();
            this.dragControl.addClass("drag-control");
            this.appendChild(this.dragControl);
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