module app.browse {

    export class BaseUserEvent extends api.event.Event {

        private gridItem: api.security.UserTreeGridItem[];

        constructor(gridItem: api.security.UserTreeGridItem[]) {
            this.gridItem = gridItem;
            super();
        }

        getPrincipals(): api.security.UserTreeGridItem[] {
            return this.gridItem;
        }
    }
}
