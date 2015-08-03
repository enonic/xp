module app.browse {

    import BrowseItem = api.app.browse.BrowseItem;
    import Application = api.application.Application;
    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;

    export class ModuleBrowseActions implements TreeGridActions<Application>  {

        public START_MODULE: api.ui.Action;
        public STOP_MODULE: api.ui.Action;

        private allActions: api.ui.Action[] = [];

        private static INSTANCE: ModuleBrowseActions;

        static init(moduleTreeGrid: ModuleTreeGrid): ModuleBrowseActions {
            new ModuleBrowseActions(moduleTreeGrid);
            return ModuleBrowseActions.INSTANCE;
        }

        static get(): ModuleBrowseActions {
            return ModuleBrowseActions.INSTANCE;
        }

        constructor(moduleTreeGrid: ModuleTreeGrid) {

            this.START_MODULE = new StartModuleAction(moduleTreeGrid);
            this.STOP_MODULE = new StopModuleAction(moduleTreeGrid);

            this.allActions.push(this.START_MODULE, this.STOP_MODULE);

            ModuleBrowseActions.INSTANCE = this;
        }

        getAllActions(): api.ui.Action[] {
            return this.allActions;
        }

        updateActionsEnabledState(applicationBrowseItems: BrowseItem<Application>[]): wemQ.Promise<BrowseItem<Application>[]> {
            var modulesSelected = applicationBrowseItems.length;
            var anySelected = modulesSelected > 0;
            var anyStarted = false;
            var anyStopped = false;
            applicationBrowseItems.forEach((moduleBrowseItem: BrowseItem<Application>) => {
                var state = moduleBrowseItem.getModel().getState();
                if (state === Application.STATE_STARTED) {
                    anyStarted = true;
                } else if (state === Application.STATE_STOPPED) {
                    anyStopped = true;
                }
            });

            this.START_MODULE.setEnabled(anyStopped);
            this.STOP_MODULE.setEnabled(anyStarted);

            var deferred = wemQ.defer<BrowseItem<Application>[]>();
            deferred.resolve(applicationBrowseItems);
            return deferred.promise;
        }

    }
}
