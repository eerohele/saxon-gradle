<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:template match="*" mode="class">
    <xsl:attribute name="class" select="local-name()"/>
  </xsl:template>

</xsl:stylesheet>
