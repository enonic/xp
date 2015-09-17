module api.dom {

    export class ElementAddedEvent extends ElementEvent {

        constructor(element: Element, target?: Element) {
            super("added", element, target);
        }
    }
}