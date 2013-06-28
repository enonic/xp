module app_wizard {

    export class ContentForm extends api_ui.Panel {

        constructor() {
            super("ContentForm");

            var h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("ContentForm: TODO");
            this.appendChild(h1El);
        }
    }
}
