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
		<restaurant version="1.1">
			<id>praha-nova-hospoda</id>
			<name>Nová Hospoda</name>
			<phone>(+420) 777826112, 608511616</phone>
			<web>http://novahospoda.eu/</web>
			<city>Praha 5</city>
			<country>Česká republika</country>
			<address>Butovická 24/64</address>
			<zip>150 00</zip>

			<source time="relative" firstDayOfWeek="Po" encoding="utf8"
				timeOffset="-1" base="day" dateFormat="d.M. y" locale="cs_CZ"
				url="http://www.lunchtime.cz/nova-hospoda/a/denni-menu/" />

			<open>
				<term day-of-week="Po" from="10:00" to="23:00" />
				<term day-of-week="Út"  from="10:00" to="23:00" />
				<term day-of-week="St" from="10:00" to="23:00" />
				<term day-of-week="Čt" from="10:00" to="23:00" />
				<term day-of-week="Pá" from="10:00" to="00:00" />
				<term day-of-week="So" from="10:30" to="00:00" />
				<term day-of-week="Ne" from="10:30" to="23:00" />
				<term from="10:00" to="14:30" />
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
		<xsl:apply-templates select="(./following-sibling::div)[1]//table//tr[td[@class='edtbl_price']/text()]">
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
			<xsl:when test=".//*[normalize-space(starts-with(text()), 'Menu')]">menu</xsl:when>
			<xsl:when test="./preceding-sibling::tr//*[starts-with(normalize-space(text()), 'Hlavní jídla')]">dinner</xsl:when>
			<xsl:when test="./preceding-sibling::tr//*[starts-with(normalize-space(text()), 'Polévky')]">soup</xsl:when>
			<xsl:otherwise></xsl:otherwise>
			</xsl:choose></xsl:attribute>
			<xsl:attribute name="category"><xsl:choose>
				<xsl:when test="./preceding-sibling::tr//*[starts-with(text(), 'Smažená jídla')] and not(starts-with(td[1], '1') or starts-with(td[1], '2') or starts-with(td[1], '3') or starts-with(td[1], '4') or starts-with(td[1], '5') or starts-with(td[1], '6') or starts-with(td[1], '7') or starts-with(td[1], '8') or starts-with(td[1], '9') or starts-with(td[1], '0'))">3-salad</xsl:when>
				<xsl:when test="./preceding-sibling::tr//*[starts-with(text(), 'Smažená jídla')]">2-fried</xsl:when>
				<xsl:otherwise>1-normal</xsl:otherwise>
			</xsl:choose></xsl:attribute>
			<xsl:attribute name="order"><xsl:value-of select="position()" /></xsl:attribute>
			<xsl:attribute name="time"><xsl:value-of select="$pos" /></xsl:attribute>
			<xsl:attribute name="ref-time"><xsl:value-of select="$date" /></xsl:attribute>
			<title>
				<xsl:apply-templates select="td[1]" />
				<xsl:apply-templates select="following-sibling::tr[1]" mode="multiline" />
			</title>
			<price>
				<xsl:value-of select="td[@class='edtbl_price']" />
			</price>
		</meal>

	</xsl:template>
	
	<xsl:template match="span[@class='show-today']">
	</xsl:template>

	<xsl:template match="tr" mode="multiline">
		<xsl:if test="not(starts-with(td, 'Smažená jídla') or starts-with(td, 'Hlavní jídla') or starts-with(td, 'Polévky') or (td[@class='edtbl_price']/text()))">
			<xsl:text>&#10;</xsl:text>
			<xsl:apply-templates select="td[1]" />
			<xsl:apply-templates select="following-sibling::tr[1]" mode="multiline" />
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>