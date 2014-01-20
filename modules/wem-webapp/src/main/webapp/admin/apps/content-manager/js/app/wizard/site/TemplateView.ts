module app.wizard.site {

    export class TemplateView extends api.dom.DivEl {

        private contentType: api.schema.content.ContentType;

        private siteTemplate: api.content.site.template.SiteTemplate;

        private nameAndIconView: api.app.NameAndIconView;

        constructor() {
            super("template-view");

            var label = new api.dom.DivEl("input-label");
            label.getEl().setInnerHtml("Site Template");
            this.appendChild(label);

            var input = new api.dom.DivEl("input-type-view");
            this.appendChild(input);

            this.nameAndIconView = new api.app.NameAndIconView().medium();
            input.appendChild(this.nameAndIconView);
        }

        setValue(siteTemplate: api.content.site.template.SiteTemplate, contentType: api.schema.content.ContentType) {
            this.siteTemplate = siteTemplate;
            this.contentType = contentType;

            this.nameAndIconView.
                setMainName(siteTemplate.getDisplayName()).
                setSubName(siteTemplate.getDescription()).
                setIconUrl(this.contentType.getIconUrl());
        }

        getSiteTemplateKey(): api.content.site.template.SiteTemplateKey {
            return this.siteTemplate.getKey();
        }
    }

}