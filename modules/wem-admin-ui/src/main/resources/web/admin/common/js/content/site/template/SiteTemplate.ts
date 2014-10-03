module api.content.site.template {

    export class SiteTemplate extends SiteTemplateSummary implements api.Equitable {

        private pageTemplates: api.content.page.PageTemplate[];

        constructor(builder: SiteTemplateBuilder) {
            super(builder);

            this.pageTemplates = builder.pageTemplates;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, SiteTemplate)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <SiteTemplate>o;

            if (!api.ObjectHelper.arrayEquals(this.pageTemplates, other.pageTemplates)) {
                return false;
            }

            return true;
        }

        static fromJson(json: api.content.site.template.SiteTemplateJson): SiteTemplate {
            return new SiteTemplateBuilder().fromSiteTemplateJson(json).build();
        }
    }

    export class SiteTemplateBuilder extends SiteTemplateSummaryBuilder {

        pageTemplates: api.content.page.PageTemplate[];

        fromSiteTemplateJson(json: api.content.site.template.SiteTemplateJson): SiteTemplateBuilder {

            super.fromSiteTemplateSummaryJson(json);

            this.pageTemplates = [];
            json.pageTemplates.forEach((pageTemplateJson: any)=> {
                //this.pageTemplates.push(new api.content.page.PageTemplateBuilder().fromJson(pageTemplateJson).build());
            });
            return this;
        }

        build(): SiteTemplate {
            return new SiteTemplate(this);
        }

    }
}