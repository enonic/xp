module api.content {

    export class MetadataByMixinNameComparator implements api.Comparator<Metadata> {

        compare(a: Metadata, b: Metadata): number {
            if (!a.getName()) {
                return 1;
            } else {
                var firstName = a.getName().getLocalName();
            }
            if (!b.getName()) {
                return -1;
            } else {
                var secondName = b.getName().getLocalName();
            }
            return firstName.localeCompare(secondName);
        }
    }
}