module api.schema.content {

    export class ContentTypesDeletedEvent extends api.event.Event {

        private contentTypeNames: ContentTypeName[];

        constructor(names: string[]) {
            super();
            this.contentTypeNames = names.map((name: string) => {
                return new ContentTypeName(name);
            });
        }

        getContentTypeNames(): ContentTypeName[] {
            return this.contentTypeNames;
        }

        static on(handler: (event: ContentTypesDeletedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentTypesDeletedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }


        static fromJson(json: ContentTypeDeletedEventJson): ContentTypesDeletedEvent {
            return new ContentTypesDeletedEvent(json.names);
        }
    }

    export interface ContentTypeDeletedEventJson {
        names: string[];
    }
}