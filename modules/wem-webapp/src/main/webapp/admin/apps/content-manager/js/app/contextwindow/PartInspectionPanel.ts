module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class PartInspectionPanel extends PageComponentInspectionPanel<api.content.page.part.PartComponent> {

        private partComponent: api.content.page.part.PartComponent;

        constructor(liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-part", liveFormPanel, siteTemplate);
        }

        setPartComponent(component: api.content.page.part.PartComponent) {
            this.setComponent(component);
            this.partComponent = component;
        }

    }
}