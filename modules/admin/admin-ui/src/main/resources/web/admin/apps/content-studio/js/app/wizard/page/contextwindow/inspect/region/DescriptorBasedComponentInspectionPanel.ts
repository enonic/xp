import '../../../../../../api.ts';
import {ComponentInspectionPanel, ComponentInspectionPanelConfig} from './ComponentInspectionPanel';

import FormView = api.form.FormView;
import DescriptorBasedComponent = api.content.page.region.DescriptorBasedComponent;
import DescriptorKey = api.content.page.DescriptorKey;
import Descriptor = api.content.page.Descriptor;
import LiveEditModel = api.liveedit.LiveEditModel;
import SiteModel = api.content.site.SiteModel;
import DescriptorBasedDropdown = api.content.page.region.DescriptorBasedDropdown;
import GetLayoutDescriptorsByApplicationsRequest = api.content.page.region.GetLayoutDescriptorsByApplicationsRequest;
import GetPartDescriptorsByApplicationsRequest = api.content.page.region.GetPartDescriptorsByApplicationsRequest;

export interface DescriptorBasedComponentInspectionPanelConfig extends ComponentInspectionPanelConfig {

}

export class DescriptorBasedComponentInspectionPanel<COMPONENT extends DescriptorBasedComponent, DESCRIPTOR extends Descriptor>
extends ComponentInspectionPanel<COMPONENT> {

    private formView: FormView;

    protected selector: DescriptorBasedDropdown<DESCRIPTOR>;

    constructor(config: DescriptorBasedComponentInspectionPanelConfig) {
        super(config);

        this.formView = null;
    }

    setModel(liveEditModel: LiveEditModel) {

        if (this.liveEditModel !== liveEditModel) {
            if (this.liveEditModel !== null && this.liveEditModel.getSiteModel() !== null) {
                let siteModel = this.liveEditModel.getSiteModel();

                siteModel.unApplicationUnavailable(this.applicationUnavailableHandler.bind(this));
                siteModel.unApplicationAdded(this.reloadDescriptorsOnApplicationChange.bind(this));
                siteModel.unApplicationRemoved(this.reloadDescriptorsOnApplicationChange.bind(this));
            }

            super.setModel(liveEditModel);
            this.layout();

            liveEditModel.getSiteModel().onApplicationUnavailable(this.applicationUnavailableHandler.bind(this));
            liveEditModel.getSiteModel().onApplicationAdded(this.reloadDescriptorsOnApplicationChange.bind(this));
            liveEditModel.getSiteModel().onApplicationRemoved(this.reloadDescriptorsOnApplicationChange.bind(this));
        }
    }

    protected layout() {
        throw new Error('Must be implemented in inheritors');
    }

    protected applicationUnavailableHandler() {
        this.selector.hideDropdown();
    }

    protected reloadDescriptorsOnApplicationChange() {
        this.selector.load();
    }

    setupComponentForm(component: DescriptorBasedComponent, descriptor: Descriptor) {
        if (this.formView) {
            if (this.hasChild(this.formView)) {
                this.removeChild(this.formView);
            }
            this.formView = null;
        }
        if (!component || !descriptor) {
            return;
        }

        let form = descriptor.getConfig();
        let config = component.getConfig();
        this.formView = new FormView(this.formContext, form, config.getRoot());
        this.appendChild(this.formView);
        component.setDisableEventForwarding(true);
        this.formView.layout().catch((reason: any) => {
            api.DefaultErrorHandler.handle(reason);
        }).finally(() => {
            component.setDisableEventForwarding(false);
        }).done();
    }
}
