module api.schema.content {

    export class ContentTypeCache extends api.cache.Cache<ContentType,ContentTypeName> {

        private static instance: ContentTypeCache;

        constructor() {
            super();

            ContentTypeUpdatedEvent.on((event: ContentTypeUpdatedEvent) => {

                console.log("ContentTypeCache on ContentTypeUpdatedEvent: " + event.getContentTypeName().toString());
                var cachedObject = this.getByKey(event.getContentTypeName());
                if (cachedObject) {
                    var cachedModifiedTimeAsMillis = cachedObject.getModifiedTime().getTime();
                    var serverUpdateModifiedTimeAsMillis = event.getModifiedTime().getTime();
                    console.log("cachedObject.getModifiedTime().toUTCString(): " + cachedObject.getModifiedTime().toUTCString());
                    console.log("event.getModifiedTime().toUTCString():  " + event.getModifiedTime().toUTCString());
                    if (cachedModifiedTimeAsMillis != serverUpdateModifiedTimeAsMillis) {
                        console.log("  updated ContentType modified after cached ContentType - removing object from cache");
                        console.log("  cachedObject.getModifiedTime(): " + cachedObject.getModifiedTime());
                        console.log("  event: " + event.getModifiedTime());
                        this.deleteByKey(event.getContentTypeName());
                    }
                    else {
                        console.log("  updated ContentType already in cache!");
                        // Not currently true so we delete anyway to avoid trouble
                        this.deleteByKey(event.getContentTypeName());
                    }
                }

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