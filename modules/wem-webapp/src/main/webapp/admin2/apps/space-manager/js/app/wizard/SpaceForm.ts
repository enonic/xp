module app_wizard {

    export class SpaceForm extends api_ui.Panel {

        constructor() {
            super("SpaceForm");

            var h1El = new api_dom.H1El();
            h1El.getEl().setInnerHtml("SpaceForm: TODO");
            this.appendChild(h1El);
        }
    }
}
