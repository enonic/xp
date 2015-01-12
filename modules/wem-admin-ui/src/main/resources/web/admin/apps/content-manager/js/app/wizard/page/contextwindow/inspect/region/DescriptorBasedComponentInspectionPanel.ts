module app.wizard.page.contextwindow.inspect.region {

    import FormView = api.form.FormView;
    import DescriptorBasedComponent = api.content.page.DescriptorBasedComponent;
    import DescriptorKey = api.content.page.DescriptorKey;
    import Descriptor = api.content.page.Descriptor;

    export interface DescriptorBasedComponentInspectionPanelConfig extends ComponentInspectionPanelConfig {

    }

    export class DescriptorBasedComponentInspectionPanel<COMPONENT extends DescriptorBasedComponent, DESCRIPTOR extends Descriptor> extends ComponentInspectionPanel<COMPONENT> {

        private formView: FormView;

        constructor(config: DescriptorBasedComponentInspectionPanelConfig) {
            super(config);

            this.formView = null;
        }

        setComponent(component: COMPONENT, descriptor?: Descriptor) {

            super.setComponent(component);

        }

        setupComponentForm(component: DescriptorBasedComponent, descriptor: Descriptor) {
            if (this.formView) {
                this.removeChild(this.formView);
                this.formView = null;
            }
            if (!component || !descriptor) {
                return;
            }

            var formContext = new api.form.FormContextBuilder().build();
            var form = descriptor.getConfig();
            var config = component.getConfig();
            this.formView = new FormView(formContext, form, config.getRoot());
            this.appendChild(this.formView);
            this.formView.layout().catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }
    }
}