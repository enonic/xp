module app.wizard.page.contextwindow.inspect {

    import DefaultModels = app.wizard.page.DefaultModels;
    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import SiteTemplate = api.content.site.template.SiteTemplate;
    import ImageComponent = api.content.page.image.ImageComponent;
    import PageComponentView = api.liveedit.PageComponentView;
    import ImageComponentView = api.liveedit.image.ImageComponentView;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import DescriptorKey = api.content.page.DescriptorKey;
    import Descriptor = api.content.page.Descriptor;
    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export interface ImageInspectionPanelConfig {

        siteTemplate: SiteTemplate;

        defaultModels: DefaultModels;
    }

    export class ImageInspectionPanel extends PageComponentInspectionPanel<ImageComponent> {

        private imageComponent: ImageComponent;

        private imageView: ImageComponentView;

        private formView: api.form.FormView;

        constructor(config: ImageInspectionPanelConfig) {
            super(<PageComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-image icon-xlarge"
            });
        }

        setComponent(component: ImageComponent) {
            super.setComponent(component);
            this.setMainName(component.getName().toString());
        }

        setImageComponent(imageView: ImageComponentView) {
            this.setComponent(imageView.getPageComponent());
            this.imageView = imageView;
            this.imageComponent = imageView.getPageComponent();

            if (this.formView) {
                this.removeChild(this.formView);
                this.formView = null;
            }
            var formContext = new api.form.FormContextBuilder().
                build();
            var configData = this.imageComponent.getConfig();
            if (!configData) {
                configData = new api.data.RootDataSet();
                this.imageComponent.setConfig(configData);
            }
            var configForm = this.imageComponent.getForm();
            this.formView = new api.form.FormView(formContext, configForm, configData);
            this.formView.setDoOffset(false);
            this.appendChild(this.formView);
        }

        getPageComponentView(): ImageComponentView {
            return this.imageView;
        }

    }
}