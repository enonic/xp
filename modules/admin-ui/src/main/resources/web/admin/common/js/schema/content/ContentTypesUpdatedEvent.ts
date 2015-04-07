module api.schema.content {

    export class ContentTypesUpdatedEvent extends api.event.Event {

        private contentTypeNames: ContentTypeName[];

        private modifiedTime: Date;

        constructor(names: string[], modifiedTime: Date) {
            super();
            this.contentTypeNames = names.map((name: string) => {
                return new ContentTypeName(name);
            });
            this.modifiedTime = modifiedTime;
        }

        getContentTypeNames(): ContentTypeName[] {
            return this.contentTypeNames;
        }

        getModifiedTime(): Date {
            return this.modifiedTime;
        }

        static on(handler: (event: ContentTypesUpdatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentTypesUpdatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }


        static fromJson(json: ContentTypeUpdatedEventJson): ContentTypesUpdatedEvent {
            var modifiedTime = json.modifiedTime ? new Date(Date.parse(json.modifiedTime)) : null;
            return new ContentTypesUpdatedEvent(json.names, modifiedTime);
        }
    }

    export interface ContentTypeUpdatedEventJson {

        names: string[];

        modifiedTime: string;
    }
}