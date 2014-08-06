module api.content.site.template {

    import PageTemplateKey = api.content.page.PageTemplateKey;

    export class SiteTemplateSummary extends api.item.BaseItem implements api.Equitable{

        private name: string;

        private displayName: string;

        private vendor: api.content.site.Vendor;

        private modules: api.module.ModuleKey[] = [];

        private version: string;

        private url: string;

        private key: SiteTemplateKey;

        private description: string;

        private contentTypeFilter: api.schema.content.ContentTypeFilter;

        private pageTemplateKeys: PageTemplateKey[];

        private iconUrl: string;

        constructor(builder: SiteTemplateSummaryBuilder) {
            super(builder);

            this.name = builder.name;
            this.displayName = builder.name;
            this.vendor = builder.vendor;
            this.modules = builder.modules;
            this.version = builder.version;
            this.url = builder.url;
            this.key = builder.key;
            this.description = builder.description;
            this.contentTypeFilter = builder.contentTypeFilter;
            this.pageTemplateKeys = builder.pageTemplateKeys;
            this.iconUrl = builder.iconUrl;
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

        getVersion(): string {
            return this.version;
        }

        getUrl(): string {
            return this.url;
        }

        getDescription(): string {
            return this.description;
        }

        getIconUrl(): string {
            return this.iconUrl;
        }

        hasChildren(): boolean {
            return false;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, SiteTemplateSummary)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <SiteTemplateSummary>o;

            if (!api.ObjectHelper.stringEquals(this.name, other.name)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.displayName, other.displayName)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.vendor, other.vendor)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.version, other.version)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.url, other.url)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.key, other.key)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.description, other.description)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.contentTypeFilter, other.contentTypeFilter)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.modules, other.modules)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.pageTemplateKeys, other.pageTemplateKeys)) {
                return false;
            }

            if (!ObjectHelper.stringEquals(this.iconUrl, other.iconUrl)) {
                return false;
            }

            return true;
        }

        static fromJson(json: api.content.site.template.SiteTemplateSummaryJson): SiteTemplateSummary {
            return new SiteTemplateSummaryBuilder().fromSiteTemplateSummaryJson(json).build();
        }

        static fromJsonArray(jsonArray: api.content.site.template.SiteTemplateSummaryJson[]): SiteTemplateSummary[] {
            var array: SiteTemplateSummary[] = [];

            jsonArray.forEach((json: api.content.site.template.SiteTemplateSummaryJson) => {
                array.push(new SiteTemplateSummaryBuilder().
                    fromSiteTemplateSummaryJson(json).
                    build());
            });
            return array;
        }

        static fromExtModel(model: Ext_data_Model): SiteTemplateSummary {
            return new SiteTemplateSummaryBuilder().
                fromSiteTemplateSummaryJson(<api.content.site.template.SiteTemplateSummaryJson>model.raw).
                build();
        }

        static fromExtModelArray(modelArray: Ext_data_Model[]): SiteTemplateSummary[] {
            var array: SiteTemplateSummary[] = [];
            modelArray.forEach((model: Ext_data_Model) => {
                array.push(SiteTemplateSummary.fromExtModel(model));
            });
            return array;
        }
    }

    export class SiteTemplateSummaryBuilder extends api.item.BaseItemBuilder {

        name: string;

        displayName: string;

        vendor: api.content.site.Vendor;

        modules: api.module.ModuleKey[] = [];

        version: string;

        url: string;

        key: SiteTemplateKey;

        description: string;

        contentTypeFilter: api.schema.content.ContentTypeFilter;

        pageTemplateKeys: PageTemplateKey[];

        iconUrl: string;

        fromSiteTemplateSummaryJson(json: api.content.site.template.SiteTemplateSummaryJson): SiteTemplateSummaryBuilder {

            super.fromBaseItemJson(json, 'key');

            this.name = json.name;
            this.displayName = json.name;
            this.vendor = new api.content.site.Vendor(json.vendor);
            for (var i = 0; i < json.modules.length; i++) {
                this.modules.push(api.module.ModuleKey.fromString(json.modules[i]));
            }
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
            this.iconUrl = json.iconUrl;
            return this;
        }

        build(): SiteTemplateSummary {
            return new SiteTemplateSummary(this);
        }
    }
}