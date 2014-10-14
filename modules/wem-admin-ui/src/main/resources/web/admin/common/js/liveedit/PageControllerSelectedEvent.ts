module api.liveedit {

    import PageDescriptor = api.content.page.PageDescriptor;

    export class PageControllerSelectedEvent extends api.event.Event {

        private pageDescriptor: PageDescriptor;

        constructor(pageDescriptor: PageDescriptor) {
            super();
            this.pageDescriptor = pageDescriptor;
        }

        getPageDescriptor(): PageDescriptor {
            return this.pageDescriptor;
        }

        static on(handler: (event: PageControllerSelectedEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: PageControllerSelectedEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}