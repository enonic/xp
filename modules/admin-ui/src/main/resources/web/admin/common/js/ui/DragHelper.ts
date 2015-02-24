module api.ui {

    export class DragHelper extends api.dom.DivEl {

        public static CURSOR_AT = {left: -10, top: -15};

        private static instance: DragHelper;

        public static debug = true;

        public static get(): DragHelper {
            if (!DragHelper.instance) {
                DragHelper.instance = new DragHelper();
            }
            return DragHelper.instance;
        }

        constructor() {
            super('drag-helper');
            this.setId('drag-helper');
        }

        public setDropAllowed(allowed: boolean): DragHelper {
            if (DragHelper.debug) {
                console.log('DragHelper.setDropAllowed: ' + allowed.toString());
            }
            this.toggleClass('drop-allowed', allowed);
            return this;
        }

        isDropAllowed(): boolean {
            return this.hasClass('drop-allowed');
        }

        reset(): DragHelper {
            this.setDropAllowed(false);
            return this;
        }

    }
}
