import "../../api.ts";
import {UserTreeGridItem} from "./UserTreeGridItem";

export class BaseUserEvent extends api.event.Event {

    private gridItems: UserTreeGridItem[];

    constructor(gridItems: UserTreeGridItem[]) {
        this.gridItems = gridItems;
        super();
    }

    getPrincipals(): UserTreeGridItem[] {
        return this.gridItems;
    }
}
