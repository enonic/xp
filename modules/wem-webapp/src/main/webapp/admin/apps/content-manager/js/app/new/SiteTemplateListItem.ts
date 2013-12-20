module app_new {

    export class SiteTemplateListItem extends api_dom.LiEl {

        private siteTemplate: api_content_site_template.SiteTemplateSummary;

        private contentType: api_schema_content.ContentTypeSummary;

        constructor(item: api_content_site_template.SiteTemplateSummary, contentType: api_schema_content.ContentTypeSummary) {
            super("SiteTemplateListItem", "site-template-list-item site");

            this.siteTemplate = item;
            this.contentType = contentType;

            var img = new api_dom.ImgEl(contentType.getIconUrl());

            var h6 = new api_dom.H6El();
            h6.getEl().setInnerHtml(item.getDisplayName());
            h6.getEl().setAttribute("title", item.getDisplayName());

            var p = new api_dom.PEl();
            p.getEl().setInnerHtml(item.getName());
            p.getEl().setAttribute("title", item.getName());

            this.appendChild(img);
            this.appendChild(h6);
            this.appendChild(p);

            var span = new api_dom.SpanEl();
            span.setClass('overlay');
            this.appendChild(span);
        }

        getName(): string {
            return this.siteTemplate.getName();
        }

        getDisplayName(): string {
            return this.siteTemplate.getDisplayName();
        }

        getIconUrl(): string {
            return this.contentType.getIconUrl();
        }

        getSiteTemplate(): api_content_site_template.SiteTemplateSummary {
            return this.siteTemplate;
        }

        getContentType(): api_schema_content.ContentTypeSummary {
            return this.contentType;
        }

    }
}