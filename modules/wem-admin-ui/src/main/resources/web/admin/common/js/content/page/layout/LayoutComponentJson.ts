module api.content.page.layout {

    export interface LayoutComponentJson extends api.content.page.DescriptorBasedComponentJson {

        regions: api.content.page.region.RegionJson[];
    }
}