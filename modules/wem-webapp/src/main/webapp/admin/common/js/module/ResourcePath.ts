module api_module{

    export class ResourcePath extends api_util.BasePath<ResourcePath> {

        private static ELEMENT_DIVIDER:string = "/";

        public static fromString(s:string) {
            var absolute:boolean = s.charAt(0) == ResourcePath.ELEMENT_DIVIDER;
            var elements:string[] = s.split(ResourcePath.ELEMENT_DIVIDER);
            return new ResourcePath(api_util.BasePath.removeEmptyElements(elements), absolute);
        }

        constructor(elements:string[], absolute?:boolean) {
            super(elements, ResourcePath.ELEMENT_DIVIDER, absolute);
        }

        newInstance(elements:string[], absolute:boolean):ResourcePath {
            return new ResourcePath(elements, absolute);
        }
    }
}