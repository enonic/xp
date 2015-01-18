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

        private imageSelected: ContentId;

        private imageChangedListeners: {(event: ImageChangedEvent): void;}[] = [];

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
            var formContext = new api.form.FormContextBuilder().
                build();
            var configData = this.imageComponent.getConfig();
            var configForm = this.imageComponent.getForm();
            this.formView = new api.form.FormView(formContext, configForm, configData.getRoot());
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

                if (this.getComponent()) {
                    var selectedContentId: ContentId = option.displayValue.getContentId();
                    this.imageComponent.setImage(selectedContentId);

                    var hasImageChanged = selectedContentId && !selectedContentId.equals(this.imageSelected);
                    if (hasImageChanged) {
                        this.notifyImageChanged(this.imageView, selectedContentId);
                    }
                    this.imageSelected = selectedContentId;
                }
            });
            this.componentSelector.onOptionDeselected((option: SelectedOption<ContentSummary>) => {
                this.imageSelected = null;
                this.imageView.getComponent().setImage(null);
                this.notifyImageChanged(this.imageView, null);
            });
        }

        getComponentView(): ImageComponentView {
            return this.imageView;
        }

        onImageChanged(listener: {(event: ImageChangedEvent): void;}) {
            this.imageChangedListeners.push(listener);
        }

        unImageChanged(listener: {(event: ImageChangedEvent): void;}) {
            this.imageChangedListeners = this.imageChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyImageChanged(imageView: ImageComponentView, contentId: ContentId) {
            var event = new ImageChangedEvent(imageView, contentId);
            this.imageChangedListeners.forEach((listener) => {
                listener(event);
            });
        }

    }
}