module app.browse {

    export class ModuleBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.module.Module> {

        createItemSelectionPanel(): ModuleBrowseItemsSelectionPanel {
            return new ModuleBrowseItemsSelectionPanel();
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<api.module.Module> {
            return new app.view.ModuleItemStatisticsPanel();
        }

    }
}