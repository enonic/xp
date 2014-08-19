module api.schema.content {

    export class ContentTypeCache {

        private static instance: ContentTypeCache;

        private objectsTypesByName: {[s:string] : ContentType;} = {};

        public put(object: ContentType) {
            this.objectsTypesByName[object.getContentTypeName().toString()] = object;
        }

        public getByName(name: ContentTypeName): ContentType {
            //return this.objectsTypesByName[name.toString()];
            return null;
        }

        static get(): ContentTypeCache {
            if (!ContentTypeCache.instance) {
                ContentTypeCache.instance = new ContentTypeCache();
            }
            return ContentTypeCache.instance;
        }
    }
}