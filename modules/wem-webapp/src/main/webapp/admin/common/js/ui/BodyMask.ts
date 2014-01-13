module api.ui {

    /**
     * A statically accessible object for masking the whole body.
     */
    export class BodyMask extends api.dom.DivEl {

        private static instance: BodyMask;

        static get(): BodyMask {
            if (!BodyMask.instance) {
                BodyMask.instance = new BodyMask();
            }
            return BodyMask.instance;
        }

        constructor() {
            super(true);
            this.getEl().setDisplay("none");
            this.getEl().addClass("body-mask");
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
