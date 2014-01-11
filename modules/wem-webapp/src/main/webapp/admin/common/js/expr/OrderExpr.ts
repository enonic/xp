module api.expr {

    export enum Direction {
        ASC,
        DESC
    }

    export class OrderExpr implements Expression {

        private direction:Direction;

        constructor( direction:Direction )
        {
            this.direction = direction;
        }

        getDirection():Direction {
            return this.direction;
        }

        directionAsString():string {
            switch (this.direction) {
                case Direction.ASC:
                    return "ASC";
                case Direction.DESC:
                    return "DESC";
                default:
                    return "";
            }
        }
    }
}
