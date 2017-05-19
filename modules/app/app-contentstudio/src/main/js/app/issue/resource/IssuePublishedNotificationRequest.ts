import {IssueResourceRequest} from "./IssueResourceRequest";

export class IssuePublishedNotificationRequest extends IssueResourceRequest<any, void> {
    private id: string;

    constructor(id: string) {
        super();

        this.id = id;
    }

    getParams(): Object {
        return {id: this.id};
    }

    getRequestPath(): api.rest.Path {
        return api.rest.Path.fromParent(super.getResourcePath(), 'notifyPublished');
    }
}
