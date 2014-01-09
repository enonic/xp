module api.form.inputtype.content.image {

    export class ImageSelectorDialog extends api.dom.DivEl {

        private content:api.content.ContentSummary;

        private nameEl:api.dom.H1El;

        private pathEl:api.dom.PEl;

        private removeButton:api.ui.Button;

        private removeButtonClickedListeners:{(): void;}[] = [];

        private editButton:api.ui.Button;

        private editButtonClickedListeners:{(): void;}[] = [];

        constructor() {
            super("ImageSelectorDialog", "dialog");

            this.nameEl = new api.dom.H1El();
            this.appendChild(this.nameEl);

            this.pathEl = new api.dom.PEl();
            this.appendChild(this.pathEl);

            var buttonsBar = new api.dom.DivEl().addClass("buttons-bar");

            this.editButton = new api.ui.Button("Edit");
            this.editButton.addClass("edit");
            buttonsBar.appendChild(this.editButton);
            this.editButton.getEl().addEventListener("click", (event) => {
                this.notifyEditButtonClicked();
            });


            this.removeButton = new api.ui.Button("Remove");
            this.removeButton.addClass("remove");
            buttonsBar.appendChild(this.removeButton);
            this.removeButton.getEl().addEventListener("click", (event) => {
                this.hide();
                this.notifyRemoveButtonClicked();
            });

            this.appendChild(buttonsBar);

        }

        setContent(value:api.content.ContentSummary) {
            this.content = value;
            this.refreshUI();
        }

        private refreshUI(){
            this.nameEl.getEl().setInnerHtml(this.content.getName().toString());
            this.pathEl.getEl().setInnerHtml(this.content.getPath().toString());
        }

        private notifyRemoveButtonClicked() {
            this.removeButtonClickedListeners.forEach( (listener) => {
                listener();
            });
        }

        private notifyEditButtonClicked() {
            this.editButtonClickedListeners.forEach( (listener) => {
                listener();
            });
        }

        addSelectedOptionRemovedListener(listener:{(): void;}) {
            this.removeButtonClickedListeners.push(listener);
        }

        addSelectedOptionEditListener(listener:{(): void;}) {
            this.editButtonClickedListeners.push(listener);
        }
    }

}