module api_content_site_template {

    export class SiteTemplateSummary extends api_item.BaseItem {

        private name: string;

        private displayName: string;

        private vendor: api_content_site.Vendor;

        private modules: api_module.ModuleKey[] = [];

        private supportedContentTypes: string[];

        private rootContentType: api_schema_content.ContentTypeName;

        private version: string;

        private url: string;

        private key: SiteTemplateKey;

        private description: string;

        constructor(json: api_content_site_template_json.SiteTemplateSummaryJson) {
            super(json);
            this.name = json.name;
            this.displayName = json.name;
            this.vendor = new api_content_site.Vendor(json.vendor);
            for (var i = 0; i < json.modules.length; i++) {
                this.modules.push(api_module.ModuleKey.fromString(json.modules[i]));
            }
            this.supportedContentTypes = json.supportedContentTypes;
            this.rootContentType = new api_schema_content.ContentTypeName(json.rootContentType);
            this.version = json.version;
            this.url = json.url;
            this.key = SiteTemplateKey.fromString(json.key);
            this.description = json.description;
        }

        static fromExtModel(model: Ext_data_Model): SiteTemplateSummary {
            return new SiteTemplateSummary(<api_content_site_template_json.SiteTemplateSummaryJson>model.raw);
        }

        getKey(): SiteTemplateKey {
            return this.key;
        }

        getName(): string {
            return this.name;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getVendor(): api_content_site.Vendor {
            return this.vendor;
        }

        getModules(): api_module.ModuleKey[] {
            return this.modules;
        }

        getSupportedContentTypes(): string[] {
            return this.supportedContentTypes;
        }

        getRootContentType(): api_schema_content.ContentTypeName {
            return this.rootContentType;
        }

        getVersion(): string {
            return this.version;
        }

        getUrl(): string {
            return this.url;
        }

        getDescription(): string {
            return this.description;
        }

        static fromJsonArray(jsonArray: api_content_site_template_json.SiteTemplateSummaryJson[]): SiteTemplateSummary[] {
            var array: SiteTemplateSummary[] = [];

            jsonArray.forEach((json: api_content_site_template_json.SiteTemplateSummaryJson) => {
                array.push(new SiteTemplateSummary(json));
            });
            return array;
        }
    }
}