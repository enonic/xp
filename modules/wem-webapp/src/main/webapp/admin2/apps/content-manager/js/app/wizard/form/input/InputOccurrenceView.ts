module app_wizard_form_input {

    export class InputOccurrenceView extends api_dom.DivEl implements api_event.Observable {

        private inputOccurrence:InputOccurrence;

        private inputElement:api_dom.Element;

        private addButtonEl:api_dom.ButtonEl;

        private removeButtonEl:api_dom.ButtonEl;

        private occurrenceCountEl:api_dom.SpanEl;

        private listeners:InputOccurrenceViewListener[] = [];

        constructor(inputOccurrence:InputOccurrence, inputElement:api_dom.Element) {
            super("InputOccurrenceView", "input-occurrence-view");
            this.inputOccurrence = inputOccurrence;

            this.inputElement = inputElement;
            this.appendChild(this.inputElement);

            this.occurrenceCountEl = new api_dom.SpanEl(null, "occurrence-count");
            this.appendChild(this.occurrenceCountEl);

            this.addButtonEl = new api_ui.Button("+");
            this.addButtonEl.setClass("add-button");
            this.appendChild(this.addButtonEl);
            this.addButtonEl.setClickListener(() => {
                this.notifyAddButtonClicked();
            });

            this.removeButtonEl = new api_ui.Button("X");
            this.removeButtonEl.setClass("remove-button");
            this.removeButtonEl.hide();
            this.appendChild(this.removeButtonEl);
            this.removeButtonEl.setClickListener(() => {
                this.notifyRemoveButtonClicked();
            });

            this.refresh();
        }

        refresh() {

            this.occurrenceCountEl.setHtml("#" + (this.inputOccurrence.getIndex() + 1));
            this.getEl().setData("dataId", this.inputOccurrence.getDataId().toString());


            if (this.inputOccurrence.showRemoveButton()) {
                this.removeButtonEl.show();
            }
            else {
                this.removeButtonEl.hide();
            }

            if (this.inputOccurrence.showAddButton()) {
                this.addButtonEl.show();
            }
            else {
                this.addButtonEl.hide();
            }
        }

        getIndex():number {
            return this.inputOccurrence.getIndex();
        }

        getInputElement():api_dom.Element {
            return this.inputElement;
        }

        addListener(listener:InputOccurrenceViewListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:InputOccurrenceViewListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyRemoveButtonClicked() {
            this.listeners.forEach((listener:InputOccurrenceViewListener) => {
                if (listener.onRemoveButtonClicked) {
                    listener.onRemoveButtonClicked(this, this.inputOccurrence.getIndex());
                }
            });
        }

        private notifyAddButtonClicked() {
            this.listeners.forEach((listener:InputOccurrenceViewListener) => {
                if (listener.onAddButtonClicked) {
                    listener.onAddButtonClicked(this);
                }
            });
        }
    }
}