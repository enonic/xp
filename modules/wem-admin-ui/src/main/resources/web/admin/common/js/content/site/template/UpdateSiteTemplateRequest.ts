module api.content.site.template {

    export class UpdateSiteTemplateRequest extends SiteTemplateResourceRequest<SiteTemplateJson, SiteTemplate> {

        private siteTemplateKey: SiteTemplateKey;
        private displayName: string;
        private description: string;
        private url: string;
        private vendor: api.content.site.Vendor;
        private moduleKeys: api.module.ModuleKey[];
        private contentTypeFilter: api.schema.content.ContentTypeFilter;

        constructor() {
            super();
            super.setMethod("POST");
        }

        setSiteTemplateKey(siteTemplateKey: SiteTemplateKey): UpdateSiteTemplateRequest {
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

        getParams(): Object {
            return {
                siteTemplateKey: this.siteTemplateKey.toString(),
                displayName: this.displayName,
                description: this.description,
                url: this.url,
                vendor: this.vendor.toJson(),
                moduleKeys: api.module.ModuleKey.toStringArray(this.moduleKeys),
                contentTypeFilter: this.contentTypeFilter.toJson()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "update");
        }

        sendAndParse(): Q.Promise<SiteTemplate> {

            return this.send().then((response: api.rest.JsonResponse<SiteTemplateJson>) => {
                return this.fromJsonToSiteTemplate(response.getResult());
            });
        }
    }
}