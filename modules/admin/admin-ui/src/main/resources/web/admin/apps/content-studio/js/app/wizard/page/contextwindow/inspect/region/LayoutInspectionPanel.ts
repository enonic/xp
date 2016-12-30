import "../../../../../../api.ts";
import {
    DescriptorBasedComponentInspectionPanel,
    DescriptorBasedComponentInspectionPanelConfig
} from "./DescriptorBasedComponentInspectionPanel";
import {DescriptorBasedDropdownForm} from "./DescriptorBasedDropdownForm";

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
import GetLayoutDescriptorsByApplicationsRequest = api.content.page.region.GetLayoutDescriptorsByApplicationsRequest;
import Descriptor = api.content.page.Descriptor;
import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
import LayoutDescriptorLoader = api.content.page.region.LayoutDescriptorLoader;
import LayoutDescriptorBuilder = api.content.page.region.LayoutDescriptorBuilder;
import LayoutDescriptorDropdown = api.content.page.region.LayoutDescriptorDropdown;
import Option = api.ui.selector.Option;
import SelectedOption = api.ui.selector.combobox.SelectedOption;
import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
import LayoutComponentView = api.liveedit.layout.LayoutComponentView;

export class LayoutInspectionPanel extends DescriptorBasedComponentInspectionPanel<LayoutComponent, LayoutDescriptor> {

    private layoutView: LayoutComponentView;

    private layoutComponent: LayoutComponent;

    private layoutForm: DescriptorBasedDropdownForm;

    private handleSelectorEvents: boolean = true;

    private componentPropertyChangedEventHandler: (event: ComponentPropertyChangedEvent) => void;

    protected selector: LayoutDescriptorDropdown;

    constructor() {
        super(<DescriptorBasedComponentInspectionPanelConfig>{
            iconClass: api.liveedit.ItemViewIconClassResolver.resolveByType("layout", "icon-xlarge")
        });
    }

    protected layout() {

        this.removeChildren();

        this.selector = new LayoutDescriptorDropdown();
        this.layoutForm = new DescriptorBasedDropdownForm(this.selector, "Layout");

        this.selector.loadDescriptors(this.liveEditModel.getSiteModel().getApplicationKeys());

        this.componentPropertyChangedEventHandler = (event: ComponentPropertyChangedEvent) => {

            // Ensure displayed config form and selector option are removed when descriptor is removed
            if (event.getPropertyName() == DescriptorBasedComponent.PROPERTY_DESCRIPTOR) {
                if (!this.layoutComponent.hasDescriptor()) {
                    this.setSelectorValue(null, false);
                }
            }
        };

        this.initSelectorListeners();
        this.appendChild(this.layoutForm);

    }

    protected reloadDescriptorsOnApplicationChange() {
        if(this.selector) {
            this.selector.loadDescriptors(this.liveEditModel.getSiteModel().getApplicationKeys());
        }
    }

    protected applicationUnavailableHandler() {
        this.selector.hideDropdown();
    }

    private registerComponentListeners(component: LayoutComponent) {
        component.onPropertyChanged(this.componentPropertyChangedEventHandler);
    }

    private unregisterComponentListeners(component: LayoutComponent) {
        component.unPropertyChanged(this.componentPropertyChangedEventHandler);
    }

    setComponent(component: LayoutComponent, descriptor?: LayoutDescriptor) {

        super.setComponent(component);
        this.selector.setDescriptor(descriptor);
    }

    setLayoutComponent(layoutView: LayoutComponentView) {

        if (this.layoutComponent) {
            this.unregisterComponentListeners(this.layoutComponent);
        }

        this.layoutView = layoutView;
        this.layoutComponent = <LayoutComponent> layoutView.getComponent();

        this.setComponent(this.layoutComponent);
        const key: DescriptorKey = this.layoutComponent.getDescriptor();
        if (key) {
            const descriptor: LayoutDescriptor = this.selector.getDescriptor(key);
            if (descriptor) {
                this.setSelectorValue(descriptor);
            } else {
                new GetLayoutDescriptorByKeyRequest(key).sendAndParse().then((receivedDescriptor: LayoutDescriptor) => {
                    this.setSelectorValue(receivedDescriptor);
                }).catch((reason: any) => {
                    if (this.isNotFoundError(reason)) {
                        this.setSelectorValue(null);
                    } else {
                        api.DefaultErrorHandler.handle(reason);
                    }
                }).done();
            }
        } else {
            this.setSelectorValue(null);
        }

        this.registerComponentListeners(this.layoutComponent);
    }

    private setSelectorValue(descriptor: LayoutDescriptor, silent: boolean = true) {
        if (silent) {
            this.handleSelectorEvents = false;
        }

        this.selector.setDescriptor(descriptor);
        this.setupComponentForm(this.layoutComponent, descriptor);

        this.handleSelectorEvents = true;
    }

    private initSelectorListeners() {
        this.selector.onOptionSelected((event: OptionSelectedEvent<LayoutDescriptor>) => {
            if (this.handleSelectorEvents) {

                let option: Option<LayoutDescriptor> = event.getOption();

                let selectedDescriptorKey: DescriptorKey = option.displayValue.getKey();
                this.layoutComponent.setDescriptor(selectedDescriptorKey, option.displayValue);
            }
        });
    }
}
