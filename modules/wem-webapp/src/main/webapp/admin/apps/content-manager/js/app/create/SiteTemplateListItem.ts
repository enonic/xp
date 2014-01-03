module app.create {

    export class SiteTemplateListItem extends api.dom.LiEl {

        private siteTemplate: api.content.site.template.SiteTemplateSummary;

        private contentType: api.schema.content.ContentTypeSummary;

        constructor(item: api.content.site.template.SiteTemplateSummary, contentType: api.schema.content.ContentTypeSummary) {
            super("SiteTemplateListItem", "site-template-list-item site");

            this.siteTemplate = item;
            this.contentType = contentType;

            var img = new api.dom.ImgEl(contentType.getIconUrl());

            var h6 = new api.dom.H6El();
            h6.getEl().setInnerHtml(item.getDisplayName());
            h6.getEl().setAttribute("title", item.getDisplayName());

            var p = new api.dom.PEl();
            p.getEl().setInnerHtml(item.getName());
            p.getEl().setAttribute("title", item.getName());

            this.appendChild(img);
            this.appendChild(h6);
            this.appendChild(p);

            var span = new api.dom.SpanEl();
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

        getSiteTemplate(): api.content.site.template.SiteTemplateSummary {
            return this.siteTemplate;
        }

        getContentType(): api.schema.content.ContentTypeSummary {
            return this.contentType;
        }

    }
}