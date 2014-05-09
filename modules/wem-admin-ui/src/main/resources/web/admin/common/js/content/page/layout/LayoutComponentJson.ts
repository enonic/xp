module api.content.page.layout {

    export interface LayoutComponentJson extends api.content.page.DescriptorBasedPageComponentJson {

        regions: api.content.page.region.RegionJson[];
    }
}