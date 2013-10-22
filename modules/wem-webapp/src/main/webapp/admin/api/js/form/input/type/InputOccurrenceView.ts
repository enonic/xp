module api_form_input_type {

    export class InputOccurrenceView extends api_form.FormItemOccurrenceView implements api_event.Observable {

        private inputOccurrence:InputOccurrence;

        private inputElement:api_dom.Element;

        private removeButtonEl:api_dom.AEl;

        private occurrenceCountEl:api_dom.SpanEl;

        constructor(inputOccurrence:InputOccurrence, inputElement:api_dom.Element) {
            super("InputOccurrenceView", "input-occurrence-view", inputOccurrence);
            this.inputOccurrence = inputOccurrence;

            this.inputElement = inputElement;
            this.appendChild(this.inputElement);

            this.occurrenceCountEl = new api_dom.SpanEl(null, "occurrence-count");
            this.appendChild(this.occurrenceCountEl);

            this.removeButtonEl = new api_dom.AEl(null, "remove-button");

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

            this.removeButtonEl.setVisible(this.inputOccurrence.showRemoveButton());
        }

        getIndex():number {
            return this.inputOccurrence.getIndex();
        }

        getInputElement():api_dom.Element {
            return this.inputElement;
        }
    }
}