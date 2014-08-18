module app.browse {

    export class TemplateIconUrlResolver extends api.icon.IconUrlResolver {

        private pageTemplateIconUri: string;

        private siteTemplateIconUri: string;

        private template: TemplateSummary;

        constructor() {
            super();
            this.pageTemplateIconUri = api.util.getAdminUri('common/images/icons/icoMoon/32x32/newspaper.png');
            this.siteTemplateIconUri = api.util.getAdminUri('common/images/icons/icoMoon/32x32/earth.png');
        }

        setTemplate(value: TemplateSummary): TemplateIconUrlResolver {
            this.template = value;
            return this;
        }

        resolve(): string {

            var url = "";

            if (this.template.isPageTemplate()) {
                url = this.pageTemplateIconUri;
            } else {
                url = this.template.getIconUrl() || this.siteTemplateIconUri;
            }
            return url;
        }

    }
}