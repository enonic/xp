module api.content.page.region {

    export class RegionDescriptor {

        private name: string;

        constructor(builder: RegionDescriptorBuilder) {
            this.name = builder.name;
        }

        getName(): string {
            return this.name;
        }
    }

    export class RegionDescriptorBuilder {

        name: string;

        public fromJson(json: RegionsDescriptorJson): RegionDescriptorBuilder {
            this.name = json.name;
            return this;
        }

        public setName(value: string): RegionDescriptorBuilder {
            this.name = value;
            return this;
        }

        public build(): RegionDescriptor {
            return new RegionDescriptor(this);
        }
    }
}