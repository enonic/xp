module api.content {

    import PageTemplateKey = api.content.page.PageTemplateKey;

    export enum TemplateType {
        PAGE,
        SITE
    }

    export class TemplateSummary extends api.item.BaseItem implements api.ui.treegrid.TreeItem {

        private name: string;

        private displayName: string;

        private key: string;

        private type: TemplateType;

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        private iconUrl: string;

        constructor(builder: TemplateSummaryBuilder) {
            super(builder);
            this.name = builder.name;
            this.displayName = builder.name;
            this.key = builder.key;
            this.type = builder.type;
            this.siteTemplateKey = builder.siteTemplateKey;
            this.iconUrl = builder.iconUrl;
        }

        getKey(): string {
            return this.key;
        }

        getName(): string {
            return this.name;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        getSiteTemplateKey(): api.content.site.template.SiteTemplateKey {
            return this.siteTemplateKey;
        }

        isPageTemplate(): boolean {
            return this.type === TemplateType.PAGE;
        }

        isSiteTemplate(): boolean {
            return this.type === TemplateType.SITE;
        }

        getIconUrl(): string {
            return this.iconUrl;
        }

        public hasChildren(): boolean {
            return this.isSiteTemplate();
        }

        static fromJson(json: api.content.site.template.TemplateSummaryJson): TemplateSummary {
            return new TemplateSummaryBuilder().fromTemplateSummaryJson(json).build();
        }
    }

    export class TemplateSummaryBuilder extends api.item.BaseItemBuilder {

        name: string;

        displayName: string;

        key: string;

        type: TemplateType;

        siteTemplateKey: api.content.site.template.SiteTemplateKey;

        iconUrl: string;

        fromTemplateSummaryJson(json: api.content.site.template.TemplateSummaryJson): TemplateSummaryBuilder {
            super.fromBaseItemJson(json, "key");
            this.name = json.name;
            this.displayName = json.name;
            this.key = json.key;
            this.type = TemplateType[json.templateType.toUpperCase()];
            if (this.type === TemplateType.SITE) {
                this.siteTemplateKey = api.content.site.template.SiteTemplateKey.fromString(this.key);
            } else if (json.parentKey) {
                this.siteTemplateKey = api.content.site.template.SiteTemplateKey.fromString(json.parentKey);
            }
            this.iconUrl = json.iconUrl;
            return this;
        }

        build(): TemplateSummary {
            return new TemplateSummary(this);
        }
    }
}