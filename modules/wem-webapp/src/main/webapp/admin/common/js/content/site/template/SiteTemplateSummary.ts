module api.content.site.template {

    import PageTemplateKey = api.content.page.PageTemplateKey;

    export class SiteTemplateSummary extends api.item.BaseItem {

        private name: string;

        private displayName: string;

        private vendor: api.content.site.Vendor;

        private modules: api.module.ModuleKey[] = [];

        private rootContentType: api.schema.content.ContentTypeName;

        private version: string;

        private url: string;

        private key: SiteTemplateKey;

        private description: string;

        private contentTypeFilter: api.schema.content.ContentTypeFilter;

        private pageTemplateKeys: PageTemplateKey[];

        constructor(json: api.content.site.template.json.SiteTemplateSummaryJson) {
            super(json);
            this.name = json.name;
            this.displayName = json.name;
            this.vendor = new api.content.site.Vendor(json.vendor);
            for (var i = 0; i < json.modules.length; i++) {
                this.modules.push(api.module.ModuleKey.fromString(json.modules[i]));
            }
            this.rootContentType = new api.schema.content.ContentTypeName(json.rootContentType);
            this.version = json.version;
            this.url = json.url;
            this.key = SiteTemplateKey.fromString(json.key);
            this.description = json.description;

            this.contentTypeFilter = new api.schema.content.ContentTypeFilterBuilder().
                fromJson(json.contentTypeFilter).
                build();

            this.pageTemplateKeys = [];
            json.pageTemplateKeys.forEach((key: string) => {
                this.pageTemplateKeys.push(PageTemplateKey.fromString(key));
            });

        }

        static fromExtModel(model: Ext_data_Model): SiteTemplateSummary {
            return new SiteTemplateSummary(<api.content.site.template.json.SiteTemplateSummaryJson>model.raw);
        }

        static fromExtModelArray(modelArray: Ext_data_Model[]): SiteTemplateSummary[] {
            var array: SiteTemplateSummary[] = [];
            modelArray.forEach((model: Ext_data_Model) => {
                array.push(SiteTemplateSummary.fromExtModel(model));
            });
            return array;
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

        getVendor(): api.content.site.Vendor {
            return this.vendor;
        }

        getModules(): api.module.ModuleKey[] {
            return this.modules;
        }

        getRootContentType(): api.schema.content.ContentTypeName {
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

        static fromJsonArray(jsonArray: api.content.site.template.json.SiteTemplateSummaryJson[]): SiteTemplateSummary[] {
            var array: SiteTemplateSummary[] = [];

            jsonArray.forEach((json: api.content.site.template.json.SiteTemplateSummaryJson) => {
                array.push(new SiteTemplateSummary(json));
            });
            return array;
        }
    }
}