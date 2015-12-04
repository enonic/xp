module api.content {
    export class ContentMovedEvent extends api.event.Event {

        private names: string[];

        constructor(contentIds: string[]) {
            super();
            this.names = contentIds;
        }

        public getNames(): string[] {
            return this.names;
        }

        static on(handler: (event: ContentMovedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentMovedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}