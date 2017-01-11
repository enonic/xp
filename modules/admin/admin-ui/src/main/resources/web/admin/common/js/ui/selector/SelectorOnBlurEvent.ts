module api.ui.selector {

    import Element = api.dom.Element;

    export class SelectorOnBlurEvent extends api.event.Event {

        private selector: Element;

        constructor(selector: Element) {
            super();
            this.selector = selector;
        }

        getSelector(): Element {
            return this.selector;
        }

        static on(handler: (event: SelectorOnBlurEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: SelectorOnBlurEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

}
