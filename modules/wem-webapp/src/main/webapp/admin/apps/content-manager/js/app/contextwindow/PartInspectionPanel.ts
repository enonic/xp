module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class PartInspectionPanel extends app.contextwindow.BaseComponentInspectionPanel {

        private partComponent: api.content.page.part.PartComponent;

        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-part", liveFormPanel, siteTemplate);
        }

        setPartComponent(component: api.content.page.part.PartComponent) {
            this.partComponent = component;
            this.setComponentName(component);
        }

    }
}