module api.liveedit {

    import Event = api.event.Event;
    import ComponentView = api.liveedit.ComponentView;
    import Component = api.content.page.region.Component;

    export class ComponentResetEvent extends api.event.Event {

        private newComponentView: ComponentView<Component>;
        private oldComponentView: ComponentView<Component>;

        constructor(newComponentView: ComponentView<Component>, oldComponentView: ComponentView<Component>) {
            super();
            this.newComponentView = newComponentView;
            this.oldComponentView = oldComponentView;
        }

        getNewComponentView(): ComponentView<Component> {
            return this.newComponentView;
        }

        getOldComponentView(): ComponentView<Component> {
            return this.oldComponentView;
        }

        static on(handler: (event: ComponentResetEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ComponentResetEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}