module app.wizard.page.contextwindow.inspect {

    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import ImageComponent = api.content.page.image.ImageComponent;
    import ImageComponentView = api.liveedit.image.ImageComponentView;
    import PropertyTree = api.data.PropertyTree;

    export class ImageInspectionPanel extends ComponentInspectionPanel<ImageComponent> {

        private imageComponent: ImageComponent;

        private imageView: ImageComponentView;

        private formView: api.form.FormView;

        constructor() {
            super(<ComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-image icon-xlarge"
            });
        }

        setComponent(component: ImageComponent) {
            super.setComponent(component);
            this.setMainName(component.getName().toString());
        }

        setImageComponent(imageView: ImageComponentView) {
            this.setComponent(imageView.getComponent());
            this.imageView = imageView;
            this.imageComponent = imageView.getComponent();

            if (this.formView) {
                this.removeChild(this.formView);
                this.formView = null;
            }
            var formContext = new api.form.FormContextBuilder().
                build();
            var configData = this.imageComponent.getConfig();
            var configForm = this.imageComponent.getForm();
            this.formView = new api.form.FormView(formContext, configForm, configData.getRoot());
            this.appendChild(this.formView);
            this.formView.layout().catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }

        getPageComponentView(): ImageComponentView {
            return this.imageView;
        }

    }
}