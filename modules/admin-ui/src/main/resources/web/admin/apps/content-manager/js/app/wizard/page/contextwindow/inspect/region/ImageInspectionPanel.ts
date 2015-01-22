module app.wizard.page.contextwindow.inspect.region {

    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import ImageComponent = api.content.page.region.ImageComponent;
    import ContentSummary = api.content.ContentSummary;
    import ContentId = api.content.ContentId;
    import GetContentSummaryByIdRequest = api.content.GetContentSummaryByIdRequest;
    import ContentComboBox = api.content.ContentComboBox;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ImageComponentView = api.liveedit.image.ImageComponentView;
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import PropertyTree = api.data.PropertyTree;

    export class ImageInspectionPanel extends ComponentInspectionPanel<ImageComponent> {

        private imageComponent: ImageComponent;

        private imageView: ImageComponentView;

        private formView: api.form.FormView;

        private imageSelector: ContentComboBox;

        private handleSelectorEvents: boolean = true;

        constructor() {
            super(<ComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-image icon-xlarge"
            });
            this.imageSelector = new api.content.ContentComboBoxBuilder().
                setMaximumOccurrences(1).
                setAllowedContentTypes([ContentTypeName.IMAGE.toString()]).
                setLoader(new api.content.ContentSummaryLoader()).
                build();

            this.initSelectorListeners();
            this.appendChild(this.imageSelector);
        }

        setComponent(component: ImageComponent) {
            super.setComponent(component);
        }

        setImageComponent(imageView: ImageComponentView) {
            this.imageView = imageView;
            this.imageComponent = imageView.getComponent();
            this.setComponent(this.imageComponent);

            var contentId: ContentId = this.imageComponent.getImage();
            if(contentId) {
                var image: ContentSummary = this.imageSelector.getContent(contentId);
                if (image) {
                    this.setSelectorValue(image);
                    this.setupComponentForm(this.imageComponent);
                } else {
                    new GetContentSummaryByIdRequest(contentId).sendAndParse().then((image:ContentSummary) => {
                        this.setSelectorValue(image);
                        this.setupComponentForm(this.imageComponent);
                    }).catch((reason:any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();
                }
            } else {
                this.setSelectorValue(null);
                this.setupComponentForm(this.imageComponent);
            }

            this.imageComponent.onPropertyChanged((event: ComponentPropertyChangedEvent) => {
                // Ensure displayed config form and selector option are removed when image is removed
                if (event.getPropertyName() == ImageComponent.PROPERTY_IMAGE) {
                    if (!this.imageComponent.hasImage()) {
                       this.setupComponentForm(this.imageComponent);
                        this.imageSelector.setContent(null);
                    }
                }
            });
        }

        private setSelectorValue(image: ContentSummary) {
            this.handleSelectorEvents = false;
            this.imageSelector.setContent(image);
            this.handleSelectorEvents = true;
        }

        private setupComponentForm(imageComponent: ImageComponent) {
            if (this.formView) {
                this.removeChild(this.formView);
                this.formView = null;
            }
            var configData = imageComponent.getConfig();
            var configForm = imageComponent.getForm();
            this.formView = new api.form.FormView(this.formContext, configForm, configData.getRoot());
            this.appendChild(this.formView);
            imageComponent.setDisableEventForwarding(true);
            this.formView.layout().catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).finally(() => {
                imageComponent.setDisableEventForwarding(false);
            }).done();
        }

        private initSelectorListeners() {

            this.imageSelector.onOptionSelected((event: OptionSelectedEvent<ContentSummary>) => {
                if(this.handleSelectorEvents) {
                    var option:Option<ContentSummary> = event.getOption();
                    var imageContent = option.displayValue;
                    this.imageComponent.setImage(imageContent.getContentId(), imageContent.getDisplayName());
                }
            });

            this.imageSelector.onOptionDeselected((option: SelectedOption<ContentSummary>) => {
                if(this.handleSelectorEvents) {
                    this.imageComponent.setImage(null, null);
                }
            });
        }

        getComponentView(): ImageComponentView {
            return this.imageView;
        }

    }
}