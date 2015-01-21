module app.wizard.page.contextwindow.inspect.region {

    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import ImageComponent = api.content.page.region.ImageComponent;
    import ContentSummary = api.content.ContentSummary;
    import ContentId = api.content.ContentId;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ImageComponentView = api.liveedit.image.ImageComponentView;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import PropertyTree = api.data.PropertyTree;

    export class ImageInspectionPanel extends ComponentInspectionPanel<ImageComponent> {

        private imageComponent: ImageComponent;

        private imageView: ImageComponentView;

        private formView: api.form.FormView;

        constructor() {
            super(<ComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-image icon-xlarge"
            });
            this.componentSelector = new api.content.ContentComboBoxBuilder().
                setMaximumOccurrences(1).
                setAllowedContentTypes([ContentTypeName.IMAGE.toString()]).
                setLoader(new api.content.ContentSummaryLoader()).
                build();

            this.initSelectorListeners();
            this.appendChild(this.componentSelector);
        }

        setComponent(component: ImageComponent) {
            super.setComponent(component);
        }

        setImageComponent(imageView: ImageComponentView) {
            this.setComponent(imageView.getComponent());
            this.imageView = imageView;
            this.imageComponent = imageView.getComponent();

            this.setSelectorValue(!!this.imageComponent.getImage() ? this.imageComponent.getImage().toString() : "");

            if (this.formView) {
                this.removeChild(this.formView);
                this.formView = null;
            }
            var configData = this.imageComponent.getConfig();
            var configForm = this.imageComponent.getForm();
            this.formView = new api.form.FormView(this.formContext, configForm, configData.getRoot());
            this.appendChild(this.formView);
            this.imageComponent.setDisableEventForwarding(true);
            this.formView.layout().catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).finally(() => {
                this.imageComponent.setDisableEventForwarding(false);
            }).done();
        }

        private initSelectorListeners() {

            this.componentSelector.onOptionSelected((event: OptionSelectedEvent<ContentSummary>) => {

                var option: Option<ContentSummary> = event.getOption();
                var imageContent = option.displayValue;
                this.imageComponent.setImage(imageContent.getContentId(), imageContent.getDisplayName());
            });

            this.componentSelector.onOptionDeselected((option: SelectedOption<ContentSummary>) => {
                this.imageComponent.setImage(null, null);
            });
        }

        getComponentView(): ImageComponentView {
            return this.imageView;
        }

    }
}