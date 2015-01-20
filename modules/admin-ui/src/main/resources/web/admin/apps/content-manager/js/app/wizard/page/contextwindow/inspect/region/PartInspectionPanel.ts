module app.wizard.page.contextwindow.inspect.region {

    import SiteModel = api.content.site.SiteModel;
    import PartDescriptor = api.content.page.region.PartDescriptor;
    import PartDescriptorComboBox = api.content.page.region.PartDescriptorComboBox;
    import PartDescriptorLoader = api.content.page.region.PartDescriptorLoader;
    import GetPartDescriptorsByModulesRequest = api.content.page.region.GetPartDescriptorsByModulesRequest;
    import GetPartDescriptorByKeyRequest = api.content.page.region.GetPartDescriptorByKeyRequest;
    import PartComponent = api.content.page.region.PartComponent;
    import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
    import DescriptorKey = api.content.page.DescriptorKey;
    import PartComponentView = api.liveedit.part.PartComponentView;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class PartInspectionPanel extends DescriptorBasedComponentInspectionPanel<PartComponent, PartDescriptor> {

        private partView: PartComponentView;

        private partComponent: PartComponent;

        constructor() {
            super(<DescriptorBasedComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-part icon-xlarge"
            });
        }

        setModel(liveEditModel: LiveEditModel) {
            super.setModel(liveEditModel);

            var descriptorsRequest = new GetPartDescriptorsByModulesRequest(liveEditModel.getSiteModel().getModuleKeys());
            var loader = new PartDescriptorLoader(descriptorsRequest);
            this.componentSelector = new PartDescriptorComboBox(loader);
            loader.load();

            this.initSelectorListeners();
            this.appendChild(this.componentSelector);

        }

        setPartComponent(partView: PartComponentView) {

            this.partView = partView;
            this.partComponent = <PartComponent>partView.getComponent();
            if (this.partComponent.hasDescriptor()) {
                new GetPartDescriptorByKeyRequest(this.partComponent.getDescriptor()).sendAndParse().then((descriptor: PartDescriptor) => {
                    this.setComponent(this.partComponent);
                    this.setSelectorValue(descriptor.getKey().toString());
                    this.setupComponentForm(this.partComponent, descriptor);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            }

            this.partComponent.onPropertyChanged((event: ComponentPropertyChangedEvent) => {

                // Ensure displayed config form is removed when descriptor is removed
                if (event.getPropertyName() == DescriptorBasedComponent.PROPERTY_DESCRIPTOR) {
                    if (!this.partComponent.hasDescriptor()) {
                        this.setupComponentForm(this.partComponent, null);
                    }
                }
            });
        }

        private initSelectorListeners() {

            this.componentSelector.onOptionSelected((event: OptionSelectedEvent<PartDescriptor>) => {

                var option: Option<PartDescriptor> = event.getOption();

                var selectedDescriptorKey: DescriptorKey = option.displayValue.getKey();
                this.partComponent.setDescriptor(selectedDescriptorKey, option.displayValue);
            });

            this.componentSelector.onOptionDeselected((option: SelectedOption<PartDescriptor>) => {

                this.partComponent.setDescriptor(null, null);
            });
        }
    }
}