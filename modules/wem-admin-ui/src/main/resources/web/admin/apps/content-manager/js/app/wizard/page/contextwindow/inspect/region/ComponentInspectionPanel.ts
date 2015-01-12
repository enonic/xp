module app.wizard.page.contextwindow.inspect.region {

    import Component = api.content.page.Component;
    import ComponentName = api.content.page.ComponentName;
    import ComponentView = api.liveedit.ComponentView;

    export interface ComponentInspectionPanelConfig {

        iconClass: string;
    }

    export class ComponentInspectionPanel<COMPONENT extends Component> extends app.wizard.page.contextwindow.inspect.BaseInspectionPanel {

        private component: COMPONENT;

        constructor(config: ComponentInspectionPanelConfig) {
            super();
        }

        setComponent(component: COMPONENT) {
            this.component = component;
        }

        getComponentView(): ComponentView<Component> {
            throw new Error("Must be implemented by inheritors");
        }

        getComponent(): COMPONENT {
            return this.component;
        }
    }
}