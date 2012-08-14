Feature: Horizontal Inheritance, used by a Content
  So web developers can be able to reuse field definitons across content types,
  they can create field and field-set templates
  that can be used by any content type.

  Scenario: A content using a field template
    Given a Module named myModule
    Given a Field named myName of type textLine
    Given a FieldTemplate named myTemplate in module myModule with field myName
    Given a ContentType named myContentType
    Given adding TemplateReference named myName referencing FieldTemplate myModule:myTemplate to ContentType myContentType
    Given creating content named myContent of type myContentType
    Given setting value "Ola Normann" to path "myName" to content named "myContent"
    When getting value "myName" from content named "myContent"
    Then the returned value should be "Ola Normann"