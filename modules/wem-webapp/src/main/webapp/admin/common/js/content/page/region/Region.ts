module api.content.page.region {

    export class Region {

        private name: string;

        private pageComponents: api.content.page.PageComponent<api.content.page.TemplateKey>[] = [];

        private componentByName: {[s:string] : api.content.page.PageComponent<api.content.page.TemplateKey>;} = {};

        constructor(builder: RegionBuilder) {
            this.name = builder.name;
            this.pageComponents = builder.pageComponents;

            this.pageComponents.forEach((c: api.content.page.PageComponent<api.content.page.TemplateKey>)=> {
                if (c instanceof api.content.page.image.ImageComponent) {
                    var imageComponent = <api.content.page.image.ImageComponent>c;
                    this.componentByName[imageComponent.getName().toString()] = imageComponent;
                }
                else if (c instanceof api.content.page.part.PartComponent) {
                    var partComponent = <api.content.page.part.PartComponent>c;
                    this.componentByName[partComponent.getName().toString()] = partComponent;
                }
                else if (c instanceof api.content.page.layout.LayoutComponent) {
                    var layoutComponent = <api.content.page.layout.LayoutComponent>c;
                    this.componentByName[layoutComponent.getName().toString()] = layoutComponent;
                }
                else {
                    throw new Error("Unsupported component for Region: " + api.util.getClassName(c));
                }
            });
        }

        getName(): string {
            return this.name;
        }

        countNumberOfDuplicates(name: api.content.page.ComponentName): number {

            var count = 0;
            this.pageComponents.forEach((component: api.content.page.PageComponent<api.content.page.TemplateKey>)=> {
                if( component.getName().isDuplicateOf(name) ) {
                    count++;
                }
            });
            return count;
        }

        hasComponentWithName(name: api.content.page.ComponentName) {
            return this.componentByName[name.toString()] != undefined;
        }

        getComponents(): api.content.page.PageComponent<api.content.page.TemplateKey>[] {
            return this.pageComponents;
        }

        getImageComponent(name: api.content.page.ComponentName): api.content.page.image.ImageComponent {
            var c = this.getComponent(name);

            var message = "Expected component [" + name.toString() + "] to be an api.content.page.image.ImageComponent: " +
                          api.util.getClassName(c);
            api.util.assert(c instanceof api.content.page.image.ImageComponent, message);
            return <api.content.page.image.ImageComponent>c;
        }

        getLayoutComponent(name: api.content.page.ComponentName): api.content.page.layout.LayoutComponent {
            var c = this.getComponent(name);

            var message = "Expected component [" + name.toString() + "] to be a api.content.page.layout.LayoutComponent: " +
                          api.util.getClassName(c);
            api.util.assert(c instanceof api.content.page.layout.LayoutComponent, message);
            return <api.content.page.layout.LayoutComponent>c;
        }

        getPartComponent(name: api.content.page.ComponentName): api.content.page.part.PartComponent {
            var c = this.getComponent(name);

            var message = "Expected component [" + name.toString() + "] to be a api.content.page.part.PartComponent: " +
                          api.util.getClassName(c);
            api.util.assert(c instanceof api.content.page.part.PartComponent, message);
            return <api.content.page.part.PartComponent>c;
        }

        getComponent(name: api.content.page.ComponentName): api.content.page.PageComponent<api.content.page.TemplateKey> {
            return this.componentByName[name.toString()];
        }
    }

    export class RegionBuilder {

        name: string;

        pageComponents: api.content.page.PageComponent<api.content.page.TemplateKey>[] = [];

        public setName(value: string): RegionBuilder {
            this.name = value;
            return this;
        }

        public addComponent(value: api.content.page.PageComponent<api.content.page.TemplateKey>): RegionBuilder {
            this.pageComponents.push(value);
            return this;
        }

        public build(): Region {
            return new Region(this);
        }
    }
}