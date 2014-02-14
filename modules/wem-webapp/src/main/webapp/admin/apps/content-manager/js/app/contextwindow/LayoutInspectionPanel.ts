module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class LayoutInspectionPanel extends app.contextwindow.BaseComponentInspectionPanel {

        private layoutComponent: api.content.page.layout.LayoutComponent;

        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-layout", liveFormPanel, siteTemplate);
        }

        setLayoutComponent(component: api.content.page.layout.LayoutComponent) {
            this.layoutComponent = component;
            this.setComponentName(component);
        }

    }
}