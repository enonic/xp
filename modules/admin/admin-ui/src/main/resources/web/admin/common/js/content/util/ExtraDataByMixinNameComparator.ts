module api.content.util {

    export class ExtraDataByMixinNameComparator implements api.Comparator<ExtraData> {

        compare(a: ExtraData, b: ExtraData): number {
            let firstName: string;
            let secondName: string;

            if (!a.getName()) {
                return 1;
            } else {
                firstName = a.getName().getLocalName();
            }
            if (!b.getName()) {
                return -1;
            } else {
                secondName = b.getName().getLocalName();
            }
            return firstName.localeCompare(secondName);
        }
    }
}
