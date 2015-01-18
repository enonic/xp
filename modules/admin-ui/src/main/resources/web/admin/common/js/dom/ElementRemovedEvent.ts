module api.dom {

    export class ElementRemovedEvent extends ElementEvent {

        constructor(element: Element, target?: Element) {
            super("removed", element, target);
        }
    }
}