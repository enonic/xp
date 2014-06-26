module api.liveedit {

    import Event2 = api.event.Event2;
    import PageComponent = api.content.page.PageComponent;

    export class ItemFromContextWindowDroppedEvent extends Event2 {

        constructor() {
            super();
        }

        static on(handler: (event: ItemFromContextWindowDroppedEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ItemFromContextWindowDroppedEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}