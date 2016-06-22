<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output indent="yes" method="xml" encoding="UTF-8"/>

    <xsl:template match="/">
        <jidelak>
            <xsl:apply-templates select="/jidelak/config|/html"/>
        </jidelak>
    </xsl:template>

    <xsl:template match="/jidelak/config|/html">
        <config>
            <xsl:call-template name="restaurant"/>
        </config>
    </xsl:template>

    <xsl:template name="restaurant">
        <restaurant version="1.0">
            <id>praha-hadog</id>
            <name>Hadog</name>
            <phone>+420 728 636 850</phone>
            <web>http://www.hadog.cz</web>
            <city>Praha 8</city>
            <country>Česká republika</country>
            <address>Sokolovská 379/204</address>
            <zip>180 00</zip>

            <source firstDayOfWeek="Po" encoding="cp1250" locale="cs_CZ"
                    url="http://www.hadog.cz/burgery-hotdogy/"/>
   <open>
                <term day-of-week="Po" from="11:00" to="22:00"/>
                <term day-of-week="Út" from="11:00" to="22:00"/>
                <term day-of-week="St" from="11:00" to="22:00"/>
                <term day-of-week="Čt" from="11:00" to="22:00"/>
                <term day-of-week="Pá" from="11:00" to="22:00"/>
                <term day-of-week="So" from="11:00" to="22:00"/>
                <term day-of-week="Ne" from="11:00" to="21:00"/>
            </open>

            <xsl:apply-templates select="//*[@id='center_left']"/>
        </restaurant>
    </xsl:template>

    <xsl:template match="*[@id='center_left']">
        <menu>
            <xsl:apply-templates select="./div[starts-with(@class,'produkty')]" mode="menuitem"/>
        </menu>
    </xsl:template>

    <xsl:template match="div" mode="menuitem">
            <meal dish="" category="">
                <xsl:call-template name="dish"/>
                <xsl:call-template name="category"/>
                <xsl:call-template name="order"/>

                <xsl:call-template name="title"/>
                <xsl:call-template name="description"/>
                <xsl:call-template name="price"/>
            </meal>
    </xsl:template>

    <xsl:template name="dish">
        <xsl:attribute name="dish">
            <xsl:choose>
                <xsl:when test="count(./preceding-sibling::h1) &gt; 4">trimmings</xsl:when>
                <xsl:when test="count(./preceding-sibling::h1) &gt; 3">drink</xsl:when>
                <xsl:otherwise>dinner</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="category">
        <xsl:attribute name="category">
            <xsl:choose>
                <xsl:when test="count(./preceding-sibling::h1) &gt; 3">10-normal</xsl:when>
                <xsl:when test="count(./preceding-sibling::h1) &gt; 2">30-tortilla</xsl:when>
                <xsl:when test="count(./preceding-sibling::h1) &gt; 1">20-sandwich</xsl:when>
                <xsl:when test="count(./preceding-sibling::h1) &gt; 0">10-burger</xsl:when>
                <xsl:otherwise>10-normal</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="title">
        <title>
            <xsl:value-of select="h3/."/>
        </title>
    </xsl:template>

    <xsl:template name="description">
    	<description>
            <xsl:value-of select="div[@class='produkty_popis']/."/>
    	</description>
    </xsl:template>

    <xsl:template name="price">
        <price>
            <xsl:apply-templates select="div[starts-with(@class,'cena')]"/>
        </price>
    </xsl:template>
    <xsl:template match="div[@class='cena']">
    	<xsl:if test="position() &gt; 1">
		<xsl:text> /</xsl:text>
	</xsl:if>
        <xsl:value-of select="text()"/>
        <xsl:value-of select="span[position() &gt; 1]/."/>
    </xsl:template>

    <xsl:template name="order">
        <xsl:attribute name="order">
            <xsl:value-of select="position()"/>
        </xsl:attribute>
    </xsl:template>

</xsl:stylesheet>
