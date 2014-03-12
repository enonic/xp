module api.content.page.json {

    export interface PageDescriptorJson extends DescriptorJson {

        regions:api.content.page.region.json.RegionsDescriptorJson[];
    }
}