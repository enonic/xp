module api.content.page.region {

    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;
    import ApplicationCaches = api.application.ApplicationCaches;
    import ApplicationBasedCache = api.application.ApplicationBasedCache;
    import DescriptorKey = api.content.page.DescriptorKey;

    export class PartDescriptorCache extends ApplicationBasedCache<PartDescriptorApplicationCache,PartDescriptor,DescriptorKey> {

        private static instance: PartDescriptorCache;

        static get(): PartDescriptorCache {

            var w = api.dom.WindowDOM.get();
            var topWindow: any = w.getTopParent().asWindow();

            if (!topWindow.api.content.page.region.PartDescriptorCache.instance) {
                topWindow.api.content.page.region.PartDescriptorCache.instance = new PartDescriptorCache();
            }
            return topWindow.api.content.page.region.PartDescriptorCache.instance;
        }

        constructor() {
            if (PartDescriptorCache.instance) {
                throw new Error("Instantiation failed: Use PartDescriptorCache.get() instead!");
            }
            super();
        }

        loadByApplication(applicationKey: ApplicationKey) {
            new GetPartDescriptorsByApplicationRequest(applicationKey).sendAndParse().catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }

        put(descriptor: PartDescriptor) {
            api.util.assertNotNull(descriptor, "a PartDescriptor must be given");

            super.put(descriptor, descriptor.getKey().getApplicationKey());
        }

        getByKey(key: DescriptorKey): PartDescriptor {
            return super.getByKey(key, key.getApplicationKey());
        }

        createApplicationCache(): PartDescriptorApplicationCache {
            return new PartDescriptorApplicationCache();
        }
    }

    export class PartDescriptorApplicationCache extends api.cache.Cache<PartDescriptor, DescriptorKey> {

        copy(object: PartDescriptor): PartDescriptor {
            return object.clone();
        }

        getKeyFromObject(object: PartDescriptor): DescriptorKey {
            return object.getKey();
        }

        getKeyAsString(key: DescriptorKey): string {
            return key.toString();
        }
    }
}
