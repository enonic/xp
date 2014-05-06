module api.dom {

    export class ElementResizedEvent extends ElementEvent {

        private newWidth:number;
        private newHeight:number;

        constructor(newWidth:number, newHeight:number, element: Element, target?: Element) {
            super("resized", element, target);
            this.newWidth = newWidth;
            this.newHeight = newHeight;
        }

        getNewWidth(): number {
            return this.newWidth;
        }

        getNewHeight(): number {
            return this.newHeight;
        }
    }
}