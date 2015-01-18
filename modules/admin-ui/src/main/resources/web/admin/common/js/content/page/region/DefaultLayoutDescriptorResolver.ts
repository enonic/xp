module api.content.page.region {

    import ModuleKey = api.module.ModuleKey;

    export class DefaultLayoutDescriptorResolver {

        static resolve(moduleKeys: ModuleKey[]) : wemQ.Promise<LayoutDescriptor> {

            return new GetLayoutDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().then((descriptors: LayoutDescriptor[]) => {
                    return descriptors[0];
                });
        }
    }
}