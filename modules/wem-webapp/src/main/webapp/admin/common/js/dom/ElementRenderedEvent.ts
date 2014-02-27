module api.dom {

    export class ElementRenderedEvent extends ElementEvent {

        constructor(element: Element, target?: Element) {
            super("rendered", element, target);
        }
    }
}