module api.ui.security.acl {

    import Access = api.security.acl.Access;
    import Principal = api.security.Principal;

    export class UserAccessListItem implements api.Equitable {

        private access: Access;

        private principals: Principal[];

        constructor(access: Access, principals: Principal[]) {
            this.access = access;
            this.principals = principals;
        }

        getAccess(): Access {
            return this.access;
        }

        getPrincipals(): Principal[] {
            return this.principals;
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, UserAccessListItem)) {
                return false;
            }

            var other = <UserAccessListItem> o;

            if (this.access != other.access) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.principals, other.principals)) {
                return false;
            }

            return true;
        }
    }
}