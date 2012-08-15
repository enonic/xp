Feature: Horizontal Inheritance, used by a ContentType
  So web developers can be able to reuse field definitons across content types,
  they can create field and field-set templates
  that can be used by any content type.

  Scenario: Two ContentTypes using same template
    Given a Module named myModule
    And a Field named myName of type textLine
    And a FieldTemplate named myTemplate in module myModule with field myName
    And a ContentType named cty1
    And a ContentType named cty2
    And adding TemplateReference named myName referencing FieldTemplate myModule:myTemplate to ContentType cty1
    And adding TemplateReference named myName referencing FieldTemplate myModule:myTemplate to ContentType cty2
    When translating template references to configItems for all content types
    Then the following ConfigItems should exist in the following ContentTypes:
      | cty1 | myName | FIELD |
      | cty2 | myName | FIELD |