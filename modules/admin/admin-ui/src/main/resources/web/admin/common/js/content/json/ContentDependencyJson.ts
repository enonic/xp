module api.content.json {

    export interface ContentDependencyJson {
        inbound: ContentDependencyGroupJson[];
        outbound: ContentDependencyGroupJson[];
    }
}
