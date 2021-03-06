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
        <restaurant version="1.5">
            <id>praha-babcak</id>
            <name>Jídelna Babčák</name>
            <phone>+420 266782055</phone>
            <!-- web></web -->
            <city>Praha 7</city>
            <country>Česká republika</country>
            <address>Jankovcova 2</address>
            <zip>170 00</zip>

            <source time="absolute" firstDayOfWeek="Mon" encoding="utf8" dateFormat="E, d MMM" locale="en_US"
                    url="https://www.zomato.com/praha/j%C3%ADdelna-bab%C4%8D%C3%A1k-hole%C5%A1ovice-praha-7/menu#daily"/>

            <open>
                <term from="11:00" to="15:00" description="denní menu"/>
                <term day-of-week="Mon" from="07:30" to="16:00"/>
                <term day-of-week="Tue" from="07:30" to="16:00"/>
                <term day-of-week="Wed" from="07:30" to="16:00"/>
                <term day-of-week="Thu" from="07:30" to="16:00"/>
                <term day-of-week="Fri" from="07:30" to="15:00"/>
            </open>

            <xsl:apply-templates select="//*[@id='daily-menu-container']"/>
        </restaurant>
    </xsl:template>

    <xsl:template match="*[@id='daily-menu-container']">
        <menu>
            <xsl:apply-templates
                    select=".//div[@class='tmi-group']/div[starts-with(@class,'tmi tmi-daily')]"
                    mode="menuitem"/>
        </menu>
    </xsl:template>

    <xsl:template match="div" mode="menuitem">
        <xsl:if
                test="not(contains(., '2dcl točené limonády k obědu'))">
            <meal dish="" category="">
                <xsl:call-template name="dish"/>
                <xsl:call-template name="category"/>
                <xsl:call-template name="order"/>
                <xsl:call-template name="time"/>
                <xsl:call-template name="ref-time"/>


                <xsl:call-template name="title"/>
                <xsl:call-template name="description"/>
                <xsl:call-template name="price"/>
            </meal>
        </xsl:if>
    </xsl:template>

    <xsl:template name="dish">
        <xsl:attribute name="dish">
            <xsl:choose>
                <xsl:when test="count(preceding-sibling::div[contains(@class, 'tmi-daily')]) &lt; 2">soup</xsl:when>
                <xsl:otherwise>dinner</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="category">
        <xsl:attribute name="category">
            <xsl:choose>
                <xsl:when
                        test="count((preceding-sibling::div|.)[div[contains(@class,'tmi-text-group')]//div[@class='tmi-name' and starts-with(normalize-space(.), 'TĚSTOVINY')]]) > 0">
                    30-pasta
                </xsl:when>
                <xsl:when
                        test="count((preceding-sibling::div|.)[div[contains(@class,'tmi-text-group')]//div[@class='tmi-name' and starts-with(normalize-space(.), 'SALÁTY')]]) > 0">
                    50-salad
                </xsl:when>
                <xsl:otherwise>10-normal</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="title">
        <title>
            <xsl:value-of select="div[starts-with(@class, 'tmi-text-group')]//div[starts-with(@class, 'tmi-name')]/."/>
        </title>
    </xsl:template>

    <xsl:template name="description">
    </xsl:template>

    <xsl:template name="price">
        <price>
            <xsl:value-of select="div[starts-with(@class, 'tmi-price')]/."/>
        </price>
    </xsl:template>

    <xsl:template name="order">
        <xsl:attribute name="order">
            <xsl:value-of select="position()"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="time">
        <xsl:attribute name="time">
            <xsl:call-template name="time-format"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="ref-time">
        <xsl:attribute name="ref-time">
            <xsl:call-template name="time-format"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="time-format">
        <xsl:variable name="time">
            <xsl:value-of select="preceding-sibling::div[starts-with(@class,'tmi-group-name')]"/>
        </xsl:variable>
        <xsl:variable name="fixed-time">
            <xsl:choose>
                <xsl:when test="contains($time,'(')">
                    <xsl:value-of
                            select="normalize-space(substring-before($time,'('))"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="normalize-space($time)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:call-template name="string-replace">
            <xsl:with-param name="string">
                <xsl:call-template name="string-replace">
                    <xsl:with-param name="string">
                        <xsl:call-template name="string-replace">
                            <xsl:with-param name="string">
                                <xsl:call-template name="string-replace">
                                    <xsl:with-param name="string">
                                        <xsl:call-template name="string-replace">
                                            <xsl:with-param name="string">
                                                <xsl:call-template name="string-replace">
                                                    <xsl:with-param name="string">
                                                        <xsl:call-template name="string-replace">
                                                            <xsl:with-param name="string">
                                                                <xsl:call-template name="string-replace">
                                                                    <xsl:with-param name="string">
                                                                        <xsl:call-template name="string-replace">
                                                                            <xsl:with-param name="string">
                                                                                <xsl:call-template
                                                                                        name="string-replace">
                                                                                    <xsl:with-param name="string">
                                                                                        <xsl:call-template
                                                                                                name="string-replace">
                                                                                            <xsl:with-param
                                                                                                    name="string">
                                                                                                <xsl:call-template
                                                                                                        name="string-replace">
                                                                                                    <xsl:with-param
                                                                                                            name="string"
                                                                                                            select="concat($fixed-time,' ')"/>
                                                                                                    <xsl:with-param
                                                                                                            name="replace"
                                                                                                            select="'leden '"/>
                                                                                                    <xsl:with-param
                                                                                                            name="with"
                                                                                                            select="'ledna'"/>
                                                                                                </xsl:call-template>
                                                                                            </xsl:with-param>
                                                                                            <xsl:with-param
                                                                                                    name="replace"
                                                                                                    select="'únor '"/>
                                                                                            <xsl:with-param name="with"
                                                                                                            select="'února'"/>
                                                                                        </xsl:call-template>
                                                                                    </xsl:with-param>
                                                                                    <xsl:with-param name="replace"
                                                                                                    select="'březen '"/>
                                                                                    <xsl:with-param name="with"
                                                                                                    select="'března'"/>
                                                                                </xsl:call-template>
                                                                            </xsl:with-param>
                                                                            <xsl:with-param name="replace"
                                                                                            select="'duben '"/>
                                                                            <xsl:with-param name="with"
                                                                                            select="'dubna'"/>
                                                                        </xsl:call-template>
                                                                    </xsl:with-param>
                                                                    <xsl:with-param name="replace" select="'květen '"/>
                                                                    <xsl:with-param name="with" select="'května'"/>
                                                                </xsl:call-template>
                                                            </xsl:with-param>
                                                            <xsl:with-param name="replace" select="'červen '"/>
                                                            <xsl:with-param name="with" select="'června'"/>
                                                        </xsl:call-template>
                                                    </xsl:with-param>
                                                    <xsl:with-param name="replace" select="'červenec '"/>
                                                    <xsl:with-param name="with" select="'července'"/>
                                                </xsl:call-template>
                                            </xsl:with-param>
                                            <xsl:with-param name="replace" select="'srpen '"/>
                                            <xsl:with-param name="with" select="'srpna'"/>
                                        </xsl:call-template>
                                    </xsl:with-param>
                                    <xsl:with-param name="replace" select="'září '"/>
                                    <xsl:with-param name="with" select="'září'"/>
                                </xsl:call-template>
                            </xsl:with-param>
                            <xsl:with-param name="replace" select="'říjen '"/>
                            <xsl:with-param name="with" select="'října'"/>
                        </xsl:call-template>
                    </xsl:with-param>
                    <xsl:with-param name="replace" select="'listopad '"/>
                    <xsl:with-param name="with" select="'listopadu'"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="replace" select="'prosinec '"/>
            <xsl:with-param name="with" select="'prosince'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="string-replace">
        <xsl:param name="string"/>
        <xsl:param name="replace"/>
        <xsl:param name="with"/>

        <xsl:choose>
            <xsl:when test="contains($string, $replace)">
                <xsl:value-of select="substring-before($string, $replace)"/>
                <xsl:value-of select="$with"/>
                <xsl:call-template name="string-replace">
                    <xsl:with-param name="string" select="substring-after($string,$replace)"/>
                    <xsl:with-param name="replace" select="$replace"/>
                    <xsl:with-param name="with" select="$with"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$string"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
