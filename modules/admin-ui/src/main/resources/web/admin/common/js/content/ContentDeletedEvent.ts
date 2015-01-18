module api.content {

    export class ContentDeletedEvent extends api.event.Event {

        private contents: ContentSummary[];

        constructor(contents: ContentSummary[]) {
            super();
            this.contents = contents;
        }

        public getContents(): ContentSummary[] {
            return this.contents;
        }

        static on(handler: (event: ContentDeletedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentDeletedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}