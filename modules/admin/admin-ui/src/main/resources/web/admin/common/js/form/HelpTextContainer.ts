module api.form {

    export class HelpTextContainer {

        private helpTextDiv: api.dom.DivEl;

        private helpTextToggler: api.dom.DivEl;

        constructor(value: string) {
            this.helpTextToggler = new api.dom.DivEl("help-text-toggler");
            this.helpTextToggler.setHtml("?");

            this.helpTextDiv = new api.dom.DivEl("help-text");

            var pEl = new api.dom.PEl();
            pEl.getEl().setText(value);

            this.helpTextDiv.appendChild(pEl);

            this.helpTextToggler.onClicked(() => {
                this.helpTextDiv.toggleClass("visible");
                this.helpTextToggler.toggleClass("on");
            });
        }

        toggleHelpText(show?: boolean) {
            this.helpTextDiv.toggleClass("visible", show);
            this.helpTextToggler.toggleClass("on", show);
        }

        getToggler(): api.dom.DivEl {
            return this.helpTextToggler;
        }

        getHelpText(): api.dom.DivEl {
            return this.helpTextDiv;
        }
    }
}