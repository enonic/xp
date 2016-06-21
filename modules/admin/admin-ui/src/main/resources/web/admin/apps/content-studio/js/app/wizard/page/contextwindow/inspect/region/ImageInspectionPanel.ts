import "../../../../../../api.ts";
import {ComponentInspectionPanel, ComponentInspectionPanelConfig} from "./ComponentInspectionPanel";
import {ImageSelectorForm} from "./ImageSelectorForm";

import ImageComponent = api.content.page.region.ImageComponent;
import ContentSummary = api.content.ContentSummary;
import ContentId = api.content.ContentId;
import ContentSummaryLoader = api.content.ContentSummaryLoader;
import GetContentSummaryByIdRequest = api.content.GetContentSummaryByIdRequest;
import ContentComboBox = api.content.ContentComboBox;
import ContentTypeName = api.schema.content.ContentTypeName;
import LiveEditModel = api.liveedit.LiveEditModel;
import ImageComponentView = api.liveedit.image.ImageComponentView;
import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
import Option = api.ui.selector.Option;
import SelectedOption = api.ui.selector.combobox.SelectedOption;
import PropertyTree = api.data.PropertyTree;
import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;

export class ImageInspectionPanel extends ComponentInspectionPanel<ImageComponent> {

    private imageComponent: ImageComponent;

    private imageView: ImageComponentView;

    private formView: api.form.FormView;

    private imageSelector: ContentComboBox;

    private loader: ContentSummaryLoader;

    private imageSelectorForm: ImageSelectorForm;

    private handleSelectorEvents: boolean = true;

    private componentPropertyChangedEventHandler: (event: ComponentPropertyChangedEvent) => void;

    constructor() {
        super(<ComponentInspectionPanelConfig>{
            iconClass: api.liveedit.ItemViewIconClassResolver.resolveByType("image", "icon-xlarge")
        });
        this.loader = new api.content.ContentSummaryLoader();
        this.loader.setAllowedContentTypeNames([ContentTypeName.IMAGE]);
        this.imageSelector = ContentComboBox.create().setMaximumOccurrences(1).setLoader(this.loader).build();

        this.imageSelectorForm = new ImageSelectorForm(this.imageSelector, "Image");

        this.componentPropertyChangedEventHandler = (event: ComponentPropertyChangedEvent) => {
            // Ensure displayed config form and selector option are removed when image is removed
            if (event.getPropertyName() == ImageComponent.PROPERTY_IMAGE) {
                if (!this.imageComponent.hasImage()) {
                    this.setupComponentForm(this.imageComponent);
                    this.imageSelector.setContent(null);
                }
            }
        };

        this.initSelectorListeners();
        this.appendChild(this.imageSelectorForm);
    }

    setModel(liveEditModel: LiveEditModel) {
        super.setModel(liveEditModel);
        this.loader.setContentPath(liveEditModel.getContent().getPath());
    }

    setComponent(component: ImageComponent) {
        super.setComponent(component);
    }

    setImageComponent(imageView: ImageComponentView) {
        this.imageView = imageView;
        if (this.imageComponent) {
            this.unregisterComponentListeners(this.imageComponent);
        }

        this.imageComponent = imageView.getComponent();
        this.setComponent(this.imageComponent);

        var contentId: ContentId = this.imageComponent.getImage();
        if (contentId) {
            var image: ContentSummary = this.imageSelector.getContent(contentId);
            if (image) {
                this.setImage(image);
            } else {
                new GetContentSummaryByIdRequest(contentId).sendAndParse().then((image: ContentSummary) => {
                    this.setImage(image);
                }).catch((reason: any) => {
                    if (this.isNotFoundError(reason)) {
                        this.setSelectorValue(null);
                        this.setupComponentForm(this.imageComponent);
                    } else {
                        api.DefaultErrorHandler.handle(reason);
                    }
                }).done();
            }
        } else {
            this.setSelectorValue(null);
            this.setupComponentForm(this.imageComponent);
        }

        this.registerComponentListeners(this.imageComponent);
    }

    private registerComponentListeners(component: ImageComponent) {
        component.onPropertyChanged(this.componentPropertyChangedEventHandler);
    }

    private unregisterComponentListeners(component: ImageComponent) {
        component.unPropertyChanged(this.componentPropertyChangedEventHandler);
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

        this.imageSelector.onOptionSelected((event: SelectedOptionEvent<ContentSummary>) => {
            if (this.handleSelectorEvents) {
                var option: Option<ContentSummary> = event.getSelectedOption().getOption();
                var imageContent = option.displayValue;
                this.imageComponent.setImage(imageContent.getContentId(), imageContent.getDisplayName());
            }
        });

        this.imageSelector.onOptionDeselected((event: SelectedOptionEvent<ContentSummary>) => {
            if (this.handleSelectorEvents) {
                this.imageComponent.reset();
            }
        });
    }

    private setImage(image: ContentSummary) {
        this.setSelectorValue(image);
        this.setupComponentForm(this.imageComponent);
    }

    getComponentView(): ImageComponentView {
        return this.imageView;
    }

}
