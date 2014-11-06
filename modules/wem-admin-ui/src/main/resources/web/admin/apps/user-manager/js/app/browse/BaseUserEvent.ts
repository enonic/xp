module app.browse {

    export class BaseUserEvent extends api.event.Event {

        private gridItem: app.browse.UserTreeGridItem[];

        constructor(gridItem: app.browse.UserTreeGridItem[]) {
            this.gridItem = gridItem;
            super();
        }

        getPrincipals(): app.browse.UserTreeGridItem[] {
            return this.gridItem;
        }
    }
}
