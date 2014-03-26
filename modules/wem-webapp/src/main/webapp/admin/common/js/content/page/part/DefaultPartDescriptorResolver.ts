module api.content.page.part {

    import ModuleKey = api.module.ModuleKey;

    export class DefaultPartDescriptorResolver {

        static resolve(moduleKeys: ModuleKey[]) : Q.Promise<PartDescriptor> {

            var d = Q.defer<PartDescriptor>();
            new GetPartDescriptorsByModulesRequest(moduleKeys).
                sendAndParse().done((descriptors: PartDescriptor[]) => {
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