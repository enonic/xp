module api.content.page.region {

    import Module = api.module.Module;
    import ApplicationKey = api.module.ApplicationKey;
    import ModuleCaches = api.module.ModuleCaches;
    import ModuleBasedCache = api.module.ModuleBasedCache;
    import DescriptorKey = api.content.page.DescriptorKey;

    export class LayoutDescriptorCache extends ModuleBasedCache<LayoutDescriptorModuleCache,LayoutDescriptor,DescriptorKey> {

        private static instance: LayoutDescriptorCache;

        static get(): LayoutDescriptorCache {

            var w = api.dom.WindowDOM.get();
            var topWindow: any = w.getTopParent().asWindow();

            if (!topWindow.api.content.page.region.LayoutDescriptorCache.instance) {
                topWindow.api.content.page.region.LayoutDescriptorCache.instance = new LayoutDescriptorCache();
            }
            return topWindow.api.content.page.region.LayoutDescriptorCache.instance;
        }

        constructor() {
            if (LayoutDescriptorCache.instance) {
                throw new Error("Instantiation failed: Use LayoutDescriptorCache.get() instead!");
            }
            super();
        }

        loadByModule(applicationKey: ApplicationKey) {
            new GetLayoutDescriptorsByModuleRequest(applicationKey).sendAndParse().catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }

        put(descriptor: LayoutDescriptor) {
            api.util.assertNotNull(descriptor, "a LayoutDescriptor must be given");

            super.put(descriptor, descriptor.getKey().getApplicationKey());
        }

        getByKey(key: DescriptorKey): LayoutDescriptor {
            return super.getByKey(key, key.getApplicationKey());
        }

        createModuleCache(): LayoutDescriptorModuleCache {
            return new LayoutDescriptorModuleCache();
        }
    }

    export class LayoutDescriptorModuleCache extends api.cache.Cache<LayoutDescriptor, DescriptorKey> {

        copy(object: LayoutDescriptor): LayoutDescriptor {
            return object.clone();
        }

        getKeyFromObject(object: LayoutDescriptor): DescriptorKey {
            return object.getKey();
        }

        getKeyAsString(key: DescriptorKey): string {
            return key.toString();
        }
    }
}
