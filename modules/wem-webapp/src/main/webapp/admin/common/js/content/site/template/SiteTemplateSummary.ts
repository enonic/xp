module api.content.site.template {

    import TemplateName = api.content.page.TemplateName;

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

        private contentTypeFilter: ContentTypeFilter;

        private pageTemplates: TemplateName[];

        private partTemplates: TemplateName[];

        private layoutTemplates: TemplateName[];

        private imageTemplates: TemplateName[];

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

            this.contentTypeFilter = new ContentTypeFilterBuilder().
                fromJson(json.contentTypeFilter).
                build();

            this.pageTemplates = [];
            json.pageTemplates.forEach((name: string) => {
                this.pageTemplates.push(new TemplateName(name));
            });

            this.layoutTemplates = [];
            json.layoutTemplates.forEach((name: string) => {
                this.layoutTemplates.push(new TemplateName(name));
            });

            this.partTemplates = [];
            json.partTemplates.forEach((name: string) => {
                this.partTemplates.push(new TemplateName(name));
            });

            this.imageTemplates = [];
            json.imageTemplates.forEach((name: string) => {
                this.imageTemplates.push(new TemplateName(name));
            });
        }

        static fromExtModel(model: Ext_data_Model): SiteTemplateSummary {
            return new SiteTemplateSummary(<api.content.site.template.json.SiteTemplateSummaryJson>model.raw);
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

        public getDefaultImageTemplate(): api.content.page.TemplateName {
            if (this.imageTemplates.length == 0) {
                return null;
            }

            return this.imageTemplates[0];
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