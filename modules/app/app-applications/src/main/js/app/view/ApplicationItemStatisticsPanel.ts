import '../../api.ts';
import {ApplicationBrowseActions} from '../browse/ApplicationBrowseActions';

import ContentTypeSummary = api.schema.content.ContentTypeSummary;
import Mixin = api.schema.mixin.Mixin;
import RelationshipType = api.schema.relationshiptype.RelationshipType;
import RelationshipTypeName = api.schema.relationshiptype.RelationshipTypeName;
import PageDescriptor = api.content.page.PageDescriptor;
import PartDescriptor = api.content.page.region.PartDescriptor;
import LayoutDescriptor = api.content.page.region.LayoutDescriptor;
import ItemDataGroup = api.app.view.ItemDataGroup;
import ApplicationKey = api.application.ApplicationKey;
import Application = api.application.Application;
import MacroDescriptor = api.macro.MacroDescriptor;

export class ApplicationItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.application.Application> {

    private applicationDataContainer: api.dom.DivEl;
    private actionMenu: api.ui.menu.ActionMenu;

    constructor() {
        super('application-item-statistics-panel');

        this.actionMenu =
            new api.ui.menu.ActionMenu('Stopped', ApplicationBrowseActions.get().START_APPLICATION,
                ApplicationBrowseActions.get().STOP_APPLICATION);

        this.appendChild(this.actionMenu);

        this.applicationDataContainer = new api.dom.DivEl('application-data-container');
        this.appendChild(this.applicationDataContainer);
    }

    setItem(item: api.app.view.ViewItem<api.application.Application>) {
        let currentItem = this.getItem();

        if (currentItem && currentItem.equals(item)) {
            // do nothing in case item has not changed
            return;
        }

        super.setItem(item);
        let currentApplication = item.getModel();

        if (currentApplication.getIconUrl()) {
            this.getHeader().setIconUrl(currentApplication.getIconUrl());
        }

        if (currentApplication.getDescription()) {
            this.getHeader().setHeaderSubtitle(currentApplication.getDescription(), 'app-description');
        }

        this.actionMenu.setLabel(api.util.StringHelper.capitalize(currentApplication.getState()));

        if (currentApplication.isStarted()) {
            ApplicationBrowseActions.get().START_APPLICATION.setEnabled(false);
            ApplicationBrowseActions.get().STOP_APPLICATION.setEnabled(true);
        } else {
            ApplicationBrowseActions.get().START_APPLICATION.setEnabled(true);
            ApplicationBrowseActions.get().STOP_APPLICATION.setEnabled(false);
        }

        this.applicationDataContainer.removeChildren();

        let infoGroup = new ItemDataGroup('Info', 'info');
        infoGroup.addDataList('Build date', 'TBA');
        infoGroup.addDataList('Version', currentApplication.getVersion());
        infoGroup.addDataList('Key', currentApplication.getApplicationKey().toString());
        infoGroup.addDataList('System Required',
            '>= ' + currentApplication.getMinSystemVersion() + ' and < ' + currentApplication.getMaxSystemVersion());

        let descriptorResponse = this.initDescriptors(currentApplication.getApplicationKey());
        let schemaResponse = this.initSchemas(currentApplication.getApplicationKey());
        let macroResponse = this.initMacros(currentApplication.getApplicationKey());
        let providerResponse = this.initProviders(currentApplication.getApplicationKey());

        wemQ.all([descriptorResponse, schemaResponse, macroResponse, providerResponse])
            .spread((descriptorsGroup, schemasGroup, macrosGroup, providersGroup) => {
                if (!infoGroup.isEmpty()) {
                    this.applicationDataContainer.appendChild(infoGroup);
                }
                if (descriptorsGroup && !descriptorsGroup.isEmpty()) {
                    this.applicationDataContainer.appendChild(descriptorsGroup);
                }

                if (schemasGroup && !schemasGroup.isEmpty()) {
                    this.applicationDataContainer.appendChild(schemasGroup);
                }

                if (macrosGroup && !macrosGroup.isEmpty()) {
                    this.applicationDataContainer.appendChild(macrosGroup);
                }

                if (providersGroup && !providersGroup.isEmpty()) {
                    this.applicationDataContainer.appendChild(providersGroup);
                }
            });

    }

