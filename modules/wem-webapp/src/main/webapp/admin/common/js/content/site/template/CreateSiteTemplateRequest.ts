module api.content.site.template {

    export class CreateSiteTemplateRequest extends SiteTemplateResourceRequest<api.content.site.template.json.SiteTemplateJson> {

        private displayName: string;
        private description: string;
        private url: string;
        private vendor: api.content.site.Vendor;
        private moduleKeys: api.module.ModuleKey[];
        private contentTypeFilter: api.schema.content.ContentTypeFilter;
        private rootContentType: api.schema.content.ContentTypeName;

        constructor() {
            super();
            super.setMethod("POST");
        }

        setDisplayName(displayName: string): CreateSiteTemplateRequest {
            this.displayName = displayName;
            return this;
        }

        setDescription(description: string): CreateSiteTemplateRequest {
            this.description = description;
            return this;
        }

        setUrl(url: string): CreateSiteTemplateRequest {
            this.url = url;
            return this;
        }

        setVendor(vendor: api.content.site.Vendor): CreateSiteTemplateRequest {
            this.vendor = vendor;
            return this;
        }

        setModuleKeys(moduleKeys: api.module.ModuleKey[]): CreateSiteTemplateRequest {
            this.moduleKeys = moduleKeys;
            return this;
        }

        setContentTypeFilter(contentTypeFilter: api.schema.content.ContentTypeFilter): CreateSiteTemplateRequest {
            this.contentTypeFilter = contentTypeFilter;
            return this;
        }

        setRootContentType(rootContentType: api.schema.content.ContentTypeName): CreateSiteTemplateRequest {
            this.rootContentType = rootContentType;
            return this;
        }

        getParams(): Object {
            return {
                displayName: this.displayName,
                description: this.description,
                url: this.url,
                vendor: this.vendor.toJson(),
                moduleKeys: api.module.ModuleKey.toStringArray(this.moduleKeys),
                contentTypeFilter: this.contentTypeFilter.toJson(),
                rootContentType: this.rootContentType.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "create");
        }

        sendAndParse(): Q.Promise<api.content.site.template.SiteTemplate> {

            var deferred = Q.defer<api.content.site.template.SiteTemplate>();

            this.send().
                then((response: api.rest.JsonResponse<api.content.site.template.json.SiteTemplateJson>) => {
                    deferred.resolve(this.fromJsonToSiteTemplate(response.getResult()));
                }).catch((response: api.rest.RequestError) => {
                    deferred.reject(null);
                }).done();

            return deferred.promise;
        }
    }
}