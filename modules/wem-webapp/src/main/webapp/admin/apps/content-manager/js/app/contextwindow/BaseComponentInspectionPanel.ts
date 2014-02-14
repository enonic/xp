module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class BaseComponentInspectionPanel extends BaseInspectionPanel {

        private siteTemplate: SiteTemplate;
        private liveFormPanel: app.wizard.LiveFormPanel;

        constructor(iconClass: string, liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super(iconClass);

            this.siteTemplate = siteTemplate;
            this.liveFormPanel = liveFormPanel;
        }

        getLiveFormPanel(): app.wizard.LiveFormPanel {
            return this.liveFormPanel;
        }

        getSiteTemplate(): SiteTemplate {
            return this.siteTemplate;
        }

        setComponentName(component: api.content.page.PageComponent) {
            this.setName(component.getName().toString(), component.getPath().toString());
        }
    }
}