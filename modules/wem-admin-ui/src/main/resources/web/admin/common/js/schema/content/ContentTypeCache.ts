module api.schema.content {

    export class ContentTypeCache {

        private static instance: ContentTypeCache;

        private objectsTypesByName: {[s:string] : ContentType;} = {};

        constructor() {
            ContentTypeUpdatedEvent.on((event: ContentTypeUpdatedEvent) => {
                console.log("ContentTypeCache on ContentTypeUpdatedEvent, deleting: " + event.getContentTypeName().toString());
                delete this.objectsTypesByName[event.getContentTypeName().toString()];
            })
        }

        public put(object: ContentType) {
            console.log("ContentTypeCache.put: " + object.getName());
            this.objectsTypesByName[object.getContentTypeName().toString()] = object;
        }

        public getByName(name: ContentTypeName): ContentType {
            console.log("ContentTypeCache.getByName: " + name.toString());
            return this.objectsTypesByName[name.toString()];
        }

        static get(): ContentTypeCache {
            if (!ContentTypeCache.instance) {
                ContentTypeCache.instance = new ContentTypeCache();
            }
            return ContentTypeCache.instance;
        }
    }
}