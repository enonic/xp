module api.schema.content {

    import ApplicationUpdatedEvent = api.application.ApplicationUpdatedEvent;
    import ApplicationUpdatedEventType = api.application.ApplicationUpdatedEventType;

    export class ContentTypeCache extends api.cache.Cache<ContentType,ContentTypeName> {

        private static instance: ContentTypeCache;

        constructor() {
            super();
            ApplicationUpdatedEvent.on((event: ApplicationUpdatedEvent) => {
                if (ApplicationUpdatedEventType.STARTED == event.getEventType()
                    || ApplicationUpdatedEventType.STOPPED == event.getEventType()
                        || ApplicationUpdatedEventType.UPDATED == event.getEventType()) {
                    console.log(api.ClassHelper.getClassName(this) + " received ApplicationUpdatedEvent - removing cached content types... " +
                                event.getApplicationKey().toString());
                    this.getCachedByApplicationKey(event.getApplicationKey()).forEach((contentType: ContentType) => {
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

        private getCachedByApplicationKey(applicationKey: api.application.ApplicationKey): ContentType[] {
            var result: ContentType[] = [];
            this.getAll().forEach((contentType: ContentType) => {
                if(applicationKey.equals(this.getKeyFromObject(contentType).getApplicationKey())) {
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