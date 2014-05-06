module api.content.page.layout {

    import ModuleKey = api.module.ModuleKey;

    export class DefaultLayoutDescriptorResolver {

        static resolve(moduleKeys: ModuleKey[]) : Q.Promise<LayoutDescriptor> {

            return new GetLayoutDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().then((descriptors: LayoutDescriptor[]) => {
                    return descriptors[0];
                });
        }
    }
}