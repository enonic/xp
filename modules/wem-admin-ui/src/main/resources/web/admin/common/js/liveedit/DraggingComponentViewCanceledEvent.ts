module api.liveedit {

    import Event = api.event.Event;
    import Component = api.content.page.Component;

    export class DraggingComponentViewCanceledEvent extends Event {

        private pageComponentView: PageComponentView<Component>;

        constructor(pageComponentView: PageComponentView<Component>) {
            super();
            this.pageComponentView = pageComponentView;
        }

        getPageComponentView(): PageComponentView<Component> {
            return this.pageComponentView;
        }

        static on(handler: (event: DraggingComponentViewCompletedEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: DraggingComponentViewCompletedEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}