module app_wizard_site {

    export class TemplateView extends api_dom.DivEl {

        private contentType: api_schema_content.ContentType;

        private siteTemplate: api_content_site_template.SiteTemplate;

        constructor(template: api_content_site_template.SiteTemplate, contentType: api_schema_content.ContentType) {
            super("TemplateView", "input-view template-view");
            this.contentType = contentType;
            this.siteTemplate = template;

            var label = new api_dom.DivEl("TemplateLabel", "input-label");
            label.getEl().setInnerHtml("Site Template");
            this.appendChild(label);

            var input = new api_dom.DivEl("TemplateInput", "input-type-view");
            this.appendChild(input);

            var imgEl = new api_dom.ImgEl(this.contentType.getIconUrl());
            input.appendChild(imgEl);

            var h4 = new api_dom.H4El();
            var p = new api_dom.PEl();
            h4.getEl().setInnerHtml(template.getDisplayName());
            p.getEl().setInnerHtml(template.getDescription());
            input.appendChild(h4);
            input.appendChild(p);
        }

        getSiteTemplateKey(): api_content_site_template.SiteTemplateKey {
            return this.siteTemplate.getKey();
        }
    }

}