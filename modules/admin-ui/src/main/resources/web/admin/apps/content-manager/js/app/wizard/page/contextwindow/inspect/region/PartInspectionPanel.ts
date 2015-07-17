module app.wizard.page.contextwindow.inspect.region {

    import SiteModel = api.content.site.SiteModel;
    import PartDescriptor = api.content.page.region.PartDescriptor;
    import PartDescriptorLoader = api.content.page.region.PartDescriptorLoader;
    import GetPartDescriptorsByModulesRequest = api.content.page.region.GetPartDescriptorsByModulesRequest;
    import GetPartDescriptorByKeyRequest = api.content.page.region.GetPartDescriptorByKeyRequest;
    import PartComponent = api.content.page.region.PartComponent;
    import PartDescriptorDropdown = api.content.page.region.PartDescriptorDropdown;
    import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;
    import ComponentPropertyChangedEvent = api.content.page.region.ComponentPropertyChangedEvent;
    import DescriptorKey = api.content.page.DescriptorKey;
    import Descriptor = api.content.page.Descriptor;
    import PartComponentView = api.liveedit.part.PartComponentView;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class PartInspectionPanel extends DescriptorBasedComponentInspectionPanel<PartComponent, PartDescriptor> {

        private partView: PartComponentView;

        private partComponent: PartComponent;

        private partSelector: PartDescriptorDropdown;

        private partForm: DescriptorBasedDropdownForm;

        private handleSelectorEvents: boolean = true;

        private componentPropertyChangedEventHandler;

        constructor() {
            super(<DescriptorBasedComponentInspectionPanelConfig>{
                iconClass: "live-edit-font-icon-part icon-xlarge"
            });
        }

        setModel(liveEditModel: LiveEditModel) {
            super.setModel(liveEditModel);

            var descriptorsRequest = new GetPartDescriptorsByModulesRequest(liveEditModel.getSiteModel().getApplicationKeys());
            var loader = new PartDescriptorLoader(descriptorsRequest);
            loader.setComparator(new api.content.page.DescriptorByDisplayNameComparator());
            this.partSelector = new PartDescriptorDropdown("", {loader: loader});
            this.partForm = new DescriptorBasedDropdownForm(this.partSelector, "Part");
            loader.load();

            this.componentPropertyChangedEventHandler = (event: ComponentPropertyChangedEvent) => {

                // Ensure displayed config form and selector option are removed when descriptor is removed
                if (event.getPropertyName() == DescriptorBasedComponent.PROPERTY_DESCRIPTOR) {
                    if (!this.partComponent.hasDescriptor()) {
                        this.setupComponentForm(this.partComponent, null);
                        this.partSelector.setDescriptor(null);
                    }
                }
            };

            this.initSelectorListeners();
            this.appendChild(this.partForm);

        }

        setComponent(component: PartComponent, descriptor?: PartDescriptor) {

            super.setComponent(component);
            this.partSelector.setDescriptor(descriptor);
        }

        private setSelectorValue(descriptor: PartDescriptor) {
            this.handleSelectorEvents = false;
            this.partSelector.setDescriptor(descriptor);
            this.handleSelectorEvents = true;
        }

        private registerComponentListeners(component: PartComponent) {
            component.onPropertyChanged(this.componentPropertyChangedEventHandler);
        }

        private unregisterComponentListeners(component: PartComponent) {
            component.unPropertyChanged(this.componentPropertyChangedEventHandler);
        }

        setPartComponent(partView: PartComponentView) {

            if (this.partComponent) {
                this.unregisterComponentListeners(this.partComponent);
            }

            this.partView = partView;
            this.partComponent = <PartComponent>partView.getComponent();

            this.setComponent(this.partComponent);
            var key: DescriptorKey = this.partComponent.getDescriptor();
            if (key) {
                var descriptor: PartDescriptor = this.partSelector.getDescriptor(key);
                if (descriptor) {
                    this.setSelectorValue(descriptor);
                    this.setupComponentForm(this.partComponent, descriptor);
                } else {
                    new GetPartDescriptorByKeyRequest(key).sendAndParse().then((descriptor: PartDescriptor) => {
                        this.setSelectorValue(descriptor);
                        this.setupComponentForm(this.partComponent, descriptor);
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();
                }
            } else {
                this.setSelectorValue(null);
                this.setupComponentForm(this.partComponent, null);
            }

            this.registerComponentListeners(this.partComponent);
        }

        private initSelectorListeners() {

            this.partSelector.onOptionSelected((event: OptionSelectedEvent<PartDescriptor>) => {
                if (this.handleSelectorEvents) {
                    var option: Option<PartDescriptor> = event.getOption();
                    var selectedDescriptorKey: DescriptorKey = option.displayValue.getKey();
                    this.partComponent.setDescriptor(selectedDescriptorKey, option.displayValue);
                }
            });
        }
    }
}