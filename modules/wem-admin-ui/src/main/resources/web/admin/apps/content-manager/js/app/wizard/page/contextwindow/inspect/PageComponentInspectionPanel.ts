module app.wizard.page.contextwindow.inspect {

    import RootDataSet = api.data.RootDataSet;
    import FormView = api.form.FormView;
    import PageComponent = api.content.page.PageComponent;

    export interface PageComponentInspectionPanelConfig {

        iconClass: string;

    }

    export class PageComponentInspectionPanel<COMPONENT extends PageComponent> extends BaseInspectionPanel {

        private namesAndIcon: api.app.NamesAndIconView;

        private component: COMPONENT;

        constructor(config: PageComponentInspectionPanelConfig) {
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

        setMainName(value: string) {
            this.namesAndIcon.setMainName(value);
        }

        getComponent(): COMPONENT {
            return this.component;
        }
    }
}