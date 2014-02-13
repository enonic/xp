module api.form.inputtype.support {

    export class InputOccurrenceView extends api.form.FormItemOccurrenceView implements api.event.Observable {

        private inputOccurrence:InputOccurrence;

        private inputElement:api.dom.Element;

        private removeButtonEl:api.dom.AEl;

        private dragControl:api.dom.DivEl;

        constructor(inputOccurrence:InputOccurrence, inputElement:api.dom.Element) {
            super("input-occurrence-view", inputOccurrence);

            this.dragControl = new api.dom.DivEl();

            this.appendChild(this.dragControl);

            this.inputOccurrence = inputOccurrence;

            this.inputElement = inputElement;
            this.appendChild(this.inputElement);

            this.removeButtonEl = new api.dom.AEl("remove-button");

            this.removeButtonEl.addClass('hidden');
            this.appendChild(this.removeButtonEl);
            this.removeButtonEl.setClickListener(() => {
                this.notifyRemoveButtonClicked();
            });

            this.refresh();
        }

        refresh() {

            if( !this.inputOccurrence.oneAndOnly() ){
                this.dragControl.addClass("drag-control");
            }
            else {
                this.dragControl.removeClass("drag-control");
            }

            this.getEl().setData("dataId", this.inputOccurrence.getDataId().toString());

            if (this.inputOccurrence.showRemoveButton()) {
                this.removeButtonEl.removeClass('hidden');
            }
            else if (!this.removeButtonEl.hasClass('hidden')) {
                this.removeButtonEl.addClass('hidden');
            }
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