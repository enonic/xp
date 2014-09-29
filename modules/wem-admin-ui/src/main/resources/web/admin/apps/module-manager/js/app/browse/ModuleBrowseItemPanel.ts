module app.browse {

    export class ModuleBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.module.Module> {

        constructor() {
            super();
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<api.module.Module> {
            return new app.view.ModuleItemStatisticsPanel();
        }

    }
}