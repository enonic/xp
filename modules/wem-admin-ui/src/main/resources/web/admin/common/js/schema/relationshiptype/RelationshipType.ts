module api.schema.relationshiptype {

    export class RelationshipType extends api.schema.Schema implements api.Equitable {

        private fromSemantic: string;

        private toSemantic: string;

        private allowedFromTypes: string[];

        private allowedToTypes: string[];

        constructor(builder: RelationshipTypeBuilder) {
            super(builder);
            this.fromSemantic = builder.fromSemantic;
            this.toSemantic = builder.toSemantic;
            this.allowedFromTypes = builder.allowedFromTypes;
            this.allowedToTypes = builder.allowedToTypes;
        }

        getRelationshiptypeName(): RelationshipTypeName {
            return new RelationshipTypeName(this.getName());
        }

        getFromSemantic(): string {
            return this.fromSemantic;
        }

        getToSemantic(): string {
            return this.toSemantic;
        }

        getAllowedFromTypes(): string[] {
            return this.allowedFromTypes;
        }

        getAllowedToTypes(): string[] {
            return this.allowedToTypes;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, RelationshipType)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <RelationshipType>o;

            if (!ObjectHelper.stringEquals(this.fromSemantic, other.fromSemantic)) {
                return false;
            }

            if (!ObjectHelper.stringEquals(this.toSemantic, other.toSemantic)) {
                return false;
            }

            if (!ObjectHelper.stringArrayEquals(this.allowedFromTypes, other.allowedFromTypes)) {
                return false;
            }

            if (!ObjectHelper.stringArrayEquals(this.allowedToTypes, other.allowedToTypes)) {
                return false;
            }

            return true;
        }

        static fromJson(json: api.schema.relationshiptype.RelationshipTypeJson): RelationshipType {
            return new RelationshipTypeBuilder().fromRelationshipTypeJson(json).build();
        }
    }

    export class RelationshipTypeBuilder extends api.schema.SchemaBuilder {

        fromSemantic: string;

        toSemantic: string;

        allowedFromTypes: string[];

        allowedToTypes: string[];

        constructor(source?: RelationshipType) {
            if (source) {
                super(source);
                this.fromSemantic = source.getFromSemantic();
                this.toSemantic = source.getToSemantic();
                this.allowedFromTypes = source.getAllowedFromTypes();
                this.allowedToTypes = source.getAllowedToTypes();
            }
        }

        fromRelationshipTypeJson(relationshipTypeJson: api.schema.relationshiptype.RelationshipTypeJson): RelationshipTypeBuilder {

            super.fromSchemaJson(relationshipTypeJson);

            this.fromSemantic = relationshipTypeJson.fromSemantic;
            this.toSemantic = relationshipTypeJson.toSemantic;
            this.allowedFromTypes = relationshipTypeJson.allowedFromTypes;
            this.allowedToTypes = relationshipTypeJson.allowedToTypes;
            return this;
        }

        build(): RelationshipType {
            return new RelationshipType(this);
        }
    }
}