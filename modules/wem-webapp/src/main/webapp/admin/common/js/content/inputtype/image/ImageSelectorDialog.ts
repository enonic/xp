module api.content.inputtype.image {

    export class ImageSelectorDialog extends api.dom.DivEl {

        private content: api.content.ContentSummary;

        private nameEl: api.dom.H1El;

        private pathEl: api.dom.PEl;

        constructor() {
            super("dialog");

            this.nameEl = new api.dom.H1El();
            this.appendChild(this.nameEl);

            this.pathEl = new api.dom.PEl();
            this.appendChild(this.pathEl);

        }

        setContent(value: api.content.ContentSummary) {
            this.content = value;
            this.refreshUI();
        }

        private refreshUI() {
            var path: string = this.content.getPath().toString();
            this.nameEl.getEl().setInnerHtml(this.content.getName().toString());
            this.pathEl.getEl().setInnerHtml(path);
        }

    }

}