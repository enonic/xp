module api.content.site {

    export class CreateSiteRequest extends SiteResourceRequest<api.content.json.ContentJson, api.content.Content> {

        private contentId: string;
        private siteTemplateKey: api.content.site.template.SiteTemplateKey;
        private moduleConfigs: ModuleConfig[];

        constructor(contentId: string) {
            super();
            super.setMethod("POST");
            this.contentId = contentId;
        }

        setSiteTemplateKey(siteTemplateKey: api.content.site.template.SiteTemplateKey): CreateSiteRequest {
            this.siteTemplateKey = siteTemplateKey;
            return this;
        }

        setModuleConfigs(moduleConfigs: ModuleConfig[]): CreateSiteRequest {
            this.moduleConfigs = moduleConfigs;
            return this;
        }

        getParams(): Object {
            var moduleConfigsJson = [];
            for (var i = 0; i < this.moduleConfigs.length; i++) {
                moduleConfigsJson.push(this.moduleConfigs[i].toJson());
            }
            return {
                contentId: this.contentId,
                siteTemplateKey: this.siteTemplateKey.toString(),
                moduleConfigs: moduleConfigsJson
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "create");
        }

        sendAndParse(): Q.Promise<api.content.Content> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                return response.isBlank() ? null : this.fromJsonToContent(response.getResult());
            });
        }
    }
}