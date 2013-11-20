module api_form_inputtype_content_image {

    export class ImageSelectorDialog extends api_dom.DivEl {

        private content:api_content.ContentSummary;

        private nameEl:api_dom.H1El;

        private pathEl:api_dom.PEl;

        private removeButton:api_ui.Button;

        private removeButtonClickedListeners:{(): void;}[] = [];

        constructor() {
            super("ImageSelectorDialog", "dialog");

            this.nameEl = new api_dom.H1El();
            this.appendChild(this.nameEl);

            this.pathEl = new api_dom.PEl();
            this.appendChild(this.pathEl);

            var buttonsBar = new api_dom.DivEl().addClass("buttons-bar");

            var editButton = new api_ui.Button("Edit").addClass("edit");
            buttonsBar.appendChild(editButton);

            this.removeButton = new api_ui.Button("Remove");
            this.removeButton.addClass("remove");
            buttonsBar.appendChild(this.removeButton);
            this.removeButton.getEl().addEventListener("click", (event) => {
                this.hide();
                this.notifyRemoveButtonClicked();
            });

            this.appendChild(buttonsBar);

        }

        setContent(value:api_content.ContentSummary) {
            this.content = value;
            this.refreshUI();
        }

        private refreshUI(){
            this.nameEl.getEl().setInnerHtml(this.content.getName());
            this.pathEl.getEl().setInnerHtml(this.content.getPath().toString());
        }

        private notifyRemoveButtonClicked() {
            this.removeButtonClickedListeners.forEach( (listener) => {
                listener();
            });
        }

        addSelectedOptionRemovedListener(listener:{(): void;}) {
            this.removeButtonClickedListeners.push(listener);
        }
    }

}