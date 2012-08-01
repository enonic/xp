Feature: A content can have required data

  Scenario: Validate required field
    Given Value that is empty
    When content is asked if required contract is broken
    Then the answer should be yes