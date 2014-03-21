module app.wizard.page.contextwindow.inspect {

    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import SiteTemplate = api.content.site.template.SiteTemplate;
    import ImageComponent = api.content.page.image.ImageComponent;
    import ImageDescriptor = api.content.page.image.ImageDescriptor;
    import GetImageDescriptorsByModulesRequest = api.content.page.image.GetImageDescriptorsByModulesRequest;
    import ImageDescriptorLoader = api.content.page.image.ImageDescriptorLoader;
    import ImageDescriptorComboBox = api.content.page.image.ImageDescriptorComboBox;
    import DescriptorKey = api.content.page.DescriptorKey;
    import Descriptor = api.content.page.Descriptor;
    import Option = api.ui.selector.Option;


    export class ImageInspectionPanel extends PageComponentInspectionPanel<ImageComponent, ImageDescriptor> {

        private imageComponent: ImageComponent;
        private descriptorSelected: DescriptorKey;
        private descriptorComboBox: ImageDescriptorComboBox;
        private imageDescriptors: {
            [key: string]: ImageDescriptor;
        };

        constructor(liveFormPanel: LiveFormPanel, siteTemplate: SiteTemplate) {
            super("live-edit-font-icon-image", liveFormPanel, siteTemplate);
            this.imageDescriptors = {};
            this.initElements();
        }

        private initElements() {
            var descriptorHeader = new api.dom.H6El();
            descriptorHeader.setText("Descriptor:");
            descriptorHeader.addClass("descriptor-header");
            this.appendChild(descriptorHeader);

            var imageDescriptorsRequest = new GetImageDescriptorsByModulesRequest(this.getSiteTemplate().getModules());
            var imageDescriptorLoader = new ImageDescriptorLoader(imageDescriptorsRequest);
            this.descriptorComboBox = new ImageDescriptorComboBox(imageDescriptorLoader);

            var onDescriptorsLoaded = (imageDescriptors:ImageDescriptor[]) => {
                imageDescriptors.forEach((imageDescriptor:ImageDescriptor) => {
                    this.imageDescriptors[imageDescriptor.getKey().toString()] = imageDescriptor;
                })
                this.descriptorComboBox.setDescriptor(this.getLiveFormPanel().getDefaultImageDescriptor().getKey());
                this.descriptorComboBox.removeLoadedListener(onDescriptorsLoaded); // execute only on the first loaded event
            };
            this.descriptorComboBox.addLoadedListener(onDescriptorsLoaded);

            this.descriptorComboBox.addOptionSelectedListener((option: Option<ImageDescriptor>) => {
                if (this.imageComponent) {
                    var selectedDescriptorKey: DescriptorKey = option.displayValue.getKey();
                    this.imageComponent.setDescriptor(selectedDescriptorKey);

                    var hasDescriptorChanged = this.descriptorSelected && !this.descriptorSelected.equals(selectedDescriptorKey);
                    this.descriptorSelected = selectedDescriptorKey;
                    if (hasDescriptorChanged) {
                        var path = this.imageComponent.getPath();
                        var component = this.getLiveFormPanel().getLiveEditWindow().getComponentByPath(path.toString());
                        var selectedDescriptor: Descriptor = option.displayValue;
                        this.getLiveFormPanel().setComponentDescriptor(selectedDescriptor, path, component);
                    }
                }
            });
            this.appendChild(this.descriptorComboBox);
        }

        getDescriptor(key: DescriptorKey): ImageDescriptor {
            return this.imageDescriptors[key.toString()];
        }

        setImageComponent(component: ImageComponent) {
            this.setComponent(component);
            this.imageComponent = component;

            var descriptorKey = component.getDescriptor();
            if (descriptorKey) {
                this.descriptorComboBox.setDescriptor(descriptorKey);
                var imageDescriptorOption: Option<ImageDescriptor> = this.descriptorComboBox.getSelectedOptions()[0];
                var imageDescriptor = imageDescriptorOption.displayValue;
                this.setupComponentForm(component, imageDescriptor);
            }
        }

    }
}