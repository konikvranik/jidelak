<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output indent="yes" method="xml" encoding="UTF-8" />

	<xsl:template match="/">
		<jidelak>
			<xsl:apply-templates select="/jidelak/config|/html" />
		</jidelak>
	</xsl:template>

	<xsl:template match="/jidelak/config|/html">
		<config>
			<xsl:call-template name="restaurant" />
		</config>
	</xsl:template>

	<xsl:template name="restaurant">
		<restaurant version="1.0">
			<id>praha-u-majeru</id>
			<name>Svět Zábavy</name>
			<phone>(+420) 606746380</phone>
			<e-mail>info@kunick.cz</e-mail>
			<web>http://www.lunchtime.cz/svet-zabavy</web>
			<city>Praha 5</city>
			<country>Česká republika</country>
			<address>Radlická 520/117</address>
			<zip>158 00</zip>

			<source time="relative" firstDayOfWeek="Po" encoding="utf8"
				timeOffset="-1" base="day" dateFormat="d.M. y" locale="cs_CZ"
				url="http://www.lunchtime.cz/svet-zabavy/a/denni-menu/" />

			<open>
				<term day-of-week="Po" from="10:00" to="00:00" />
				<term day-of-week="Út" from="10:00" to="00:00" />
				<term day-of-week="St" from="10:00" to="00:00" />
				<term day-of-week="Čt" from="10:00" to="00:00" />
				<term day-of-week="Pá" from="10:00" to="02:00" />
				<term day-of-week="So" from="10:00" to="02:00" />
				<term day-of-week="Ne" from="10:00" to="00:00" />
				<term from="11:00" to="14:30" />
			</open>

			<!-- <xsl:apply-templates select="//div[@id='levy']/div[@class='poledni_menu']" 
				/> -->
			<xsl:apply-templates select="//div[@id='tab_denni_menu']" />
		</restaurant>
	</xsl:template>

	<xsl:template match="div[@id='tab_denni_menu']">
		<menu>
			<xsl:apply-templates select="div/h4" />
		</menu>
	</xsl:template>

	<xsl:template match="h4">
		<xsl:apply-templates select="(./following-sibling::div)[1]//table//tr[td]">
			<xsl:with-param name="date"
				select="concat(//*[@id='tab_denni_menu']/div[1]/div[2]/h3/span[1], //*[@id='tab_denni_menu']/div[1]/div[2]/h3/text()[3])" />
			<xsl:with-param name="pos" select="position()" />
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="table//tr">
		<xsl:param name="date" />
		<xsl:param name="pos" />
		<meal>
			<xsl:attribute name="dish"><xsl:choose>
		<xsl:when test="count(./preceding-sibling::tr[th]) = 1">soup</xsl:when>
		<xsl:otherwise>dinner</xsl:otherwise>
		</xsl:choose></xsl:attribute>
			<xsl:attribute name="order"><xsl:value-of select="position()" /></xsl:attribute>
			<xsl:attribute name="time"><xsl:value-of select="$pos" /></xsl:attribute>
			<xsl:attribute name="ref-time"><xsl:value-of select="$date" /></xsl:attribute>
			<title>
				<xsl:value-of select="td[1]" />
			</title>
			<price>
				<xsl:value-of select="td[@class='edtbl_price']" />
			</price>
		</meal>

	</xsl:template>

</xsl:stylesheet>