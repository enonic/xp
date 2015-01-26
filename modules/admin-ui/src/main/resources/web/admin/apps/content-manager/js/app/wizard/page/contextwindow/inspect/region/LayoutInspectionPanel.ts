module app.wizard.page.contextwindow.inspect.region {

    import Content = api.content.Content;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
    import DescriptorKey = api.content.page.DescriptorKey;
    import DescriptorByDisplayNameComparator = api.content.page.DescriptorByDisplayNameComparator;
    import LayoutComponent = api.content.page.region.LayoutComponent;
    import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
    import GetLayoutDescriptorByKeyRequest = api.content.page.region.GetLayoutDescriptorByKeyRequest;
    import GetLayoutDescriptorsByModulesRequest = api.content.page.region.GetLayoutDescriptorsByModulesRequest;
    import Descriptor = api.content.page.Descriptor;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LayoutDescriptorLoader = api.content.page.region.LayoutDescriptorLoader;
    import LayoutDescriptorBuilder = api.content.page.region.LayoutDescriptorBuilder;
    import LayoutDescriptorComboBox = api.content.page.region.LayoutDescriptorComboBox;
    import LayoutDescriptorDropdown = api.content.page.region.LayoutDescriptorDropdown;
    import LayoutDescriptorDropdownConfig = api.content.page.region.LayoutDescriptorDropdownConfig;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;

    export class LayoutInspectionPanel extends DescriptorBasedComponentInspectionPanel<LayoutComponent, LayoutDescriptor> {

        private layoutView: LayoutComponentView;

        private layoutComponent: LayoutComponent;

        private layoutSelector: LayoutDescriptorComboBox;

        private handleSelectorEvents: boolean = true;

        private componentPropertyChangedEventHandler;

        constructor() {
            super(<DescriptorBasedComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-layout icon-xlarge"
            });
        }

        setModel(liveEditModel: LiveEditModel) {

            super.setModel(liveEditModel);

            var descriptorsRequest = new GetLayoutDescriptorsByModulesRequest(liveEditModel.getSiteModel().getModuleKeys());
            var loader = new LayoutDescriptorLoader(descriptorsRequest);
            loader.setComparator(new DescriptorByDisplayNameComparator());
            this.layoutSelector = new LayoutDescriptorComboBox(loader);
            loader.load();

            this.componentPropertyChangedEventHandler = (event: ComponentPropertyChangedEvent) => {

                // Ensure displayed config form and selector option are removed when descriptor is removed
                if (event.getPropertyName() == DescriptorBasedComponent.PROPERTY_DESCRIPTOR) {
                    if (!this.layoutComponent.hasDescriptor()) {
                        this.setupComponentForm(this.layoutComponent, null);
                        this.layoutSelector.setDescriptor(null);
                    }
                }
            };

            this.initSelectorListeners();
            this.appendChild(this.layoutSelector);

            liveEditModel.getSiteModel().onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == SiteModel.PROPERTY_NAME_MODULE_CONFIGS) {
                    descriptorsRequest.setModuleKeys(liveEditModel.getSiteModel().getModuleKeys());
                    loader.load();
                }
            });
        }

        private registerComponentListeners(component: LayoutComponent) {
            component.onPropertyChanged(this.componentPropertyChangedEventHandler);
        }

        private unregisterComponentListeners(component: LayoutComponent) {
            component.unPropertyChanged(this.componentPropertyChangedEventHandler);
        }

        setComponent(component: LayoutComponent, descriptor?: LayoutDescriptor) {

            super.setComponent(component);
            if (descriptor) {
                this.layoutSelector.setDescriptor(descriptor);
            }
        }

        setLayoutComponent(layoutView: LayoutComponentView) {

            if (this.layoutComponent) {
                this.unregisterComponentListeners(this.layoutComponent);
            }

            this.layoutView = layoutView;
            this.layoutComponent = <LayoutComponent> layoutView.getComponent();

            this.setComponent(this.layoutComponent);
            var key: DescriptorKey = this.layoutComponent.getDescriptor();
            if (key) {
                var descriptor: LayoutDescriptor = this.layoutSelector.getDescriptor(key);
                if (descriptor) {
                    this.setSelectorValue(descriptor);
                    this.setupComponentForm(this.layoutComponent, descriptor);
                } else {
                    new GetLayoutDescriptorByKeyRequest(key).sendAndParse().then((descriptor: LayoutDescriptor) => {
                        this.setSelectorValue(descriptor);
                        this.setupComponentForm(this.layoutComponent, descriptor);
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();
                }
            } else {
                this.setSelectorValue(null);
                this.setupComponentForm(this.layoutComponent, null);
            }

            this.registerComponentListeners(this.layoutComponent);
        }

        private setSelectorValue(descriptor: LayoutDescriptor) {
            this.handleSelectorEvents = false;
            this.layoutSelector.setDescriptor(descriptor);
            this.handleSelectorEvents = true;
        }

        private initSelectorListeners() {
            this.layoutSelector.onOptionSelected((event: OptionSelectedEvent<LayoutDescriptor>) => {
                if (this.handleSelectorEvents) {

                    var option: Option<LayoutDescriptor> = event.getOption();

                    var selectedDescriptorKey: DescriptorKey = option.displayValue.getKey();
                    this.layoutComponent.setDescriptor(selectedDescriptorKey, option.displayValue);
                }
            });

            this.layoutSelector.onOptionDeselected((option: SelectedOption<LayoutDescriptor>) => {
                if (this.handleSelectorEvents) {
                    this.layoutComponent.setDescriptor(null, null);
                }

            });
        }
    }
}