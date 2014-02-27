module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class PageComponentInspectionPanel<COMPONENT extends api.content.page.PageComponent, DESCRIPTOR extends api.content.page.Descriptor> extends BaseInspectionPanel {

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

            this.setMainName(this.getDescriptor(component.getDescriptor()).getName().toString());
            this.setSubName(component.getName().toString());

            // TODO: select descriptor (component.descriptor)
            // TODO: display config form for selected descriptor

        }

        getDescriptor(key: api.content.page.DescriptorKey): DESCRIPTOR   {
            throw new Error("To be implemented by subclasses")
        }

        setupComponentForm(component: api.content.page.PageComponent, descriptor: api.content.page.Descriptor) {
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
            this.appendChild(this.formView);
        }
    }
}