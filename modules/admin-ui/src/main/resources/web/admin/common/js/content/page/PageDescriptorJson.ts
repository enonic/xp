module api.content.page {

    export interface PageDescriptorJson extends DescriptorJson {

        regions:api.content.page.region.RegionsDescriptorJson[];
    }
}