module app.wizard.page.contextwindow.inspect.region {

    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import ContentSummary = api.content.ContentSummary;
    import ContentId = api.content.ContentId;
    import ContentDropdown = api.content.ContentDropdown;
    import ContentDropdownConfig = api.content.ContentDropdownConfig;
    import ImageComponent = api.content.page.image.ImageComponent;
    import ImageComponentView = api.liveedit.image.ImageComponentView;
    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import PropertyTree = api.data.PropertyTree;

    export class ImageInspectionPanel extends ComponentInspectionPanel<ImageComponent> {

        private imageComponent: ImageComponent;

        private imageView: ImageComponentView;

        private formView: api.form.FormView;

        private imageSelected: ContentId;

        private imageSelector: ContentDropdown;

        private imageChangedListeners: {(event: ImageChangedEvent): void;}[] = [];

        constructor() {
            super(<ComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-image icon-xlarge"
            });
            var loader = new api.content.ContentSummaryLoader();
            this.imageSelector = new ContentDropdown("imageSelector", <ContentDropdownConfig>{
                loader: loader,
                allowedContentTypes: [ api.schema.content.ContentTypeName.IMAGE.toString()]
            });
            loader.search("");
            this.appendChild(this.imageSelector);
            this.imageSelector.onOptionSelected((event: OptionSelectedEvent<ContentSummary>) => {

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
        }

        setComponent(component: ImageComponent) {
            super.setComponent(component);
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
            this.imageSelector.setContent(this.imageComponent.getImage());
            this.formView.layout().catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
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