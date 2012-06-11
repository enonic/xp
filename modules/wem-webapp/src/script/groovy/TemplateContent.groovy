import org.apache.commons.lang.StringUtils

class TemplateContent
{
  // properties
  String name
  def lines = []

  String toJavascriptText( String linePrefix )
  {
    removeEmptyLines();

    StringBuilder text = new StringBuilder();
    lines.eachWithIndex() { line, i ->
      String lineTrimmed = StringUtils.strip( line );
      String trimmed = StringUtils.substringBefore( line, lineTrimmed );

      String textLine = linePrefix + trimmed + "'" + escapeSingleQuotes( lineTrimmed ) + "'";
      text.append( textLine );

      boolean isLastLine = ( i == lines.size() - 1 );
      if ( !isLastLine )
      {
        text.append( " + \r\n" );
      }
    }
    return text.toString();
  }

  def removeEmptyLines()
  {
    lines = lines.findAll { (it != null) && (!it.trim().isEmpty()) }
  }

  String escapeSingleQuotes( String text )
  {
    return text.replace( "'", "\\\'" );
  }

  public String getText(){
    return toJavascriptText( "\t\t" );
  }

}