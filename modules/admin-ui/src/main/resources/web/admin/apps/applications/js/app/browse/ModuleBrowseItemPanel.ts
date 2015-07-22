module app.browse {

    export class ModuleBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.module.Application> {

        createItemSelectionPanel(): ModuleBrowseItemsSelectionPanel {
            return new ModuleBrowseItemsSelectionPanel();
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<api.module.Application> {
            return new app.view.ModuleItemStatisticsPanel();
        }

    }
}