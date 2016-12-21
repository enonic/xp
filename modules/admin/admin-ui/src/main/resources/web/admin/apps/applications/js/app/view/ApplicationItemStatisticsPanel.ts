import "../../api.ts";
import {ApplicationBrowseActions} from "../browse/ApplicationBrowseActions";

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
        super("application-item-statistics-panel");

        this.actionMenu =
            new api.ui.menu.ActionMenu("Application actions", ApplicationBrowseActions.get().START_APPLICATION,
                ApplicationBrowseActions.get().STOP_APPLICATION);

        this.appendChild(this.actionMenu);

        this.applicationDataContainer = new api.dom.DivEl("application-data-container");
        this.appendChild(this.applicationDataContainer);
    }

    setItem(item: api.app.view.ViewItem<api.application.Application>) {
        var currentItem = this.getItem();

        if (currentItem && currentItem.equals(item)) {
            // do nothing in case item has not changed
            return;
        }

        super.setItem(item);
        var currentApplication = item.getModel();

        if (currentApplication.getIconUrl()) {
            this.getHeader().setIconUrl(currentApplication.getIconUrl());
        }

        if (currentApplication.getDescription()) {
            this.getHeader().setHeaderSubtitle(currentApplication.getDescription(), "app-description");
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

        var infoGroup = new ItemDataGroup("Info", "info");
        infoGroup.addDataList("Build date", "TBA");
        infoGroup.addDataList("Version", currentApplication.getVersion());
        infoGroup.addDataList("Key", currentApplication.getApplicationKey().toString());
        infoGroup.addDataList("System Required",
            ">= " + currentApplication.getMinSystemVersion() + " and < " + currentApplication.getMaxSystemVersion());


        var descriptorResponse = this.initDescriptors(currentApplication.getApplicationKey());
        var schemaResponse = this.initSchemas(currentApplication.getApplicationKey());
        var macroResponse = this.initMacros(currentApplication.getApplicationKey());
        var providerResponse = this.initProviders(currentApplication.getApplicationKey());


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
            })

    }

    private initMacros(applicationKey: ApplicationKey): wemQ.Promise<any> {
        let macroRequest = new api.macro.resource.GetMacrosRequest();
        macroRequest.setApplicationKeys([applicationKey]);

        let macroPromises = [macroRequest.sendAndParse()];

        return wemQ.all(macroPromises).spread((macros: MacroDescriptor[])=> {

            var macrosGroup = new ItemDataGroup("Macros", "macros");

            var macroNames = macros.
            filter((macro: MacroDescriptor) => {
                return !ApplicationKey.SYSTEM.equals(macro.getKey().getApplicationKey());
            }).map((macro: MacroDescriptor) => {
                return macro.getDisplayName();
            });
            macrosGroup.addDataArray("Name", macroNames);

            return macrosGroup;
        }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
    }

    private initDescriptors(applicationKey: ApplicationKey): wemQ.Promise<any> {

        var descriptorPromises = [
            new api.content.page.GetPageDescriptorsByApplicationRequest(applicationKey).sendAndParse(),
            new api.content.page.region.GetPartDescriptorsByApplicationRequest(applicationKey).sendAndParse(),
            new api.content.page.region.GetLayoutDescriptorsByApplicationRequest(applicationKey).sendAndParse()
        ];

        return wemQ.all(descriptorPromises).spread(
            (pageDescriptors: PageDescriptor[], partDescriptors: PartDescriptor[], layoutDescriptors: LayoutDescriptor[]) => {

                var descriptorsGroup = new ItemDataGroup("Descriptors", "descriptors");

                var pageNames = pageDescriptors.map((descriptor: PageDescriptor) => descriptor.getName().toString()).sort(
                    this.sortAlphabeticallyAsc);
                descriptorsGroup.addDataArray("Page", pageNames);

                var partNames = partDescriptors.map((descriptor: PartDescriptor) => descriptor.getName().toString()).sort(
                    this.sortAlphabeticallyAsc);
                descriptorsGroup.addDataArray("Part", partNames);

                var layoutNames = layoutDescriptors.map((descriptor: LayoutDescriptor) => descriptor.getName().toString()).sort(
                    this.sortAlphabeticallyAsc);
                descriptorsGroup.addDataArray("Layout", layoutNames);

                return descriptorsGroup;
            }).catch((reason: any) => api.DefaultErrorHandler.handle(reason));
    }

    private initSchemas(applicationKey: ApplicationKey): wemQ.Promise<any> {

        var schemaPromises = [
            new api.schema.content.GetContentTypesByApplicationRequest(applicationKey).sendAndParse(),
            new api.schema.mixin.GetMixinsByApplicationRequest(applicationKey).sendAndParse(),
            new api.schema.relationshiptype.GetRelationshipTypesByApplicationRequest(applicationKey).sendAndParse()
        ];

        return wemQ.all(schemaPromises).spread<any>(
            (contentTypes: ContentTypeSummary[], mixins: Mixin[], relationshipTypes: RelationshipType[]) => {
                var schemasGroup = new ItemDataGroup("Schemas", "schemas");


                var contentTypeNames = contentTypes.map(
                    (contentType: ContentTypeSummary) => contentType.getContentTypeName().getLocalName()).sort(this.sortAlphabeticallyAsc);
                schemasGroup.addDataArray("Content Types", contentTypeNames);

                var mixinsNames = mixins.map((mixin: Mixin) => mixin.getMixinName().getLocalName()).sort(this.sortAlphabeticallyAsc);
                schemasGroup.addDataArray("Mixins", mixinsNames);

                var relationshipTypeNames = relationshipTypes.map(
                    (relationshipType: RelationshipType) => relationshipType.getRelationshiptypeName().getLocalName()).sort(
                    this.sortAlphabeticallyAsc);
                schemasGroup.addDataArray("RelationshipTypes", relationshipTypeNames);

                return schemasGroup;

            }).catch((reason: any) => api.DefaultErrorHandler.handle(reason))
    }

    private initProviders(applicationKey: ApplicationKey): wemQ.Promise<ItemDataGroup> {
        var providersPromises = [new api.application.AuthApplicationRequest(applicationKey).sendAndParse()];

        return wemQ.all(providersPromises).spread<ItemDataGroup>(
            (application: Application) => {
                if(application) {
                    var providersGroup = new ItemDataGroup("ID Providers", "providers");

                    providersGroup.addDataList("Key", application.getApplicationKey().toString());
                    providersGroup.addDataList("Name", application.getDisplayName());

                    return providersGroup;
                }
                return null;
            });
    }

    private sortAlphabeticallyAsc(a: string, b: string): number {
        return a.toLocaleLowerCase().localeCompare(b.toLocaleLowerCase());
    }

}
