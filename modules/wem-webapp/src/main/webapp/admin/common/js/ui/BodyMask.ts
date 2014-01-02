module api.ui{

    /**
     * A statically accessible object for masking the whole body.
     */
    export class BodyMask extends api.dom.DivEl {

        private static instance:BodyMask = new BodyMask();

        static get():BodyMask {
            return BodyMask.instance;
        }

        constructor() {
            super("BodyMask");
            this.getEl().setDisplay("none");
            this.getEl().addClass("body-mask")
            this.getEl().setZindex(30000);

            api.dom.Body.get().appendChild(this);
        }

        activate() {
            this.getEl().setDisplay("block");
        }

        deActivate() {
            this.getEl().setDisplay("none");
        }
    }
}
