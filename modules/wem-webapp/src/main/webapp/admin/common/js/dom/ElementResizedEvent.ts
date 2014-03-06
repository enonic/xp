module api.dom {

    export class ElementResizedEvent extends ElementEvent {

        private newSize:number;

        constructor(newSize:number, element: Element, target?: Element) {
            super("rendered", element, target);
            this.newSize = newSize;
        }

        getNewSize(): number {
            return this.newSize;
        }
    }
}