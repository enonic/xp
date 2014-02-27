module api.dom {

    export class ElementHiddenEvent extends ElementEvent {

        constructor(element: Element, target?: Element) {
            super("hidden", element, target);
        }
    }
}