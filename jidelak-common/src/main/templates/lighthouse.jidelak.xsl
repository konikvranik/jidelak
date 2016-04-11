<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

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
            <id>praha-lighthouse-bufet</id>
            <name>Lighthouse bufet</name>
            <phone>+420 777257838</phone>
            <web>http://www.lighthousecoffee.cz/</web>
            <city>Praha 7</city>
            <country>Česká republika</country>
            <address>Jankovcova 1566/2b</address>
            <zip>170 00</zip>

            <source time="absolute" firstDayOfWeek="Po" encoding="utf8" dateFormat="E d.M.yyyy" locale="cs_CZ"
                    url="http://www.lighthousecoffee.cz/files/file/jidelni_listek_restaurant.pdf"/>

            <open>
                <term day-of-week="Po" from="11:00" to="14:00"/>
                <term day-of-week="Út" from="11:00" to="14:00"/>
                <term day-of-week="St" from="11:00" to="14:00"/>
                <term day-of-week="Čt" from="11:00" to="14:00"/>
                <term day-of-week="Pá" from="11:00" to="14:00"/>
            </open>

            <xsl:apply-templates select="//i[text() = 'Polévky:']"/>
        </restaurant>
    </xsl:template>

    <xsl:template match="//i[text() = 'Polévky:']">
        <menu>
            <xsl:apply-templates
                    select="./following::p[count((self::p|following-sibling::p)[i='Hlavní jídla:']) &gt; 0]"
                    mode="menuitem">
                <xsl:with-param name="dish">soup</xsl:with-param>
            </xsl:apply-templates>
            <xsl:apply-templates
                    select="./following::p[i='Hlavní jídla:']/following-sibling::p[count((self::p|following-sibling::p)[*='K JÍDLU DOMÁCÍ LIMONÁDA ZDARMA 0,2L']) &gt; 0]"
                    mode="menuitem">
                <xsl:with-param name="dish">dinner</xsl:with-param>
            </xsl:apply-templates>
        </menu>
    </xsl:template>

    <xsl:template match="p" mode="menuitem">
        <xsl:param name="dish"/>
        <xsl:if test="string-length(normalize-space(child::text())) &gt; 0">
            <meal dish="dinner" category="10-normal">
                <xsl:attribute name="dish">
                    <xsl:value-of select="$dish"/>
                </xsl:attribute>
                <xsl:call-template name="order"/>
                <xsl:call-template name="time"/>
                <xsl:call-template name="title"/>
                <xsl:call-template name="description"/>
                <xsl:call-template name="price"/>
            </meal>
        </xsl:if>
    </xsl:template>

    <xsl:template name="title">
        <title>
            <xsl:apply-templates select="child::text()"/>
        </title>
    </xsl:template>

    <xsl:template name="description">
    </xsl:template>

    <xsl:template name="price">
        <price>
            <xsl:value-of select="td[contains(@class, 'td-cena')]"/>
        </price>

    </xsl:template>

    <xsl:template name="order">
        <xsl:attribute name="order">
            <xsl:value-of select="position()"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="time">
        <xsl:attribute name="time">
            <xsl:value-of select="/html/body/div/div/p[1]/b[text()='Lighthouse bufet']/following-sibling::text()"/>
        </xsl:attribute>
    </xsl:template>


</xsl:stylesheet>
