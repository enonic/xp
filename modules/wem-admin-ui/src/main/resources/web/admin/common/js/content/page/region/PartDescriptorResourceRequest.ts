module api.content.page.region {

    export class PartDescriptorResourceRequest<JSON_TYPE, PARSED_TYPE> extends api.rest.ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: api.rest.Path;

        cache: PartDescriptorCache;

        constructor() {
            super();
            this.cache = PartDescriptorCache.get();
            this.resourcePath = api.rest.Path.fromParent(super.getRestPath(), "content", "page", "part", "descriptor");
        }

        getResourcePath(): api.rest.Path {
            return this.resourcePath;
        }

        fromJsonToPartDescriptor(json: PartDescriptorJson): PartDescriptor {
            var partDescriptor = new PartDescriptorBuilder().fromJson(json).build();
            this.cache.put(partDescriptor);
            return  partDescriptor;
        }
    }
}