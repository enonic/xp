Feature: Horizontal Inheritance, used by a Content
  So web developers can be able to reuse field definitons across content types,
  they can create component and formItemSet subTypes
  that can be used by any content type.

  Scenario: A content using a field subType
    Given a Module named myModule
    Given a Component named myName of type textLine
    Given a ComponentSubType named mySubType in module myModule with component myName
    Given a ContentType named myContentType
    Given adding SubTypeReference named myName referencing ComponentSubType myModule:mySubType to ContentType myContentType
    Given creating content named myContent of type myContentType
    Given setting value "Ola Normann" to path "myName" to content named "myContent"
    When getting value "myName" from content named "myContent"
    Then the returned value should be "Ola Normann"