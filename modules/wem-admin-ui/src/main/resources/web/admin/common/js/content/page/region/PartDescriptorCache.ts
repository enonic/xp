module api.content.page.region {

    import Module = api.module.Module;
    import ModuleKey = api.module.ModuleKey;
    import ModuleCaches = api.module.ModuleCaches;
    import ModuleBasedCache = api.module.ModuleBasedCache;
    import DescriptorKey = api.content.page.DescriptorKey;

    export class PartDescriptorCache extends ModuleBasedCache<PartDescriptorModuleCache,PartDescriptor,DescriptorKey> {

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

        loadByModule(moduleKey: ModuleKey) {
            new GetPartDescriptorsByModuleRequest(moduleKey).sendAndParse().catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }

        put(descriptor: PartDescriptor) {
            api.util.assertNotNull(descriptor, "a PartDescriptor must be given");

            super.put(descriptor, descriptor.getKey().getModuleKey());
        }

        getByKey(key: DescriptorKey): PartDescriptor {
            return super.getByKey(key, key.getModuleKey());
        }

        createModuleCache(): PartDescriptorModuleCache {
            return new PartDescriptorModuleCache();
        }
    }

    export class PartDescriptorModuleCache extends api.cache.Cache<PartDescriptor, DescriptorKey> {

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
