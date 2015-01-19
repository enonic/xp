module app.wizard.page.contextwindow.inspect.region {

    import Content = api.content.Content;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
    import DescriptorKey = api.content.page.DescriptorKey;
    import LayoutComponent = api.content.page.region.LayoutComponent;
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

        private descriptorSelected: DescriptorKey;

        private layoutDescriptorChangedListeners: {(event: LayoutDescriptorChangedEvent): void;}[] = [];

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

        onLayoutDescriptorChanged(listener: {(event: LayoutDescriptorChangedEvent): void;}) {
            this.layoutDescriptorChangedListeners.push(listener);
        }

        unLayoutDescriptorChanged(listener: {(event: LayoutDescriptorChangedEvent): void;}) {
            this.layoutDescriptorChangedListeners = this.layoutDescriptorChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyLayoutDescriptorChanged(layoutView: LayoutComponentView, descriptor: LayoutDescriptor) {
            var event = new LayoutDescriptorChangedEvent(layoutView, descriptor);
            this.layoutDescriptorChangedListeners.forEach((listener) => {
                listener(event);
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
        }

        private initSelectorListeners() {
            this.componentSelector.onOptionSelected((event: OptionSelectedEvent<LayoutDescriptor>) => {

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

            this.componentSelector.onOptionDeselected((option: SelectedOption<LayoutDescriptor>) => {
                this.descriptorSelected = null;
                this.notifyLayoutDescriptorChanged(this.layoutView, null);
            });
        }

    }
}