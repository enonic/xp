module api.content.page {

    import Module = api.module.Module;
    import ModuleKey = api.module.ModuleKey;
    import ModuleCaches = api.module.ModuleCaches;
    import ModuleBasedCache = api.module.ModuleBasedCache;

    export class PageDescriptorCache extends ModuleBasedCache<PageDescriptorModuleCache,PageDescriptor,DescriptorKey> {

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

        loadByModule(moduleKey: ModuleKey) {
            new GetPageDescriptorsByModuleRequest(moduleKey).sendAndParse().catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }

        put(descriptor: PageDescriptor) {
            api.util.assertNotNull(descriptor, "a PageDescriptor must be given");

            super.put(descriptor, descriptor.getKey().getModuleKey());
        }

        getByKey(key: DescriptorKey): PageDescriptor {
            return super.getByKey(key, key.getModuleKey());
        }

        createModuleCache(): PageDescriptorModuleCache {
            return new PageDescriptorModuleCache();
        }
    }

    export class PageDescriptorModuleCache extends api.cache.Cache<PageDescriptor, DescriptorKey> {

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
