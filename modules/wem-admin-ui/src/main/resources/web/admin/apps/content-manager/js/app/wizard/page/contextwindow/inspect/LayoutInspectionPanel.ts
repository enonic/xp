module app.wizard.page.contextwindow.inspect {

    import Content = api.content.Content;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import LayoutDescriptor = api.content.page.layout.LayoutDescriptor;
    import DescriptorKey = api.content.page.DescriptorKey;
    import LayoutComponent = api.content.page.layout.LayoutComponent;
    import GetLayoutDescriptorByKeyRequest = api.content.page.layout.GetLayoutDescriptorByKeyRequest;
    import GetLayoutDescriptorsByModulesRequest = api.content.page.layout.GetLayoutDescriptorsByModulesRequest;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LayoutDescriptorLoader = api.content.page.layout.LayoutDescriptorLoader;
    import LayoutDescriptorDropdown = api.content.page.layout.LayoutDescriptorDropdown;
    import LayoutDescriptorDropdownConfig = api.content.page.layout.LayoutDescriptorDropdownConfig;
    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import LayoutComponentView = api.liveedit.layout.LayoutComponentView;

    export class LayoutInspectionPanel extends DescriptorBasedComponentInspectionPanel<LayoutComponent, LayoutDescriptor> {

        private layoutView: LayoutComponentView;

        private layoutComponent: LayoutComponent;

        private descriptorSelected: DescriptorKey;

        private descriptorSelector: LayoutDescriptorDropdown;

        private layoutDescriptorChangedListeners: {(event: LayoutDescriptorChangedEvent): void;}[] = [];

        constructor() {
            super(<DescriptorBasedComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-layout icon-xlarge"
            });

        }

        setModel(liveEditModel: LiveEditModel) {

            var descriptorsRequest = new GetLayoutDescriptorsByModulesRequest(liveEditModel.getSiteModel().getModuleKeys());
            var loader = new LayoutDescriptorLoader(descriptorsRequest);
            this.descriptorSelector = new LayoutDescriptorDropdown("layoutDescriptor", <LayoutDescriptorDropdownConfig>{
                loader: loader
            });
            var descriptorLabel = new api.dom.LabelEl("Descriptor", this.descriptorSelector, "descriptor-header");
            this.appendChild(descriptorLabel);
            loader.load();
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
                    this.descriptorSelector.setDescriptor(descriptor.getKey());
                    this.setupComponentForm(this.layoutComponent, descriptor);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            }
        }
    }
}