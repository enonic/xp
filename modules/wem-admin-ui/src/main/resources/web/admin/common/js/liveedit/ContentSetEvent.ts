module api.liveedit {

    import Content = api.content.Content;

    export class ContentSetEvent extends api.event.Event2 {

        private content: Content;

        constructor(content: Content) {
            super();
            this.content = content;
        }

        getContent(): Content {
            return this.content;
        }

        static on(handler: (event: ContentSetEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ContentSetEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}