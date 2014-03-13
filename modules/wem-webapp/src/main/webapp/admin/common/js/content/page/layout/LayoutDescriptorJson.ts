module api.content.page.layout {

    export interface LayoutDescriptorJson extends api.content.page.DescriptorJson {

        regions:api.content.page.region.RegionsDescriptorJson[];

    }
}