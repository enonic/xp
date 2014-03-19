module api.content.page {

    /**
     * Request representing either a create, update or delete Request for a Page.
     */
    export interface PageCUDRequest {

        sendAndParse(): Q.Promise<Content>;
    }
}