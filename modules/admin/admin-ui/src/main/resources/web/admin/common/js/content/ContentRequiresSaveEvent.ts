module api.content {

    import Content = api.content.Content;

    export class ContentRequiresSaveEvent extends api.event.Event {

        private content: Content;

        constructor(content: Content) {
            super();
            this.content = content;
        }

        getContent(): Content {
            return this.content;
        }

        static on(handler: (event: ContentRequiresSaveEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentRequiresSaveEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}