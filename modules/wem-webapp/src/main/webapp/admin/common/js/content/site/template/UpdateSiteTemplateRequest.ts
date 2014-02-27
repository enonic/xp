module api.content.site.template {

    export class UpdateSiteTemplateRequest extends SiteTemplateResourceRequest<api.content.site.template.json.SiteTemplateJson> {

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;
        private displayName: string;
        private description: string;
        private url: string;
        private vendor: api.content.site.Vendor;
        private moduleKeys: api.module.ModuleKey[];
        private contentTypeFilter: api.schema.content.ContentTypeFilter;
        private rootContentType: api.schema.content.ContentTypeName;

        constructor(siteTemplateKey: api.content.site.template.SiteTemplateKey,
                    displayName:string,
                    description:string,
                    url:string,
                    vendor: api.content.site.Vendor,
                    moduleKeys: api.module.ModuleKey[],
                    contentTypeFilter: api.schema.content.ContentTypeFilter,
                    rootContentType: api.schema.content.ContentTypeName) {

            super();
            super.setMethod("POST");

            this.siteTemplateKey = siteTemplateKey;
            this.displayName = displayName;
            this.description = description;
            this.url = url;
            this.vendor = vendor;
            this.moduleKeys = moduleKeys;
            this.contentTypeFilter = contentTypeFilter;
            this.rootContentType = rootContentType;
        }

        setSiteTemplateKey(siteTemplateKey: api.content.site.template.SiteTemplateKey): UpdateSiteTemplateRequest {
            this.siteTemplateKey = siteTemplateKey;
            return this;
        }

        setDisplayName(displayName: string): UpdateSiteTemplateRequest {
            this.displayName = displayName;
            return this;
        }

        setDescription(description: string): UpdateSiteTemplateRequest {
            this.description = description;
            return this;
        }

        setUrl(url: string): UpdateSiteTemplateRequest {
            this.url = url;
            return this;
        }

        setVendor(vendor: api.content.site.Vendor): UpdateSiteTemplateRequest {
            this.vendor = vendor;
            return this;
        }

        setModuleKeys(moduleKeys: api.module.ModuleKey[]): UpdateSiteTemplateRequest {
            this.moduleKeys = moduleKeys;
            return this;
        }

        setContentTypeFilter(contentTypeFilter: api.schema.content.ContentTypeFilter): UpdateSiteTemplateRequest {
            this.contentTypeFilter = contentTypeFilter;
            return this;
        }

        setRootContentType(rootContentType: api.schema.content.ContentTypeName): UpdateSiteTemplateRequest {
            this.rootContentType = rootContentType;
            return this;
        }

        getParams(): Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString(),
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
            return api.rest.Path.fromParent(super.getResourcePath(), "update");
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