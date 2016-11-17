import "../../api.ts";
import {UserTreeGridItem} from "./UserTreeGridItem";

export class BaseUserEvent extends api.event.Event {

    private gridItems: UserTreeGridItem[];

    constructor(gridItems: UserTreeGridItem[]) {
        super();

        this.gridItems = gridItems;
    }

    getPrincipals(): UserTreeGridItem[] {
        return this.gridItems;
    }
}
