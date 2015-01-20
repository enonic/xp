module app.wizard.page.contextwindow.inspect.region {

    import Content = api.content.Content;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
    import DescriptorKey = api.content.page.DescriptorKey;
    import LayoutComponent = api.content.page.region.LayoutComponent;
    import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
    import GetLayoutDescriptorByKeyRequest = api.content.page.region.GetLayoutDescriptorByKeyRequest;
    import GetLayoutDescriptorsByModulesRequest = api.content.page.region.GetLayoutDescriptorsByModulesRequest;
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

        constructor() {
            super(<DescriptorBasedComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-layout icon-xlarge"
            });
        }

        setModel(liveEditModel: LiveEditModel) {

            super.setModel(liveEditModel);

            var descriptorsRequest = new GetLayoutDescriptorsByModulesRequest(liveEditModel.getSiteModel().getModuleKeys());
            var loader = new LayoutDescriptorLoader(descriptorsRequest);
            this.componentSelector = new LayoutDescriptorComboBox(loader);
            loader.load();

            this.initSelectorListeners();
            this.appendChild(this.componentSelector);

            liveEditModel.getSiteModel().onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == SiteModel.PROPERTY_NAME_MODULE_CONFIGS) {
                    descriptorsRequest.setModuleKeys(liveEditModel.getSiteModel().getModuleKeys());
                    loader.load();
                }
            });
        }

        setLayoutComponent(layoutView: LayoutComponentView) {

            this.layoutView = layoutView;
            this.layoutComponent = <LayoutComponent>layoutView.getComponent();
            if (this.layoutComponent.hasDescriptor()) {
                new GetLayoutDescriptorByKeyRequest(this.layoutComponent.getDescriptor()).sendAndParse().then((descriptor: LayoutDescriptor) => {
                    this.setComponent(this.layoutComponent);
                    this.setSelectorValue(descriptor.getKey().toString());
                    this.setupComponentForm(this.layoutComponent, descriptor);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            }

            this.layoutComponent.onPropertyChanged((event: ComponentPropertyChangedEvent) => {

                // Ensure displayed config form is removed when descriptor is removed
                if (event.getPropertyName() == DescriptorBasedComponent.PROPERTY_DESCRIPTOR) {
                    if (!this.layoutComponent.hasDescriptor()) {
                        this.setupComponentForm(this.layoutComponent, null);
                    }
                }
            });
        }

        private initSelectorListeners() {
            this.componentSelector.onOptionSelected((event: OptionSelectedEvent<LayoutDescriptor>) => {

                var option: Option<LayoutDescriptor> = event.getOption();
                var selectedDescriptorKey: DescriptorKey = option.displayValue.getKey();
                this.layoutComponent.setDescriptor(selectedDescriptorKey, option.displayValue);
            });

            this.componentSelector.onOptionDeselected((option: SelectedOption<LayoutDescriptor>) => {

                this.layoutComponent.setDescriptor(null, null);

            });
        }
    }
}