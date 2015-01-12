module app.wizard.page.contextwindow.inspect.region {

    import Component = api.content.page.Component;
    import ComponentName = api.content.page.ComponentName;
    import ComponentView = api.liveedit.ComponentView;

    export interface ComponentInspectionPanelConfig {

        iconClass: string;
    }

    export class ComponentInspectionPanel<COMPONENT extends Component> extends app.wizard.page.contextwindow.inspect.BaseInspectionPanel {

        private namesAndIcon: api.app.NamesAndIconView;

        private component: COMPONENT;

        constructor(config: ComponentInspectionPanelConfig) {
            super();

            this.namesAndIcon = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.medium)).
                setIconClass(config.iconClass);

            this.appendChild(this.namesAndIcon);

        }

        setComponent(component: COMPONENT) {
            this.component = component;

            this.namesAndIcon.setMainName(component.getName().toString());
            this.namesAndIcon.setSubName(component.getPath().toString());
        }

        getComponentView(): ComponentView<Component> {
            throw new Error("Must be implemented by inheritors");
        }

        setMainName(value: string) {
            this.namesAndIcon.setMainName(value);
        }

        getComponent(): COMPONENT {
            return this.component;
        }
    }
}