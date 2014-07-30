module api.ui.mask {

    /**
     * A statically accessible object for masking the whole body.
     */
    export class BodyMask extends Mask {

        private static instance: BodyMask;

        static get(): BodyMask {
            if (!BodyMask.instance) {
                BodyMask.instance = new BodyMask();
            }
            return BodyMask.instance;
        }

        constructor() {
            super();
            this.addClass("body-mask");
        }

    }
}
