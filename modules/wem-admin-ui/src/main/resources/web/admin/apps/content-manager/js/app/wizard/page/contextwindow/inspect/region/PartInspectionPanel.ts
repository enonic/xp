module app.wizard.page.contextwindow.inspect.region {

    import SiteModel = api.content.site.SiteModel;
    import PartDescriptor = api.content.page.part.PartDescriptor;
    import PartDescriptorComboBox = api.content.page.part.PartDescriptorComboBox;
    import PartDescriptorLoader = api.content.page.part.PartDescriptorLoader;
    import PartDescriptorDropdown = api.content.page.part.PartDescriptorDropdown;
    import GetPartDescriptorsByModulesRequest = api.content.page.part.GetPartDescriptorsByModulesRequest;
    import GetPartDescriptorByKeyRequest = api.content.page.part.GetPartDescriptorByKeyRequest;
    import PartComponent = api.content.page.part.PartComponent;
    import PartDescriptorDropdownConfig = api.content.page.part.PartDescriptorDropdownConfig;
    import DescriptorKey = api.content.page.DescriptorKey;
    import PartComponentView = api.liveedit.part.PartComponentView;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class PartInspectionPanel extends DescriptorBasedComponentInspectionPanel<PartComponent, PartDescriptor> {

        private partView: PartComponentView;

        private liveEditModel: LiveEditModel;

        private partComponent: PartComponent;

        private descriptorSelected: DescriptorKey;

        private descriptorSelector: PartDescriptorDropdown;

        private partDescriptorChangedListeners: {(event: PartDescriptorChangedEvent): void;}[] = [];

        constructor() {
            super(<DescriptorBasedComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-part icon-xlarge"
            });
        }

        setModel(liveEditModel: LiveEditModel) {
            this.liveEditModel = liveEditModel;

            var descriptorsRequest = new GetPartDescriptorsByModulesRequest(liveEditModel.getSiteModel().getModuleKeys());
            var loader = new PartDescriptorLoader(descriptorsRequest);
            this.descriptorSelector = new PartDescriptorDropdown("layoutDescriptor", <PartDescriptorDropdownConfig>{
                loader: loader
            });
            loader.load();

            this.descriptorSelector.onOptionSelected((event: OptionSelectedEvent<PartDescriptor>) => {

                var option: Option<PartDescriptor> = event.getOption();

                if (this.getComponent()) {
                    var selectedDescriptorKey: DescriptorKey = option.displayValue.getKey();
                    this.partComponent.setDescriptor(selectedDescriptorKey);

                    var hasDescriptorChanged = selectedDescriptorKey && !selectedDescriptorKey.equals(this.descriptorSelected);
                    this.descriptorSelected = selectedDescriptorKey;
                    if (hasDescriptorChanged) {
                        var selectedDescriptor: PartDescriptor = option.displayValue;
                        this.notifyPartDescriptorChanged(this.partView, selectedDescriptor);
                    }
                }
            });
            this.appendChild(this.descriptorSelector);

        }

        onPartDescriptorChanged(listener: {(event: PartDescriptorChangedEvent): void;}) {
            this.partDescriptorChangedListeners.push(listener);
        }

        unPartDescriptorChanged(listener: {(event: PartDescriptorChangedEvent): void;}) {
            this.partDescriptorChangedListeners = this.partDescriptorChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyPartDescriptorChanged(partView: PartComponentView, descriptor: PartDescriptor) {
            var event = new PartDescriptorChangedEvent(partView, descriptor);
            this.partDescriptorChangedListeners.forEach((listener) => {
                listener(event);
            });
        }

        setPartComponent(partView: PartComponentView) {

            this.partView = partView;
            this.partComponent = <PartComponent>partView.getComponent();
            if (this.partComponent.hasDescriptor()) {
                new GetPartDescriptorByKeyRequest(this.partComponent.getDescriptor()).sendAndParse().then((descriptor: PartDescriptor) => {
                    this.setComponent(this.partComponent);
                    this.descriptorSelector.setDescriptor(descriptor.getKey());
                    this.setupComponentForm(this.partComponent, descriptor);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            }
        }
    }
}