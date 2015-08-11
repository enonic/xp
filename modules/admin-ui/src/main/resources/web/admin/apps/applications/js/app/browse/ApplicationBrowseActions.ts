module app.browse {

    import BrowseItem = api.app.browse.BrowseItem;
    import Application = api.application.Application;
    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;

    export class ApplicationBrowseActions implements TreeGridActions<Application> {

        public START_APPLICATION: api.ui.Action;
        public STOP_APPLICATION: api.ui.Action;

        private allActions: api.ui.Action[] = [];

        private static INSTANCE: ApplicationBrowseActions;

        static init(applicationTreeGrid: ApplicationTreeGrid): ApplicationBrowseActions {
            new ApplicationBrowseActions(applicationTreeGrid);
            return ApplicationBrowseActions.INSTANCE;
        }

        static get(): ApplicationBrowseActions {
            return ApplicationBrowseActions.INSTANCE;
        }

        constructor(applicationTreeGrid: ApplicationTreeGrid) {

            this.START_APPLICATION = new StartApplicationAction(applicationTreeGrid);
            this.STOP_APPLICATION = new StopApplicationAction(applicationTreeGrid);

            this.allActions.push(this.START_APPLICATION, this.STOP_APPLICATION);

            ApplicationBrowseActions.INSTANCE = this;
        }

        getAllActions(): api.ui.Action[] {
            return this.allActions;
        }

        updateActionsEnabledState(applicationBrowseItems: BrowseItem<Application>[]): wemQ.Promise<BrowseItem<Application>[]> {
            var applicationsSelected = applicationBrowseItems.length;
            var anySelected = applicationsSelected > 0;
            var anyStarted = false;
            var anyStopped = false;
            applicationBrowseItems.forEach((applicationBrowseItem: BrowseItem<Application>) => {
                var state = applicationBrowseItem.getModel().getState();
                if (state === Application.STATE_STARTED) {
                    anyStarted = true;
                } else if (state === Application.STATE_STOPPED) {
                    anyStopped = true;
                }
            });

            this.START_APPLICATION.setEnabled(anyStopped);
            this.STOP_APPLICATION.setEnabled(anyStarted);

            var deferred = wemQ.defer<BrowseItem<Application>[]>();
            deferred.resolve(applicationBrowseItems);
            return deferred.promise;
        }

    }
}
