module api.content.page {

    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;
    import ApplicationCaches = api.application.ApplicationCaches;
    import ApplicationBasedCache = api.application.ApplicationBasedCache;

    export class PageDescriptorCache extends ApplicationBasedCache<PageDescriptorApplicationCache,PageDescriptor,DescriptorKey> {

        private static instance: PageDescriptorCache;

        static get(): PageDescriptorCache {

            var w = api.dom.WindowDOM.get();
            var topWindow: any = w.getTopParent().asWindow();

            if (!topWindow.api.content.page.PageDescriptorCache.instance) {
                topWindow.api.content.page.PageDescriptorCache.instance = new PageDescriptorCache();
            }
            return topWindow.api.content.page.PageDescriptorCache.instance;
        }

        constructor() {
            if (PageDescriptorCache.instance) {
                throw new Error("Instantiation failed: Use PageDescriptorCache.get() instead!");
            }
            super();
        }

        loadByApplication(applicationKey: ApplicationKey) {
            new GetPageDescriptorsByApplicationRequest(applicationKey).sendAndParse().catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }

        put(descriptor: PageDescriptor) {
            api.util.assertNotNull(descriptor, "a PageDescriptor must be given");

            super.put(descriptor, descriptor.getKey().getApplicationKey());
        }

        getByKey(key: DescriptorKey): PageDescriptor {
            return super.getByKey(key, key.getApplicationKey());
        }

        createApplicationCache(): PageDescriptorApplicationCache {
            return new PageDescriptorApplicationCache();
        }
    }

    export class PageDescriptorApplicationCache extends api.cache.Cache<PageDescriptor, DescriptorKey> {

        copy(object: PageDescriptor): PageDescriptor {
            return object.clone();
        }

        getKeyFromObject(object: PageDescriptor): DescriptorKey {
            return object.getKey();
        }

        getKeyAsString(key: DescriptorKey): string {
            return key.toString();
        }
    }
}
