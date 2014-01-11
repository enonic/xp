module app.wizard.site {

    export class TemplateView extends api.dom.DivEl {

        private contentType: api.schema.content.ContentType;

        private siteTemplate: api.content.site.template.SiteTemplate;

        constructor(template: api.content.site.template.SiteTemplate, contentType: api.schema.content.ContentType) {
            super(true, "input-view template-view");
            this.contentType = contentType;
            this.siteTemplate = template;

            var label = new api.dom.DivEl(true, "input-label");
            label.getEl().setInnerHtml("Site Template");
            this.appendChild(label);

            var input = new api.dom.DivEl(true, "input-type-view");
            this.appendChild(input);

            var imgEl = new api.dom.ImgEl(this.contentType.getIconUrl());
            input.appendChild(imgEl);

            var h4 = new api.dom.H4El();
            var p = new api.dom.PEl();
            h4.getEl().setInnerHtml(template.getDisplayName());
            p.getEl().setInnerHtml(template.getDescription());
            input.appendChild(h4);
            input.appendChild(p);
        }

        getSiteTemplateKey(): api.content.site.template.SiteTemplateKey {
            return this.siteTemplate.getKey();
        }
    }

}