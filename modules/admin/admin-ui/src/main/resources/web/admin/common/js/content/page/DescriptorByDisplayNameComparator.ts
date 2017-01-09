module api.content.page {

    export class DescriptorByDisplayNameComparator implements api.Comparator<Descriptor> {

        compare(a: Descriptor, b: Descriptor): number {
            let firstName: string;
            let secondName: string;
            if (!a) {
                return 1;
            } else {
                firstName = a.getDisplayName() || '';
            }
            if (!b) {
                return -1;
            } else {
                secondName = b.getDisplayName() || '';
            }
            return firstName.localeCompare(secondName);
        }
    }
}