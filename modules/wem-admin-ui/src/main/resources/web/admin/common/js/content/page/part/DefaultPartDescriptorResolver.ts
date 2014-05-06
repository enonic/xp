module api.content.page.part {

    import ModuleKey = api.module.ModuleKey;

    export class DefaultPartDescriptorResolver {

        static resolve(moduleKeys: ModuleKey[]) : Q.Promise<PartDescriptor> {

            return new GetPartDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().then((descriptors: PartDescriptor[]) => {
                    return descriptors[0];
                });
        }
    }
}