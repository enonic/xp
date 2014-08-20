module api.schema.content {

    export class ContentTypeUpdatedEvent extends api.event.Event {

        private name: ContentTypeName;

        private modifiedTime: Date;

        constructor(name: ContentTypeName, modifiedTime: Date) {
            super();
            this.name = name;
            this.modifiedTime = modifiedTime;
        }

        getContentTypeName(): ContentTypeName {
            return this.name;
        }

        getModifiedTime(): Date {
            return this.modifiedTime;
        }

        static on(handler: (event: ContentTypeUpdatedEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ContentTypeUpdatedEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }


        static fromJson(json: ContentTypeUpdatedEventJson): ContentTypeUpdatedEvent {
            return new ContentTypeUpdatedEvent(new ContentTypeName(json.name), json.modifiedTime);
        }
    }

    export interface ContentTypeUpdatedEventJson {

        name: string;

        modifiedTime: Date;
    }
}