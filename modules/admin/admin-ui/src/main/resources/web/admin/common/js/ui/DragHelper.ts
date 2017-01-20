module api.ui {

    export class DragHelper extends api.dom.DivEl {

        public static CURSOR_AT: {left: number, top: number} = {left: -10, top: -15};

        private static instance: DragHelper;

        public static debug: boolean = false;

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

        public setItemName(itemName: string) {
            let p = new api.dom.PEl();
            p.setClass('drag-item-name');
            p.setHtml(itemName);

            this.removeChildren();
            this.appendChild(p);
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
