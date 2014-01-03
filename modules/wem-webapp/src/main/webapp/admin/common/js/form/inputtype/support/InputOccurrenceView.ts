module api.form.inputtype.support {

    export class InputOccurrenceView extends api.form.FormItemOccurrenceView implements api.event.Observable {

        private inputOccurrence:InputOccurrence;

        private inputElement:api.dom.Element;

        private removeButtonEl:api.dom.AEl;

        private occurrenceCountEl:api.dom.SpanEl;

        constructor(inputOccurrence:InputOccurrence, inputElement:api.dom.Element) {
            super("InputOccurrenceView", "input-occurrence-view", inputOccurrence);
            this.inputOccurrence = inputOccurrence;



            this.inputElement = inputElement;
            this.appendChild(this.inputElement);

            this.occurrenceCountEl = new api.dom.SpanEl(null, "occurrence-count");
            this.appendChild(this.occurrenceCountEl);

            this.removeButtonEl = new api.dom.AEl(null, "remove-button");

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

        getInputElement():api.dom.Element {
            return this.inputElement;
        }

        giveFocus() : boolean {
            return this.inputElement.giveFocus();
        }
    }
}