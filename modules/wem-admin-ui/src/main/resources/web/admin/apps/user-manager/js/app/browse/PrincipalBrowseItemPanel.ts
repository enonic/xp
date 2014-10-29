module app.browse {

    export class PrincipalBrowseItemPanel extends api.app.browse.BrowseItemPanel<api.security.Principal> {

        constructor() {
            super();
        }

        createItemStatisticsPanel(): api.app.view.ItemStatisticsPanel<api.security.Principal> {
            return new app.view.PrincipalItemStatisticsPanel();
        }

    }
}