    private initMacros(applicationKey: ApplicationKey): wemQ.Promise<any> {
        let macroRequest = new api.macro.resource.GetMacrosRequest();
        macroRequest.setApplicationKeys([applicationKey]);

        let macroPromises = [macroRequest.sendAndParse()];

        return wemQ.all(macroPromises).spread((macros: MacroDescriptor[])=> {

            let macrosGroup = new ItemDataGroup('Macros', 'macros');

            let macroNames = macros.
            filter((macro: MacroDescriptor) => {
                return !ApplicationKey.SYSTEM.equals(macro.getKey().getApplicationKey());
            }).map((macro: MacroDescriptor) => {
                return macro.getDisplayName();
            });
            macrosGroup.addDataArray('Name', macroNames);

            return macrosGroup;
        }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
    }

    private initDescriptors(applicationKey: ApplicationKey): wemQ.Promise<any> {

        let descriptorPromises = [
            new api.content.page.GetPageDescriptorsByApplicationRequest(applicationKey).sendAndParse(),
            new api.content.page.region.GetPartDescriptorsByApplicationRequest(applicationKey).sendAndParse(),
            new api.content.page.region.GetLayoutDescriptorsByApplicationRequest(applicationKey).sendAndParse()
        ];

        return wemQ.all(descriptorPromises).spread(
            (pageDescriptors: PageDescriptor[], partDescriptors: PartDescriptor[], layoutDescriptors: LayoutDescriptor[]) => {

                let descriptorsGroup = new ItemDataGroup('Descriptors', 'descriptors');

                let pageNames = pageDescriptors.map((descriptor: PageDescriptor) => descriptor.getName().toString()).sort(
                    this.sortAlphabeticallyAsc);
                descriptorsGroup.addDataArray('Page', pageNames);

                let partNames = partDescriptors.map((descriptor: PartDescriptor) => descriptor.getName().toString()).sort(
                    this.sortAlphabeticallyAsc);
                descriptorsGroup.addDataArray('Part', partNames);

                let layoutNames = layoutDescriptors.map((descriptor: LayoutDescriptor) => descriptor.getName().toString()).sort(
                    this.sortAlphabeticallyAsc);
                descriptorsGroup.addDataArray('Layout', layoutNames);

                return descriptorsGroup;
            }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
    }

    private initSchemas(applicationKey: ApplicationKey): wemQ.Promise<any> {

        let schemaPromises = [
            new api.schema.content.GetContentTypesByApplicationRequest(applicationKey).sendAndParse(),
            new api.schema.mixin.GetMixinsByApplicationRequest(applicationKey).sendAndParse(),
            new api.schema.relationshiptype.GetRelationshipTypesByApplicationRequest(applicationKey).sendAndParse()
        ];

        return wemQ.all(schemaPromises).spread<any>(
            (contentTypes: ContentTypeSummary[], mixins: Mixin[], relationshipTypes: RelationshipType[]) => {
                let schemasGroup = new ItemDataGroup('Schemas', 'schemas');

                let contentTypeNames = contentTypes.map(
                    (contentType: ContentTypeSummary) => contentType.getContentTypeName().getLocalName()).sort(this.sortAlphabeticallyAsc);
                schemasGroup.addDataArray('Content Types', contentTypeNames);

                let mixinsNames = mixins.map((mixin: Mixin) => mixin.getMixinName().getLocalName()).sort(this.sortAlphabeticallyAsc);
                schemasGroup.addDataArray('Mixins', mixinsNames);

                let relationshipTypeNames = relationshipTypes.map(
                    (relationshipType: RelationshipType) => relationshipType.getRelationshiptypeName().getLocalName()).sort(
                    this.sortAlphabeticallyAsc);
                schemasGroup.addDataArray('RelationshipTypes', relationshipTypeNames);

                return schemasGroup;

            }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
    }

    private initProviders(applicationKey: ApplicationKey): wemQ.Promise<ItemDataGroup> {
        let providersPromises = [new api.application.AuthApplicationRequest(applicationKey).sendAndParse()];

        return wemQ.all(providersPromises).spread<ItemDataGroup>(
            (application: Application) => {
                if(application) {
                    let providersGroup = new ItemDataGroup('ID Providers', 'providers');

                    providersGroup.addDataList('Key', application.getApplicationKey().toString());
                    providersGroup.addDataList('Name', application.getDisplayName());

                    return providersGroup;
                }
                return null;
            });
    }

    private sortAlphabeticallyAsc(a: string, b: string): number {
        return a.toLocaleLowerCase().localeCompare(b.toLocaleLowerCase());
    }

}
