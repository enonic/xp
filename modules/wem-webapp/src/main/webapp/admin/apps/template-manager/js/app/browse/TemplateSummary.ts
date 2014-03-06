module app.browse {

    import PageTemplateKey = api.content.page.PageTemplateKey;

    export enum TemplateType {
        PAGE,
        SITE
    }

    export class TemplateSummary extends api.item.BaseItem {

        private name: string;

        private displayName: string;

        private key: string;

        private type: TemplateType;

        private siteTemplateKey: api.content.site.template.SiteTemplateKey;

        constructor(json: api.content.site.template.json.TemplateSummaryJson) {
            super(json);
            this.name = json.name;
            this.displayName = json.name;
            this.key = json.key;
            this.type = TemplateType[json.templateType.toUpperCase()];
            if (this.type === TemplateType.SITE) {
                this.siteTemplateKey = api.content.site.template.SiteTemplateKey.fromString(this.key);
            }
        }

        static fromExtModel(model: Ext_data_Model): TemplateSummary {
            return new TemplateSummary(<api.content.site.template.json.TemplateSummaryJson>model.raw);
        }

        static fromExtModelArray(modelArray: Ext_data_Model[]): TemplateSummary[] {
            var array: TemplateSummary[] = [];
            modelArray.forEach((model: Ext_data_Model) => {
                array.push(TemplateSummary.fromExtModel(model));
            });
            return array;
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

    }
}