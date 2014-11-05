module api.security {

    export class UserTreeGridItem implements api.Equitable {
        private id: string;
        private displayName: string;
        private children: boolean;

        constructor(displayName: string) {
            this.displayName = displayName;
        }

        getDisplayName(): string {
            return this.displayName;
        }

        hasChildren(): boolean {
            return this.children;
        }

        getId(): string {
            return this.id;
        }

        setId(id: string) {
            this.id = id;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserTreeGridItem)) {
                return false;
            }

            var other = <UserTreeGridItem> o;
            return this.displayName === other.displayName;
        }
    }
}
