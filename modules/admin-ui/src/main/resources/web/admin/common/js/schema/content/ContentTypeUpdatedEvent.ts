module api.schema.content {

    export class ContentTypeUpdatedEvent extends api.event.Event {

        private contentTypeName: ContentTypeName;

        private modifiedTime: Date;

        constructor(name: ContentTypeName, modifiedTime: Date) {
            super();
            this.contentTypeName = name;
            this.modifiedTime = modifiedTime;
        }

        getContentTypeName(): ContentTypeName {
            return this.contentTypeName;
        }

        getModifiedTime(): Date {
            return this.modifiedTime;
        }

        static on(handler: (event: ContentTypeUpdatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentTypeUpdatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }


        static fromJson(json: ContentTypeUpdatedEventJson): ContentTypeUpdatedEvent {
            var modifiedTime = json.modifiedTime ? new Date(Date.parse(json.modifiedTime)) : null;
            return new ContentTypeUpdatedEvent(new ContentTypeName(json.name), modifiedTime);
        }
    }

    export interface ContentTypeUpdatedEventJson {

        name: string;

        modifiedTime: string;
    }
}