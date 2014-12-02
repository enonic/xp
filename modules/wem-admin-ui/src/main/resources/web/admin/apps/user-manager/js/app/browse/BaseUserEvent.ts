module app.browse {

    export class BaseUserEvent extends api.event.Event {

        private gridItems: app.browse.UserTreeGridItem[];

        constructor(gridItems: app.browse.UserTreeGridItem[]) {
            this.gridItems = gridItems;
            super();
        }

        getPrincipals(): app.browse.UserTreeGridItem[] {
            return this.gridItems;
        }
    }
}
