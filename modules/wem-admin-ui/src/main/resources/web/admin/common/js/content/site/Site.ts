module api.content.site {

    import ModuleKey = api.module.ModuleKey;

    export class Site implements api.Equitable, api.Cloneable {

        private templateKey: api.content.site.template.SiteTemplateKey;

        private moduleConfigs: ModuleConfig[] = [];

        constructor(builder: SiteBuilder) {
            this.templateKey = builder.templateKey;
            this.moduleConfigs = builder.moduleConfigs;
        }

        getTemplateKey(): api.content.site.template.SiteTemplateKey {
            return this.templateKey;
        }

        getModuleConfigs(): ModuleConfig[] {
            return this.moduleConfigs;
        }

        getModuleKeys(): ModuleKey[] {
            return this.moduleConfigs.map((config: ModuleConfig) => config.getModuleKey());
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Site)) {
                return false;
            }

            var other = <Site>o;

            if (!api.ObjectHelper.equals(this.templateKey, other.templateKey)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.moduleConfigs, other.moduleConfigs)) {
                return false;
            }

            return true;
        }

        clone(): Site {

            return new SiteBuilder(this).build();
        }

        public static fromJson(siteJson: api.content.site.SiteJson): Site {
            return new SiteBuilder().fromSiteJson(siteJson).build();
        }
    }

    export class SiteBuilder {

        templateKey: api.content.site.template.SiteTemplateKey;

        moduleConfigs: ModuleConfig[] = [];

        constructor(source?: Site) {
            if (source) {
                this.templateKey = source.getTemplateKey();

                source.getModuleConfigs().forEach((config: ModuleConfig) => {
                    this.moduleConfigs.push(config.clone());
                });

            }
        }

        fromSiteJson(siteJson: api.content.site.SiteJson): SiteBuilder {
            this.templateKey = api.content.site.template.SiteTemplateKey.fromString(siteJson.templateName);

            if (siteJson.moduleConfigs != null) {
                siteJson.moduleConfigs.forEach((moduleConfigJson: api.content.site.ModuleConfigJson) => {
                    this.moduleConfigs.push(new ModuleConfigBuilder().setFromJson(moduleConfigJson).build());
                });
            }
            return this;
        }

        setTemplateKey(value: api.content.site.template.SiteTemplateKey): SiteBuilder {
            this.templateKey = value;
            return this;
        }

        setModuleConfigs(value: ModuleConfig[]): SiteBuilder {
            this.moduleConfigs = value;
            return this;
        }

        build(): Site {
            return new Site(this);
        }
    }
}