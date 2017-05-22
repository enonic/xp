import {PublishRequestItemJson} from "./PublishRequestItemJson";

export interface PublishRequestJson {

    excludeIds: string[];

    items: PublishRequestItemJson[];
}
