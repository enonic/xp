module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class PageComponentInspectionPanel<COMPONENT extends api.content.page.PageComponent> extends BaseInspectionPanel {

        private siteTemplate: SiteTemplate;
        private liveFormPanel: app.wizard.LiveFormPanel;
        private component: COMPONENT;

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

        setComponent(component: COMPONENT) {

            this.component = component;

            this.setMainName(component.getName().toString());
            this.setSubName(component.getPath().toString());

            // TODO: select descriptor (component.descriptor)
            // TODO: display config form for selected descriptor
        }
    }
}