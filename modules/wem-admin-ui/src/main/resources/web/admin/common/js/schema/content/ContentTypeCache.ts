module api.schema.content {

    export class ContentTypeCache extends api.cache.Cache<ContentType,ContentTypeName> {

        private static instance: ContentTypeCache;

        constructor() {
            super();

            ContentTypeUpdatedEvent.on((event: ContentTypeUpdatedEvent) => {
                /*if (event.getContentTypeName()) {
                 console.log("ContentTypeCache on ContentTypeUpdatedEvent, deleting: " + event.getContentTypeName().toString());
                 // TODO: Do not delete if cache already contains updated object
                 var cachedObject = this.getByKey(event.getContentTypeName());
                 if (cachedObject) {
                 console.log("cachedObject.getModifiedTime(): " + cachedObject.getModifiedTime());
                 console.log("event: " + event.getModifiedTime());
                 }
                 this.deleteByKey(event.getContentTypeName());
                 }
                 else {
                 console.log(event);
                 }*/
            });
            ContentTypeDeletedEvent.on((event: ContentTypeDeletedEvent) => {
                console.log("ContentTypeCache on ContentTypeDeletedEvent, deleting: " + event.getContentTypeName().toString());
                this.deleteByKey(event.getContentTypeName());
            });
        }

        copy(object: ContentType): ContentType {
            return new ContentTypeBuilder(object).build();
        }

        getKeyFromObject(object: ContentType): ContentTypeName {
            return new ContentTypeName(object.getName());
        }

        getKeyAsString(key: ContentTypeName): string {
            return key.toString();
        }

        static get(): ContentTypeCache {
            if (!ContentTypeCache.instance) {
                ContentTypeCache.instance = new ContentTypeCache();
            }
            return ContentTypeCache.instance;
        }
    }
}