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
import i18n = api.util.i18n;
import DivEl = api.dom.DivEl;

export class ApplicationItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.application.Application> {

    private applicationDataContainer: api.dom.DivEl;
    private actionMenu: api.ui.menu.ActionMenu;

    constructor() {
        super('application-item-statistics-panel');

        this.addActionMenu();
        this.addApplicationDataContainer();
    }

    private addActionMenu() {
        this.actionMenu =
            new api.ui.menu.ActionMenu(i18n('application.state.stopped'), ApplicationBrowseActions.get().START_APPLICATION,
                ApplicationBrowseActions.get().STOP_APPLICATION);

        const actionMenuWrapper: DivEl = new DivEl('action-menu-wrapper');
        actionMenuWrapper.appendChild(this.actionMenu);

        this.appendChild(actionMenuWrapper);
    }

    private addApplicationDataContainer() {
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

        this.actionMenu.setLabel(this.getLocalizedState(currentApplication.getState()));

        this.applicationDataContainer.removeChildren();

        const infoGroup = new ItemDataGroup(i18n('field.info'), 'info');
        const minVersion = currentApplication.getMinSystemVersion();
        const maxVersion = currentApplication.getMaxSystemVersion();
        infoGroup.addDataList(i18n('field.buildDate'), 'TBA');
        infoGroup.addDataList(i18n('field.version'), currentApplication.getVersion());
        infoGroup.addDataList(i18n('field.key'), currentApplication.getApplicationKey().toString());
        infoGroup.addDataList(i18n('field.systemRequired'), i18n('field.systemRequired.value', minVersion, maxVersion));

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

            let macrosGroup = new ItemDataGroup(i18n('field.macros'), 'macros');

            let macroNames = macros.
            filter((macro: MacroDescriptor) => {
                return !ApplicationKey.SYSTEM.equals(macro.getKey().getApplicationKey());
            }).map((macro: MacroDescriptor) => {
                return macro.getDisplayName();
            });
            macrosGroup.addDataArray(i18n('field.name'), macroNames);

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

                let descriptorsGroup = new ItemDataGroup(i18n('field.descriptors'), 'descriptors');

                let pageNames = pageDescriptors.map((descriptor: PageDescriptor) => descriptor.getName().toString()).sort(
                    this.sortAlphabeticallyAsc);
                descriptorsGroup.addDataArray(i18n('field.page'), pageNames);

                let partNames = partDescriptors.map((descriptor: PartDescriptor) => descriptor.getName().toString()).sort(
                    this.sortAlphabeticallyAsc);
                descriptorsGroup.addDataArray(i18n('field.part'), partNames);

                let layoutNames = layoutDescriptors.map((descriptor: LayoutDescriptor) => descriptor.getName().toString()).sort(
                    this.sortAlphabeticallyAsc);
                descriptorsGroup.addDataArray(i18n('field.layout'), layoutNames);

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
                let schemasGroup = new ItemDataGroup(i18n('field.schemas'), 'schemas');

                let contentTypeNames = contentTypes.map(
                    (contentType: ContentTypeSummary) => contentType.getContentTypeName().getLocalName()).sort(this.sortAlphabeticallyAsc);
                schemasGroup.addDataArray(i18n('field.contentTypes'), contentTypeNames);

                let mixinsNames = mixins.map((mixin: Mixin) => mixin.getMixinName().getLocalName()).sort(this.sortAlphabeticallyAsc);
                schemasGroup.addDataArray(i18n('field.mixins'), mixinsNames);

                let relationshipTypeNames = relationshipTypes.map(
                    (relationshipType: RelationshipType) => relationshipType.getRelationshiptypeName().getLocalName()).sort(
                    this.sortAlphabeticallyAsc);
                schemasGroup.addDataArray(i18n('field.relationshipTypes'), relationshipTypeNames);

                return schemasGroup;

            }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
    }

    private initProviders(applicationKey: ApplicationKey): wemQ.Promise<ItemDataGroup> {
        let providersPromises = [new api.application.AuthApplicationRequest(applicationKey).sendAndParse()];

        return wemQ.all(providersPromises).spread<ItemDataGroup>(
            (application: Application) => {
                if (application) {
                    const providersGroup = new ItemDataGroup(i18n('field.idProviders'), 'providers');

                    providersGroup.addDataList(i18n('field.key'), application.getApplicationKey().toString());
                    providersGroup.addDataList(i18n('field.name'), application.getDisplayName());

                    return providersGroup;
                }
                return null;
            });
    }

    private sortAlphabeticallyAsc(a: string, b: string): number {
        return a.toLocaleLowerCase().localeCompare(b.toLocaleLowerCase());
    }

    private getLocalizedState(state: string): string {
        switch (state) {
        case Application.STATE_STARTED:
            return i18n('application.state.started');
        case Application.STATE_STOPPED:
            return i18n('application.state.stopped');
        default:
            return '';
        }
    }
}
