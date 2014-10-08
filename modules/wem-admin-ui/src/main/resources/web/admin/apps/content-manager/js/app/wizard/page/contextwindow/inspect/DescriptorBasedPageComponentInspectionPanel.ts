module app.wizard.page.contextwindow.inspect {

    import RootDataSet = api.data.RootDataSet;
    import FormView = api.form.FormView;
    import DescriptorBasedPageComponent = api.content.page.DescriptorBasedPageComponent;
    import DescriptorKey = api.content.page.DescriptorKey;
    import Descriptor = api.content.page.Descriptor;

    export interface DescriptorBasedPageComponentInspectionPanelConfig {

        iconClass: string;

    }

    export class DescriptorBasedPageComponentInspectionPanel<COMPONENT extends DescriptorBasedPageComponent, DESCRIPTOR extends Descriptor> extends PageComponentInspectionPanel<COMPONENT> {

        private formView: FormView;

        constructor(config: DescriptorBasedPageComponentInspectionPanelConfig) {
            super(config);

            this.formView = null;
        }

        setComponent(component: COMPONENT) {

            super.setComponent(component);

            if (this.hasDescriptor()) {
                this.setMainName(this.getDescriptor().getDisplayName().toString());
            }
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
            throw new Error("Must be implemented by inheritors")
        }

        setupComponentForm(component: DescriptorBasedPageComponent, descriptor: Descriptor) {
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
            this.formView = new FormView(formContext, form, config);
            this.appendChild(this.formView);
        }
    }
}