module api.content.page.region {

    export class Region {

        private name: string;

        private pageComponents: api.content.page.PageComponent[] = [];

        private componentByName: {[s:string] : api.content.page.PageComponent;} = {};

        constructor(builder: RegionBuilder) {
            this.name = builder.name;
            this.pageComponents = builder.pageComponents;

            this.pageComponents.forEach((c: api.content.page.PageComponent)=> {
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
            this.pageComponents.forEach((component: api.content.page.PageComponent)=> {
                if (component.getName().isDuplicateOf(name)) {
                    count++;
                }
            });
            return count;
        }

        /*
         *  Add component after target component. Component will only be added if target component is found.
         *  Returns the index of the added component, -1 if target component was not found.
         */
        addComponentAfter(component: api.content.page.PageComponent, target: ComponentName): number {

            api.util.assert(!this.hasComponentWithName(component.getName()),
                "Component already added to region [" + this.name + "]: " + component.getName().toString());

            var targetIndex = this.getComponentIndex(target);
            if (targetIndex == -1 && this.pageComponents.length > 1) {
                return -1;
            }

            if (targetIndex == -1) {
                this.pageComponents.push(component);
                return 0;
            }
            else {
                var index = targetIndex + 1;
                this.pageComponents.splice(index, 0, component);
                return index;
            }
        }

        addComponent(component: api.content.page.PageComponent) {

            api.util.assert(!this.hasComponentWithName(component.getName()),
                "Component already added to region [" + this.name + "]: " + component.getName().toString());

            this.componentByName[component.getName().toString()] = component;
            this.pageComponents.push(component);
        }

        getComponentIndex(componentName: ComponentName): number {

            for (var i = 0; i < this.pageComponents.length; i++) {
                var currComponent = this.pageComponents[i];
                if (currComponent.getName().equals(componentName)) {
                    return i;
                }
            }
            return -1;
        }

        hasComponentWithName(name: ComponentName) {
            return this.componentByName[name.toString()] != undefined;
        }

        getComponents(): api.content.page.PageComponent[] {
            return this.pageComponents;
        }

        getComponentByIndex(index: number): api.content.page.PageComponent {
            return this.pageComponents[index];
        }

        getComponentByName(name: api.content.page.ComponentName): api.content.page.PageComponent {
            return this.componentByName[name.toString()];
        }

        getImageComponent(name: api.content.page.ComponentName): api.content.page.image.ImageComponent {
            var c = this.getComponentByName(name);

            var message = "Expected component [" + name.toString() + "] to be an api.content.page.image.ImageComponent: " +
                          api.util.getClassName(c);
            api.util.assert(c instanceof api.content.page.image.ImageComponent, message);
            return <api.content.page.image.ImageComponent>c;
        }

        getLayoutComponent(name: api.content.page.ComponentName): api.content.page.layout.LayoutComponent {
            var c = this.getComponentByName(name);

            var message = "Expected component [" + name.toString() + "] to be a api.content.page.layout.LayoutComponent: " +
                          api.util.getClassName(c);
            api.util.assert(c instanceof api.content.page.layout.LayoutComponent, message);
            return <api.content.page.layout.LayoutComponent>c;
        }

        getPartComponent(name: api.content.page.ComponentName): api.content.page.part.PartComponent {
            var c = this.getComponentByName(name);

            var message = "Expected component [" + name.toString() + "] to be a api.content.page.part.PartComponent: " +
                          api.util.getClassName(c);
            api.util.assert(c instanceof api.content.page.part.PartComponent, message);
            return <api.content.page.part.PartComponent>c;
        }

        toJson(): json.RegionJson {

            var componentJsons: api.content.page.json.PageComponentTypeWrapperJson[] = [];

            this.pageComponents.forEach((component: api.content.page.PageComponent) => {
                componentJsons.push(component.toJson());
            });

            return {
                name: this.name,
                components: componentJsons
            };
        }
    }

    export class RegionBuilder {

        name: string;

        pageComponents: api.content.page.PageComponent[] = [];

        public setName(value: string): RegionBuilder {
            this.name = value;
            return this;
        }

        public addComponent(value: api.content.page.PageComponent): RegionBuilder {
            this.pageComponents.push(value);
            return this;
        }

        public build(): Region {
            return new Region(this);
        }
    }
}