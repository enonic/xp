module app.contextwindow {

    export class LayoutInspectionPanel extends app.contextwindow.BaseComponentInspectionPanel {

        private layoutComponent: api.content.page.layout.LayoutComponent;

        constructor(config: ComponentInspectionPanelConfig) {
            super(config, "live-edit-font-icon-layout");

        }

        setLayoutComponent(component: api.content.page.layout.LayoutComponent) {
            this.layoutComponent = component;
            this.setName(component.getName().toString(), component.getPath().toString());
        }

    }
}