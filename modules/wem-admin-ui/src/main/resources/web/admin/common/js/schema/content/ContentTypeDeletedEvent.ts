module api.schema.content {

    export class ContentTypeDeletedEvent extends api.event.Event {

        private name: ContentTypeName;

        constructor(name: ContentTypeName) {
            super();
            this.name = name;
        }

        getContentTypeName(): ContentTypeName {
            return this.name;
        }

        static on(handler: (event: ContentTypeDeletedEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ContentTypeDeletedEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }


        static fromJson(json: ContentTypeDeletedEventJson): ContentTypeDeletedEvent {
            return new ContentTypeDeletedEvent(new ContentTypeName(json.name));
        }
    }

    export interface ContentTypeDeletedEventJson {
        name: string;
    }
}