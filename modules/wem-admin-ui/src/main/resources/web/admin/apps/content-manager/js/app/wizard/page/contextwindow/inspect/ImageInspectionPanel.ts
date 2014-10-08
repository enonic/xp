module app.wizard.page.contextwindow.inspect {

    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import ImageComponent = api.content.page.image.ImageComponent;
    import PageComponentView = api.liveedit.PageComponentView;
    import ImageComponentView = api.liveedit.image.ImageComponentView;

    export interface ImageInspectionPanelConfig {

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
            this.appendChild(this.formView);
        }

        getPageComponentView(): ImageComponentView {
            return this.imageView;
        }

    }
}