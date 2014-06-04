module app.wizard.page.contextwindow.inspect {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import DescriptorKey = api.content.page.DescriptorKey;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import GetLayoutDescriptorsByModulesRequest = api.content.page.layout.GetLayoutDescriptorsByModulesRequest;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LayoutDescriptorLoader = api.content.page.layout.LayoutDescriptorLoader;
    import LayoutDescriptorDropdown = api.content.page.layout.LayoutDescriptorDropdown;
    import LayoutDescriptorDropdownConfig = api.content.page.layout.LayoutDescriptorDropdownConfig;
    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import LayoutView = api.liveedit.layout.LayoutView;

    export interface LayoutInspectionPanelConfig {

        siteTemplate: SiteTemplate;

    }

    export class LayoutInspectionPanel extends DescriptorBasedPageComponentInspectionPanel<LayoutComponent, LayoutDescriptor> {

        private layoutView: LayoutView;

        private layoutComponent: LayoutComponent;

        private descriptorSelected: DescriptorKey;

        private descriptorSelector: LayoutDescriptorDropdown;

        private layoutDescriptorChangedListeners: {(event: LayoutDescriptorChangedEvent): void;}[] = [];

        private layoutDescriptors: {
            [key: string]: LayoutDescriptor;
        };

        constructor(config: LayoutInspectionPanelConfig) {
            super(<PageComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-layout icon-xlarge"
            });

            this.layoutDescriptors = {};

            var descriptorHeader = new api.dom.H6El();
            descriptorHeader.setText("Descriptor:");
            descriptorHeader.addClass("descriptor-header");
            this.appendChild(descriptorHeader);

            var getLayoutDescriptorsRequest = new GetLayoutDescriptorsByModulesRequest(config.siteTemplate.getModules());
            var layoutDescriptorLoader = new LayoutDescriptorLoader(getLayoutDescriptorsRequest);
            this.descriptorSelector = new LayoutDescriptorDropdown("layoutDescriptor", <LayoutDescriptorDropdownConfig>{
                loader: layoutDescriptorLoader
            });

            var descriptorsLoadedHandler = (event: LoadedDataEvent<LayoutDescriptor>) => {

                var layoutDescriptors = event.getData();
                // cache descriptors
                this.layoutDescriptors = {};
                layoutDescriptors.forEach((layoutDescriptor: LayoutDescriptor) => {
                    this.layoutDescriptors[layoutDescriptor.getKey().toString()] = layoutDescriptor;
                });
            };
            layoutDescriptorLoader.onLoadedData(descriptorsLoadedHandler);

            layoutDescriptorLoader.load();
            this.descriptorSelector.onOptionSelected((event: OptionSelectedEvent<LayoutDescriptor>) => {

                var option: Option<LayoutDescriptor> = event.getOption();

                if (this.getComponent()) {
                    var selectedDescriptorKey: DescriptorKey = option.displayValue.getKey();
                    this.layoutComponent.setDescriptor(selectedDescriptorKey);

                    var hasDescriptorChanged = this.descriptorSelected && !this.descriptorSelected.equals(selectedDescriptorKey);
                    this.descriptorSelected = selectedDescriptorKey;
                    if (hasDescriptorChanged) {
                        var selectedDescriptor: LayoutDescriptor = option.displayValue;
                        this.notifyLayoutDescriptorChanged(this.layoutView, selectedDescriptor);
                    }
                }
            });
            this.appendChild(this.descriptorSelector);
        }

        onLayoutDescriptorChanged(listener: {(event: LayoutDescriptorChangedEvent): void;}) {
            this.layoutDescriptorChangedListeners.push(listener);
        }

        unLayoutDescriptorChanged(listener: {(event: LayoutDescriptorChangedEvent): void;}) {
            this.layoutDescriptorChangedListeners = this.layoutDescriptorChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyLayoutDescriptorChanged(layoutView: LayoutView, descriptor: LayoutDescriptor) {
            var event = new LayoutDescriptorChangedEvent(layoutView, descriptor);
            this.layoutDescriptorChangedListeners.forEach((listener) => {
                listener(event);
            });
        }

        getDescriptor(): LayoutDescriptor {
            if (!this.getComponent().hasDescriptor()) {
                return null;
            }
            return this.layoutDescriptors[this.getComponent().getDescriptor().toString()];
        }

        setLayoutComponent(layoutView: LayoutView) {

            this.layoutView = layoutView;
            this.setComponent(layoutView.getPageComponent());
            this.layoutComponent = layoutView.getPageComponent();

            var layoutDescriptor = this.getDescriptor();
            if (layoutDescriptor) {
                this.descriptorSelector.setDescriptor(layoutDescriptor.getKey());
                this.setupComponentForm(layoutView.getPageComponent(), layoutDescriptor);
            }
        }
    }
}