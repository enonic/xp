module api.content {

    export class ContentDeletedEvent extends api.event.Event2 {

        private contents:ContentSummary[];

        constructor( contents:ContentSummary[] ) {
            super();
            this.contents = contents;
        }

        public getContents():ContentSummary[] {
            return this.contents;
        }

        static on(handler: (event: ContentDeletedEvent) => void) {
            api.event.Event2.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ContentDeletedEvent) => void) {
            api.event.Event2.unbind(api.util.getFullName(this), handler);
        }
    }
}