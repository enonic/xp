module api.content {

    export class ContentDuplicatedEvent extends api.event.Event {

        private source: ContentSummary;
        private content: Content;
        private nextToSource: boolean;

        constructor(content: Content, source: ContentSummary, nextToSource: boolean = true) {
            super();
            this.content = content;
            this.source = source;
            this.nextToSource = nextToSource;
        }

        getSource(): ContentSummary {
            return this.source;
        }

        getContent(): Content {
            return this.content;
        }

        isNextToSource(): boolean {
            return this.nextToSource;
        }

        static on(handler: (event: ContentDuplicatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentDuplicatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

}
