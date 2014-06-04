module app.wizard.page.contextwindow.inspect {

    import DefaultModels = app.wizard.page.DefaultModels;
    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import SiteTemplate = api.content.site.template.SiteTemplate;
    import ImageView = api.liveedit.image.ImageView;
    import ImageComponent = api.content.page.image.ImageComponent;
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

        private imageView: ImageView;

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

            var descriptorHeader = new api.dom.H6El();
            descriptorHeader.setText("Descriptor:");
            descriptorHeader.addClass("descriptor-header");
            this.appendChild(descriptorHeader);


            var imageDescriptorsRequest = new GetImageDescriptorsByModulesRequest(config.siteTemplate.getModules());
            var imageDescriptorLoader = new ImageDescriptorLoader(imageDescriptorsRequest);
            this.descriptorSelector = new ImageDescriptorDropdown("imageDescriptor", <ImageDescriptorDropdownConfig>{
                loader: imageDescriptorLoader
            });

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
                    this.getComponent().setDescriptor(selectedDescriptorKey);

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

        onImageDescriptorChanged(listener: {(event: ImageDescriptorChangedEvent): void;}) {
            this.imageDescriptorChangedListeners.push(listener);
        }

        unImageDescriptorChanged(listener: {(event: ImageDescriptorChangedEvent): void;}) {
            this.imageDescriptorChangedListeners = this.imageDescriptorChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyImageDescriptorChanged(imageView: ImageView, descriptor: ImageDescriptor) {
            var event = new ImageDescriptorChangedEvent(imageView, descriptor);
            this.imageDescriptorChangedListeners.forEach((listener) => listener(event));
        }

        getDescriptor(): ImageDescriptor {
            if (!this.getComponent().hasDescriptor()) {
                return null;
            }
            return this.imageDescriptors[this.getComponent().getDescriptor().toString()];
        }

        setImageView(view: ImageView) {
            this.setComponent(view.getPageComponent());
            this.imageView = view;

            var descriptor = this.getDescriptor();
            if (descriptor) {

                this.descriptorSelector.setDescriptor(descriptor.getKey());
                this.setupComponentForm(view.getPageComponent(), descriptor);
            }
        }

    }
}