<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE stylesheet [ <!ENTITY alef "&#2135;"> ]>

<xsl:stylesheet version="3.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  exclude-result-prefixes="xs">

  <xsl:param name="title" as="xs:string"/>
  <xsl:param name="padding" as="xs:string"/>

  <xsl:output method="html" indent="yes" version="5.0"/>

  <xsl:strip-space elements="*"/>

  <xsl:import href="xsl:common.xsl"/>

  <xsl:template match="/">
    <html>
      <head>
        <title><xsl:value-of select="$title"/></title>

        <style type="text/css">
          body {
            font-family: sans-serif;
            padding: <xsl:value-of select="$padding"/>
          }

          dt {
            font-weight: bold;
          }

          .DeliveryNotes {
            font-style: italic;
          }
        </style>
      </head>

      <body>
        <h1><xsl:value-of select="$title"/></h1>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="PurchaseOrder">
    <article>
      <xsl:apply-templates select="." mode="class"/>
      <xsl:apply-templates select="@*"/>
      <h2>Purchase Order <xsl:value-of select="@PurchaseOrderNumber"/></h2>
      <xsl:apply-templates select="node()"/>
    </article>
  </xsl:template>

  <xsl:template match="@PurchaseOrderNumber">
    <xsl:attribute name="data-order-number" select="."/>
  </xsl:template>

  <xsl:template match="@OrderDate">
    <dl>
      <dt>Order date</dt>
      <dd>
        <time>
          <xsl:value-of select="."/>
        </time>
      </dd>
    </dl>
  </xsl:template>

  <xsl:template match="Address">
    <xsl:apply-templates select="@Type" mode="header"/>

    <address>
      <xsl:apply-templates select="@* | node()"/>
    </address>
  </xsl:template>

  <xsl:template match="Address/@Type" mode="header">
    <h3><xsl:value-of select="."/> address</h3>
  </xsl:template>

  <xsl:template match="Address/@Type">
    <xsl:attribute name="data-type" select="."/>
  </xsl:template>

  <xsl:template match="Address/Name | Address/Street | Address/City">
    <xsl:value-of select="."/><br/>
  </xsl:template>

  <xsl:template match="Address/State | Address/Zip | Address/Country">
    <xsl:value-of select="."/><xsl:text> </xsl:text>
  </xsl:template>

  <xsl:template match="Items">
    <dl>
      <xsl:apply-templates select="@* | node()"/>
    </dl>
  </xsl:template>

  <xsl:template match="Item">
    <xsl:apply-templates select="node()"/>
  </xsl:template>

  <xsl:template match="@PartNumber">
    <xsl:attribute name="data-part-number" select="."/>
  </xsl:template>

  <xsl:template match="DeliveryNotes">
    <p>
      <xsl:apply-templates select="." mode="class"/>
      <xsl:apply-templates select="@* | node()"/>
    </p>
  </xsl:template>

  <xsl:template match="Item/*">
    <dd>
      <xsl:apply-templates select="." mode="class"/>
      <xsl:apply-templates select="@* | node()"/>
    </dd>
  </xsl:template>

  <xsl:template match="Item/ProductName" priority="1">
    <dt>
      <xsl:apply-templates select="../@PartNumber, @*, node()"/>
    </dt>
  </xsl:template>

  <xsl:template match="ShipDate">
    <time>
      <xsl:value-of select="."/>
    </time>
  </xsl:template>

  <xsl:template match="*" mode="class">
    <xsl:attribute name="class" select="local-name()"/>
  </xsl:template>

</xsl:stylesheet>
