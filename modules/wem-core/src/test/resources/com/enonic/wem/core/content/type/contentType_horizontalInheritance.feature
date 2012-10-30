Feature: Horizontal Inheritance, used by a ContentType
  So web developers can be able to reuse field definitons across content types,
  they can create input and formItemSet subTypes
  that can be used by any content type.

  Scenario: Two ContentTypes using same subType
    Given a Module named myModule
    And a Input named myName of type textLine
    And a ComponentSubType named mySubType in module myModule with input myName
    And a ContentType named cty1
    And a ContentType named cty2
    And adding SubTypeReference named myName referencing ComponentSubType myModule:mySubType to ContentType cty1
    And adding SubTypeReference named myName referencing ComponentSubType myModule:mySubType to ContentType cty2
    When translating subType references to configItems for all content types
    Then the following ConfigItems should exist in the following ContentTypes:
      | cty1 | myName | FIELD |
      | cty2 | myName | FIELD |