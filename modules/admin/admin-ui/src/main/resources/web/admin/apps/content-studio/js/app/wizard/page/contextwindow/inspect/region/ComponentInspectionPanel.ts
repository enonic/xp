module app.wizard.page.contextwindow.inspect.region {

    import Component = api.content.page.region.Component;
    import ComponentName = api.content.page.region.ComponentName;
    import ComponentView = api.liveedit.ComponentView;
    import ContentFormContext = api.content.form.ContentFormContext;
    import LiveEditModel = api.liveedit.LiveEditModel;

    export interface ComponentInspectionPanelConfig {

        iconClass: string;
    }

    export abstract class ComponentInspectionPanel<COMPONENT extends Component> extends app.wizard.page.contextwindow.inspect.BaseInspectionPanel {

        liveEditModel: LiveEditModel;

        formContext: ContentFormContext;

        private component: COMPONENT;

        constructor(config: ComponentInspectionPanelConfig) {
            super();
        }

        setModel(liveEditModel: LiveEditModel) {

            this.liveEditModel = liveEditModel;
            this.formContext = liveEditModel.getFormContext();
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