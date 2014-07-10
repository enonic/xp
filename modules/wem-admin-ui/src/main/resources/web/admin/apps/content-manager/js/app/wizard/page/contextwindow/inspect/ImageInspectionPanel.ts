module app.wizard.page.contextwindow.inspect {

    import DefaultModels = app.wizard.page.DefaultModels;
    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import SiteTemplate = api.content.site.template.SiteTemplate;
    import ImageComponent = api.content.page.image.ImageComponent;
    import PageComponentView = api.liveedit.PageComponentView;
    import ImageComponentView = api.liveedit.image.ImageComponentView;
    import ImageDescriptor = api.content.page.image.ImageDescriptor;
    import GetImageDescriptorsByModulesRequest = api.content.page.image.GetImageDescriptorsByModulesRequest;
    import ImageDescriptorLoader = api.content.page.image.ImageDescriptorLoader;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import ImageDescriptorDropdown = api.content.page.image.ImageDescriptorDropdown;
    import ImageDescriptorDropdownConfig = api.content.page.image.ImageDescriptorDropdownConfig;
    import DescriptorKey = api.content.page.DescriptorKey;
    import Descriptor = api.content.page.Descriptor;
    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export interface ImageInspectionPanelConfig {

        siteTemplate: SiteTemplate;

        defaultModels: DefaultModels;
    }

    export class ImageInspectionPanel extends DescriptorBasedPageComponentInspectionPanel<ImageComponent, ImageDescriptor> {

        private imageComponent: ImageComponent;

        private imageView: ImageComponentView;

        private descriptorSelected: DescriptorKey;

        private descriptorSelector: ImageDescriptorDropdown;

        private imageDescriptorChangedListeners: {(event: ImageDescriptorChangedEvent): void;}[] = [];

        private imageDescriptors: {
            [key: string]: ImageDescriptor;
        };

        constructor(config: ImageInspectionPanelConfig) {
            super(<PageComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-image icon-xlarge"
            });
            this.imageDescriptors = {};

            var imageDescriptorsRequest = new GetImageDescriptorsByModulesRequest(config.siteTemplate.getModules());
            var imageDescriptorLoader = new ImageDescriptorLoader(imageDescriptorsRequest);
            this.descriptorSelector = new ImageDescriptorDropdown("imageDescriptor", <ImageDescriptorDropdownConfig>{
                loader: imageDescriptorLoader
            });

            var descriptorLabel = new api.dom.LabelEl("Descriptor", this.descriptorSelector, "descriptor-header");
            this.appendChild(descriptorLabel);

            var descriptorsLoadedHandler = (event: LoadedDataEvent<ImageDescriptor>) => {

                var imageDescriptors = event.getData();
                // cache descriptors
                this.imageDescriptors = {};
                imageDescriptors.forEach((imageDescriptor: ImageDescriptor) => {
                    this.imageDescriptors[imageDescriptor.getKey().toString()] = imageDescriptor;
                });
                // set default descriptor
                if (config.defaultModels.getImageDescriptor()) {
                    this.descriptorSelector.setDescriptor(config.defaultModels.getImageDescriptor().getKey());
                }
            };
            imageDescriptorLoader.onLoadedData(descriptorsLoadedHandler);

            imageDescriptorLoader.load();
            this.descriptorSelector.onOptionSelected((event: OptionSelectedEvent<ImageDescriptor>) => {

                var option: Option<ImageDescriptor> = event.getOption();

                if (this.getComponent()) {
                    var selectedDescriptorKey: DescriptorKey = option.displayValue.getKey();
                    this.imageComponent.setDescriptor(selectedDescriptorKey);

                    var hasDescriptorChanged = this.descriptorSelected && !this.descriptorSelected.equals(selectedDescriptorKey);
                    this.descriptorSelected = selectedDescriptorKey;
                    if (hasDescriptorChanged) {
                        var selectedDescriptor: ImageDescriptor = option.displayValue;
                        this.notifyImageDescriptorChanged(this.imageView, selectedDescriptor);
                    }
                }
            });
            this.appendChild(this.descriptorSelector);
        }

        setComponent(component: ImageComponent) {
            super.setComponent(component);

            this.setMainName(component.getName().toString());
        }

        onImageDescriptorChanged(listener: {(event: ImageDescriptorChangedEvent): void;}) {
            this.imageDescriptorChangedListeners.push(listener);
        }

        unImageDescriptorChanged(listener: {(event: ImageDescriptorChangedEvent): void;}) {
            this.imageDescriptorChangedListeners = this.imageDescriptorChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyImageDescriptorChanged(imageView: ImageComponentView, descriptor: ImageDescriptor) {
            var event = new ImageDescriptorChangedEvent(imageView, descriptor);
            this.imageDescriptorChangedListeners.forEach((listener) => {
                listener(event);
            });
        }

        getDescriptor(): ImageDescriptor {
            if (!this.getComponent().hasDescriptor()) {
                return null;
            }
            return this.imageDescriptors[this.getComponent().getDescriptor().toString()];
        }

        setImageComponent(imageView: ImageComponentView) {
            this.setComponent(imageView.getPageComponent());
            this.imageView = imageView;
            this.imageComponent = imageView.getPageComponent();

            var descriptor = this.getDescriptor();
            if (descriptor) {

                this.descriptorSelector.setDescriptor(descriptor.getKey());
                this.setupComponentForm(imageView.getPageComponent(), descriptor);
            }
        }

        getPageComponentView(): ImageComponentView {
            return this.imageView;
        }

    }
}