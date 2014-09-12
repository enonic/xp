module app.browse {

    export class ModuleBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.module.ModuleSummary> {

        constructor() {
            super();
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<api.module.ModuleSummary> {
            return new app.view.ModuleItemStatisticsPanel();
        }

    }
}