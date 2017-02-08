module api.content.page {

    import PropertyTree = api.data.PropertyTree;
    import Component = api.content.page.region.Component;
    import GetContentByIdRequest = api.content.resource.GetContentByIdRequest;
    import Region = api.content.page.region.Region;

    export class Page implements api.Equitable, api.Cloneable {

        private controller: DescriptorKey;

        private template: PageTemplateKey;

        private regions: api.content.page.region.Regions;

        private fragment: Component;

        private config: PropertyTree;

        private customized: boolean;

        constructor(builder: PageBuilder) {
            this.controller = builder.controller;
            this.template = builder.template;
            this.regions = builder.regions;
            this.fragment = builder.fragment;
            this.config = builder.config;
            this.customized = builder.customized;
        }

        hasController(): boolean {
            return !!this.controller;
        }

        getController(): DescriptorKey {
            return this.controller;
        }

        hasTemplate(): boolean {
            return !!this.template;
        }

        getTemplate(): PageTemplateKey {
            return this.template;
        }

        hasRegions(): boolean {
            return this.regions != null;
        }

        getRegions(): api.content.page.region.Regions {
            return this.regions;
        }

        hasConfig(): boolean {
            return this.config != null;
        }

        getConfig(): PropertyTree {
            return this.config;
        }

        isCustomized(): boolean {
            return this.customized;
        }

        getFragment(): Component {
            return this.fragment;
        }

        isFragment(): boolean {
            return this.fragment != null;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Page)) {
                return false;
            }

            let other = <Page>o;

            if (!api.ObjectHelper.equals(this.controller, other.controller)) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.template, other.template)) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.regions, other.regions)) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.fragment, other.fragment)) {
                return false;
            }

            if (!this.config && (!other.config || other.config.isEmpty())) {
                return true;
            }
            if (!other.config && (!this.config || this.config.isEmpty())) {
                return true;
            }
            return api.ObjectHelper.equals(this.config, other.config);
        }

        clone(): Page {

            return new PageBuilder(this).build();
        }

        public doRegionComponentsContainId(id: ContentId): wemQ.Promise<boolean> {
            let fragments: ContentId[] = [];
            let containsId = this.doRegionsContainId(this.getRegions().getRegions(), id, fragments);
            if (!containsId && fragments.length > 0) {
                return wemQ.all(fragments.map(fragmentId => new GetContentByIdRequest(fragmentId).sendAndParse()))
                    .then((fragmentContents: Content[]) => {
                        return fragmentContents.some((fragmentContent: Content) => {
                            return fragmentContent.getPage().doesFragmentContainId(id);
                        });
                    });
            } else {
                return wemQ(containsId);
            }
        }

        public doesFragmentContainId(id: ContentId): boolean {
            let containsId = false;
            let fragmentCmp = this.getFragment();
            if (!!fragmentCmp && ObjectHelper.iFrameSafeInstanceOf(fragmentCmp.getType(), api.content.page.region.ImageComponentType)) {
                containsId = (<api.content.page.region.ImageComponent>fragmentCmp).getImage().equals(id);
            }

            return containsId;
        }

        private doRegionsContainId(regions: Region[], id: ContentId, fragments: ContentId[] = []): boolean {
            return regions.some((region: Region) => {
                return region.getComponents().some((component: Component) => {
                    if (ObjectHelper.iFrameSafeInstanceOf(component.getType(), api.content.page.region.FragmentComponentType)) {
                        fragments.push((<api.content.page.region.FragmentComponent>component).getFragment());
                    }
                    if (ObjectHelper.iFrameSafeInstanceOf(component.getType(), api.content.page.region.ImageComponentType)) {
                        return (<api.content.page.region.ImageComponent>component).getImage().equals(id);
                    }
                    if (ObjectHelper.iFrameSafeInstanceOf(component.getType(), api.content.page.region.LayoutComponentType)) {
                        return this.doRegionsContainId((<api.content.page.region.LayoutComponent>component).getRegions().getRegions(),
                                                        id,
                                                        fragments);
                    }
                    return false;
                });
            });
        }
    }

    export class PageBuilder {

        controller: DescriptorKey;

        template: PageTemplateKey;

        regions: api.content.page.region.Regions;

        config: PropertyTree;

        customized: boolean;

        fragment: Component;

        constructor(source?: Page) {
            if (source) {
                this.controller = source.getController();
                this.template = source.getTemplate();
                this.regions = source.getRegions() ? source.getRegions().clone() : null;
                this.config = source.getConfig() ? source.getConfig().copy() : null;
                this.customized = source.isCustomized();
                this.fragment = source.isFragment() ? source.getFragment().clone() : null;
            }
        }

        public fromJson(json: api.content.page.PageJson): PageBuilder {
            this.setController(json.controller ? DescriptorKey.fromString(json.controller) : null);
            this.setTemplate(json.template ? PageTemplateKey.fromString(json.template) : null);
            this.setRegions(json.regions != null ? api.content.page.region.Regions.create().fromJson(json.regions, null).build() : null);
            this.setConfig(json.config != null
                ? PropertyTree.fromJson(json.config)
                : null);
            this.setCustomized(json.customized);

            if (json.fragment) {
                let component: Component = api.content.page.region.ComponentFactory.createFromJson(json.fragment, 0, null);
                this.setFragment(component);
            }

            return this;
        }

        public setController(value: DescriptorKey): PageBuilder {
            this.controller = value;
            return this;
        }

        public setTemplate(value: PageTemplateKey): PageBuilder {
            this.template = value;
            return this;
        }

        public setRegions(value: api.content.page.region.Regions): PageBuilder {
            this.regions = value;
            return this;
        }

        public setConfig(value: PropertyTree): PageBuilder {
            this.config = value;
            return this;
        }

        public setCustomized(value: boolean): PageBuilder {
            this.customized = value;
            return this;
        }

        public setFragment(value: Component): PageBuilder {
            this.fragment = value;
            return this;
        }

        public build(): Page {
            return new Page(this);
        }
    }
}
