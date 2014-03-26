module api.content.page.layout {

    import ModuleKey = api.module.ModuleKey;

    export class DefaultLayoutDescriptorResolver {

        static resolve(moduleKeys: ModuleKey[]) : Q.Promise<LayoutDescriptor> {

            var d = Q.defer<LayoutDescriptor>();
            new GetLayoutDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().done((descriptors: LayoutDescriptor[]) => {
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