module api.ui.security.acl {

    import Principal = api.security.Principal;

    export class UserAccessListItem implements api.Equitable {

        private access: Access;

        private principals: Principal[] = [];

        constructor(access: Access) {
            this.access = access;
        }

        getAccess(): Access {
            return this.access;
        }

        getPrincipals(): Principal[] {
            return this.principals;
        }

        addItem(principal: Principal) {
            if (principal) {
                var exist = this.principals.some((curPrincipal: Principal) => {
                    return curPrincipal.equals(principal);
                });

                if (!exist) {
                    this.principals.push(principal);
                }
            }
        }

        addItems(principals: Principal[]) {
            if (principals) {
                principals.forEach((principal) => this.addItem(principal));
            }
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