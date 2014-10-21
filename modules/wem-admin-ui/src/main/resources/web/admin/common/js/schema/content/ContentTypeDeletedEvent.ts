module api.schema.content {

    export class ContentTypeDeletedEvent extends api.event.Event {

        private contentTypeName: ContentTypeName;

        constructor(name: ContentTypeName) {
            super();
            this.contentTypeName = name;
        }

        getContentTypeName(): ContentTypeName {
            return this.contentTypeName;
        }

        static on(handler: (event: ContentTypeDeletedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentTypeDeletedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }


        static fromJson(json: ContentTypeDeletedEventJson): ContentTypeDeletedEvent {
            return new ContentTypeDeletedEvent(new ContentTypeName(json.name));
        }
    }

    export interface ContentTypeDeletedEventJson {
        name: string;
    }
}