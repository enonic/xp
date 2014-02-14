module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class LayoutInspectionPanel extends BaseComponentInspectionPanel<api.content.page.layout.LayoutComponent> {

        private layoutComponent: api.content.page.layout.LayoutComponent;

        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-layout", liveFormPanel, siteTemplate);
        }

        setLayoutComponent(component: api.content.page.layout.LayoutComponent) {
            this.setComponent(component);
            this.layoutComponent = component;
        }

    }
}