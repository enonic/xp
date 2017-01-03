module api.content.util {

    export class ContentByPathComparator implements api.Comparator<ContentSummary> {

        compare(a: ContentSummary, b: ContentSummary): number {
            let firstName: string;
            let secondName: string;
            if (!a) {
                return 1;
            } else {
                firstName = a.getPath().toString();
            }
            if (!b) {
                return -1;
            } else {
                secondName = b.getPath().toString();
            }
            return firstName.localeCompare(secondName);
        }
    }
}