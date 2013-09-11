module app_wizard_form_input {

    export class InputOccurrenceView extends api_dom.DivEl implements api_event.Observable {

        private input:api_schema_content_form.Input;

        private inputElement:api_dom.Element;

        private index:number;

        private addButtonEl:api_dom.ButtonEl;

        private removeButtonEl:api_dom.ButtonEl;

        private occurrenceCountEl:api_dom.SpanEl;

        private listeners:InputOccurrenceViewListener[] = [];

        constructor(input:api_schema_content_form.Input, inputElement:api_dom.Element, index:number) {
            super("InputOccurrenceView", "input-occurrence-view");
            this.input = input;
            this.index = index;
            this.getEl().setData("dataId", this.input.getName() + "[" + this.index + "]");

            this.inputElement = inputElement;
            this.appendChild(this.inputElement);

            this.occurrenceCountEl = new api_dom.SpanEl(null, "occurrence-count");
            this.occurrenceCountEl.setHtml("#" + (index + 1 ));
            this.appendChild(this.occurrenceCountEl);

            this.removeButtonEl = new api_ui.Button("X");
            this.removeButtonEl.setClass("remove-button");
            this.removeButtonEl.hide();
            this.appendChild(this.removeButtonEl);
            this.removeButtonEl.setClickListener(() => {
                this.notifyRemoveButtonClicked();
            });

            var showRemoveButton = (this.index + 1) > Math.max(1, this.input.getOccurrences().getMinimum());
            if (showRemoveButton) {
                this.removeButtonEl.show();
            }

            this.addButtonEl = new api_ui.Button("+");
            this.addButtonEl.setClass("add-button");
            this.appendChild(this.addButtonEl);
            this.addButtonEl.setClickListener(() => {
                this.notifyAddButtonClicked();
            });
        }


        setIndex(value:number) {
            this.index = value;
            this.occurrenceCountEl.setHtml("#" + (this.index + 1));
            this.getEl().setData("dataId", this.input.getName() + "[" + this.index + "]");
        }

        getIndex():number {
            return this.index;
        }

        getInputElement():api_dom.Element {
            return this.inputElement;
        }

        showRemoveButton(value:boolean) {
            if (value) {
                this.removeButtonEl.show();
            }
            else {
                this.removeButtonEl.hide();
            }
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
                    listener.onRemoveButtonClicked(this);
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