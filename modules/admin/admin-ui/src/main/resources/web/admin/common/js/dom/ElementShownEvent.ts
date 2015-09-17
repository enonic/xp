module api.dom {

    export class ElementShownEvent extends ElementEvent {

        constructor(element: Element, target?: Element) {
            super("shown", element, target);
        }
    }
}