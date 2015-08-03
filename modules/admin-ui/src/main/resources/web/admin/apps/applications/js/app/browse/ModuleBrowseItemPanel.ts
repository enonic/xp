module app.browse {

    export class ModuleBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.application.Application> {

        createItemSelectionPanel(): ModuleBrowseItemsSelectionPanel {
            return new ModuleBrowseItemsSelectionPanel();
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<api.application.Application> {
            return new app.view.ModuleItemStatisticsPanel();
        }

    }
}