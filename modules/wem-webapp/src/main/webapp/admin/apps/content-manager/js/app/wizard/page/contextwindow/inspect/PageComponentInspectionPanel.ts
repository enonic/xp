module app.wizard.page.contextwindow.inspect {

    import RootDataSet = api.data.RootDataSet;
    import FormView = api.form.FormView;
    import PageComponent = api.content.page.PageComponent;
    import DescriptorKey = api.content.page.DescriptorKey;
    import Descriptor = api.content.page.Descriptor;

    export interface PageComponentInspectionPanelConfig {

        iconClass: string;

    }

    export class PageComponentInspectionPanel<COMPONENT extends PageComponent, DESCRIPTOR extends Descriptor> extends BaseInspectionPanel {

        private namesAndIcon: api.app.NamesAndIconView;

        private formView: FormView;

        private component: COMPONENT;

        constructor(config: PageComponentInspectionPanelConfig) {
            super();

            this.formView = null;

            this.namesAndIcon = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.medium)).
                setIconClass(config.iconClass);

            this.appendChild(this.namesAndIcon);
        }

        setComponent(component: COMPONENT) {

            this.component = component;

            if (this.hasDescriptor()) {
                this.namesAndIcon.setMainName(this.getDescriptor().getDisplayName().toString());
            }
            else {
                this.namesAndIcon.setMainName(component.getName().toString());
            }
            this.namesAndIcon.setSubName(component.getPath().toString());
        }

        getComponent(): COMPONENT {
            return this.component;
        }

        hasDescriptor(): boolean {
            if (this.getDescriptor()) {
                return true;
            }
            else {
                return false;
            }
        }

        getDescriptor(): DESCRIPTOR {
            throw new Error("To be implemented by subclasses")
        }

        setupComponentForm(component: PageComponent, descriptor: Descriptor) {
            if (this.formView || !descriptor) {
                this.removeChild(this.formView);
                this.formView = null;
                return;
            }
            if (!component) {
                return;
            }

            var formContext = new api.form.FormContextBuilder().build();
            var form = descriptor.getConfig();
            var config: RootDataSet = component.getConfig();
            this.formView = new FormView(formContext, form, config);
            this.formView.setDoOffset(false);
            this.appendChild(this.formView);
        }
    }
}