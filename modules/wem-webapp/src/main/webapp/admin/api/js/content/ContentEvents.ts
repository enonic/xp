module api_content {

    export class ContentDeletedEvent extends api_event.Event {

        private contents:ContentSummary[];

        constructor(contents:ContentSummary[]) {
            super("ContentDeletedEvent");
            this.contents = contents;
        }

        public getContents():ContentSummary[] {
            return this.contents;
        }

        static on(handler:(event:ContentDeletedEvent) => void) {
            api_event.onEvent('ContentDeletedEvent', handler);
        }

    }

    export class ContentCreatedEvent extends api_event.Event {

        private path:api_content.ContentPath;

        constructor(path:api_content.ContentPath) {
            super('ContentCreatedEvent');
            this.path = path;
        }

        public getPath():api_content.ContentPath {
            return this.path;
        }

        static on(handler:(event:api_content.ContentCreatedEvent) => void) {
            api_event.onEvent('ContentCreatedEvent', handler);
        }
    }

    export class ContentUpdatedEvent extends api_event.Event {

        private model:api_content.ContentSummary;

        constructor(model:api_content.ContentSummary) {
            super('ContentUpdatedEvent');
            this.model = model;
        }

        public getModel():api_content.ContentSummary {
            return this.model;
        }

        static on(handler:(event:api_content.ContentUpdatedEvent) => void) {
            api_event.onEvent('ContentUpdatedEvent', handler);
        }
    }

}