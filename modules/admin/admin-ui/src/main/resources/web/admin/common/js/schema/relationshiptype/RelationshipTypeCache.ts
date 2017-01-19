module api.schema.relationshiptype {

    import ApplicationEvent = api.application.ApplicationEvent;
    import ApplicationEventType = api.application.ApplicationEventType;

    export class RelationshipTypeCache extends api.cache.Cache<RelationshipType,RelationshipTypeName> {

        private static instance: RelationshipTypeCache;

        constructor() {
            super();
            ApplicationEvent.on((event: ApplicationEvent) => {
                if (ApplicationEventType.STARTED === event.getEventType()
                    || ApplicationEventType.STOPPED === event.getEventType()
                    || ApplicationEventType.UPDATED === event.getEventType()) {
                    console.log(api.ClassHelper.getClassName(this) + ' received ApplicationEvent - removing cached content types... ' +
                                event.getApplicationKey().toString());
                    this.getCachedByApplicationKey(event.getApplicationKey()).forEach((relationshipType: RelationshipType) => {
                        this.deleteByKey(this.getKeyFromObject(relationshipType));
                        console.log('Removed cached content type: ' + relationshipType.getName());
                    });
                }
            });
        }

        copy(object: RelationshipType): RelationshipType {
            return new RelationshipTypeBuilder(object).build();
        }

        getKeyFromObject(object: RelationshipType): RelationshipTypeName {
            return new RelationshipTypeName(object.getName());
        }

        getKeyAsString(key: RelationshipTypeName): string {
            return key.toString();
        }

        private getCachedByApplicationKey(applicationKey: api.application.ApplicationKey): RelationshipType[] {
            let result: RelationshipType[] = [];
            this.getAll().forEach((relationshipType: RelationshipType) => {
                if (applicationKey.equals(this.getKeyFromObject(relationshipType).getApplicationKey())) {
                    result.push(relationshipType);
                }
            });
            return result;
        }

        static get(): RelationshipTypeCache {
            if (!RelationshipTypeCache.instance) {
                RelationshipTypeCache.instance = new RelationshipTypeCache();
            }
            return RelationshipTypeCache.instance;
        }
    }
}
