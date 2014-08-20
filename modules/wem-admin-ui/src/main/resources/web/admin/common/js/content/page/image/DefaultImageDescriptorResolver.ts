module api.content.page.image {

    import ModuleKey = api.module.ModuleKey;

    export class DefaultImageDescriptorResolver {

        static resolve(moduleKeys: ModuleKey[]) : wemQ.Promise<ImageDescriptor> {

            return new GetImageDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().then((descriptors: ImageDescriptor[]) => {
                    return descriptors[0];
                });
        }
    }
}