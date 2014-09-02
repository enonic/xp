module api.content.form.inputtype.image {

    export class ImageSelectorDialog extends api.dom.DivEl {

        private content: ImageSelectorDisplayValue;

        private nameEl: api.dom.H1El;

        private pathEl: api.dom.PEl;

        constructor() {
            super("dialog");

            this.nameEl = new api.dom.H1El();
            this.appendChild(this.nameEl);

            this.pathEl = new api.dom.PEl();
            this.appendChild(this.pathEl);

        }

        setContent(value: ImageSelectorDisplayValue) {
            this.content = value;
            this.refreshUI();
        }

        getContent(): ImageSelectorDisplayValue {
            return this.content;
        }

        private refreshUI() {
            this.nameEl.getEl().setInnerHtml(this.content.getLabel());
            this.pathEl.getEl().setInnerHtml(this.content.getPath());
        }

    }

}