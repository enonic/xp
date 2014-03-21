module app.wizard.page.contextwindow.inspect{

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import PageComponent = api.content.page.PageComponent;
    import DescriptorKey = api.content.page.DescriptorKey;
    import Descriptor = api.content.page.Descriptor;

    export class PageComponentInspectionPanel<COMPONENT extends PageComponent, DESCRIPTOR extends Descriptor> extends BaseInspectionPanel {

        private siteTemplate: SiteTemplate;
        private liveFormPanel: app.wizard.LiveFormPanel;
        private formView: api.form.FormView;
        private component: COMPONENT;

        constructor(iconClass: string, liveFormPanel: app.wizard.LiveFormPanel, siteTemplate: SiteTemplate) {
            super(iconClass);

            this.siteTemplate = siteTemplate;
            this.liveFormPanel = liveFormPanel;
            this.formView = null;
        }

        getLiveFormPanel(): app.wizard.LiveFormPanel {
            return this.liveFormPanel;
        }

        getSiteTemplate(): SiteTemplate {
            return this.siteTemplate;
        }

        setComponent(component: COMPONENT) {

            this.component = component;

            if (component.getDescriptor()) {
                this.setMainName(this.getDescriptor(component.getDescriptor()).getName().toString());
            } else {
                this.setMainName(component.getName().toString());
            }
            this.setSubName(component.getName().toString());

            // TODO: select descriptor (component.descriptor)
            // TODO: display config form for selected descriptor

        }

        getDescriptor(key: DescriptorKey): DESCRIPTOR   {
            throw new Error("To be implemented by subclasses")
        }

        setupComponentForm(component: PageComponent, descriptor: Descriptor) {
            if (this.formView) {
                this.removeChild(this.formView);
            }
            if (!component) {
                return;
            }

            var formContext = new api.form.FormContextBuilder().build();
            var form = descriptor.getConfig();
            var config: api.data.RootDataSet = component.getConfig();
            this.formView = new api.form.FormView(formContext, form, config);
            this.formView.setDoOffset(false);
            this.appendChild(this.formView);
        }
    }
}