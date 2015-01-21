module api.content.page {

    export class DescriptorByDisplayNameComparator implements api.Comparator<Descriptor> {

        compare(a:Descriptor, b:Descriptor):number {
            if (!a) {
                return 1;
            } else {
                var firstName = a.getDisplayName();
            }
            if (!b) {
                return -1;
            } else {
                var secondName = b.getDisplayName();
            }
            return firstName.localeCompare(secondName);
        }
    }
}