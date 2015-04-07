module api.schema.content {

    import ModuleUpdatedEvent = api.module.ModuleUpdatedEvent;
    import ModuleUpdatedEventType = api.module.ModuleUpdatedEventType;

    export class ContentTypeCache extends api.cache.Cache<ContentType,ContentTypeName> {

        private static instance: ContentTypeCache;

        constructor() {
            super();
            ModuleUpdatedEvent.on((event: ModuleUpdatedEvent) => {
                if (ModuleUpdatedEventType.STARTED == event.getEventType()
                    || ModuleUpdatedEventType.STOPPED == event.getEventType()
                        || ModuleUpdatedEventType.UPDATED == event.getEventType()) {
                    console.log(api.ClassHelper.getClassName(this) + " received ModuleUpdatedEvent - removing cached content types... " +
                                event.getModuleKey().toString());
                    this.getCachedByModuleKey(event.getModuleKey()).forEach((contentType: ContentType) => {
                        this.deleteByKey(this.getKeyFromObject(contentType));
                        console.log("Removed cached content type: " + contentType.getName());
                    });
                }
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

        private getCachedByModuleKey(moduleKey: api.module.ModuleKey): ContentType[] {
            var result: ContentType[] = [];
            this.getAll().forEach((contentType: ContentType) => {
                if(moduleKey.equals(this.getKeyFromObject(contentType).getModuleKey())) {
                    result.push(contentType);
                }
            });
            return result;
        }

        static get(): ContentTypeCache {
            if (!ContentTypeCache.instance) {
                ContentTypeCache.instance = new ContentTypeCache();
            }
            return ContentTypeCache.instance;
        }
    }
}