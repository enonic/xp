module app.contextwindow {

    export class PartInspectionPanel extends app.contextwindow.BaseComponentInspectionPanel {

        private partComponent: api.content.page.part.PartComponent;

        constructor(config: ComponentInspectionPanelConfig) {
            super(config, "live-edit-font-icon-part");

        }

        setPartComponent(component: api.content.page.part.PartComponent) {
            this.partComponent = component;
            this.setName(component.getName().toString(), component.getPath().toString());
        }

    }
}