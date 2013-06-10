module api_ui{

    /**
     * A statically accessible object for masking the whole body.
     */
    export class BodyMask extends api_ui.DivEl {

        private static instance:BodyMask = new BodyMask();

        static get():BodyMask {
            return instance;
        }

        constructor() {
            super("Mask");
            this.getEl().setDisplay("none");
            this.getEl().addClass("body-mask")
            this.getEl().setZindex(30000);

            document.body.appendChild(this.getHTMLElement());
        }

        activate() {
            this.getEl().setDisplay("block");
        }

        deActivate() {
            this.getEl().setDisplay("none");
        }
    }
}
