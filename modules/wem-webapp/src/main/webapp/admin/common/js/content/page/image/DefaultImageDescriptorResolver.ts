module api.content.page.image {

    import ModuleKey = api.module.ModuleKey;

    export class DefaultImageDescriptorResolver {

        static resolve(moduleKeys: ModuleKey[]) : Q.Promise<ImageDescriptor> {

            var d = Q.defer<ImageDescriptor>();
            new GetImageDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().done((descriptors: ImageDescriptor[]) => {
                    if (descriptors.length == 0) {
                        d.resolve(null);
                    }
                    else {
                        d.resolve(descriptors[0]);
                    }
                });
            return d.promise;
        }
    }
}