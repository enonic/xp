module api.schema.content {

    export class ContentTypeUpdatedEvent extends api.event.Event {

        private name: ContentTypeName;

        constructor(name: ContentTypeName) {
            super();
            this.name = name;
        }

        getContentTypeName(): ContentTypeName {
            return this.name;
        }

        static on(handler: (event: ContentTypeUpdatedEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ContentTypeUpdatedEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }


        static fromJson(json: ContentTypeUpdatedEventJson): ContentTypeUpdatedEvent {
            return new ContentTypeUpdatedEvent(new ContentTypeName(json.name));
        }
    }

    export interface ContentTypeUpdatedEventJson {
        name: string;
    }
}