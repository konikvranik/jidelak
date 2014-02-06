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
		<restaurant version="1.4">
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
		<xsl:apply-templates select="(./following-sibling::div)[1]//table//tr[*//text()[starts-with(normalize-space(.),'Menu')]]" mode="menu">
			<xsl:with-param name="date"
				select="concat(//*[@id='tab_denni_menu']/div[1]/div[2]/h3/span[1], //*[@id='tab_denni_menu']/div[1]/div[2]/h3/text()[3])" />
			<xsl:with-param name="pos" select="position()" />
		</xsl:apply-templates>
		<xsl:apply-templates select="(./following-sibling::div)[1]//table//tr[*[@class='edtbl_price']/text()]">
			<xsl:with-param name="date"
				select="concat(//*[@id='tab_denni_menu']/div[1]/div[2]/h3/span[1], //*[@id='tab_denni_menu']/div[1]/div[2]/h3/text()[3])" />
			<xsl:with-param name="pos" select="position()" />
		</xsl:apply-templates>
	</xsl:template>

	
	<xsl:template match="table//tr" mode="menu">
		<xsl:param name="date" />
		<xsl:param name="pos" />
		<meal>
			<xsl:attribute name="dish">menu</xsl:attribute>
			<xsl:attribute name="category">1-normal</xsl:attribute>
			<xsl:attribute name="order"><xsl:value-of select="position()" /></xsl:attribute>
			<xsl:attribute name="time"><xsl:value-of select="$pos" /></xsl:attribute>
			<xsl:attribute name="ref-time"><xsl:value-of select="$date" /></xsl:attribute>
			<title>
				<xsl:apply-templates select="(td|th)[1]" />
				<xsl:apply-templates select="following-sibling::tr[1]" mode="multilinemenu" />
			</title>
			<price>
				<xsl:value-of select="(self::tr[*[@class='edtbl_price']/text()]|following-sibling::tr[*[@class='edtbl_price']/text()])[1]/*[@class='edtbl_price']" />
			</price>
		</meal>
	</xsl:template>
	
	<xsl:template match="table//tr">
		<xsl:param name="date" />
		<xsl:param name="pos" />
		<xsl:if test="preceding-sibling::tr[starts-with(normalize-space(*//text()),'Polévky')]">
			<meal>
				<xsl:attribute name="dish"><xsl:choose>
				<xsl:when test=".//*[starts-with(normalize-space(.), 'Menu')]">menu</xsl:when>
				<xsl:when test="./preceding-sibling::tr//*[starts-with(normalize-space(.), 'Hlavní jídla')]">dinner</xsl:when>
				<xsl:when test="./preceding-sibling::tr//*[starts-with(normalize-space(.), 'Polévky')]">soup</xsl:when>
				<xsl:otherwise>soup</xsl:otherwise>
				</xsl:choose></xsl:attribute>
				<xsl:attribute name="category"><xsl:choose>
					<xsl:when test="./preceding-sibling::tr//*[starts-with(., 'Smažená jídla')] and not(td[1]//text()[starts-with(., '1') or starts-with(., '2') or starts-with(., '3') or starts-with(., '4') or starts-with(., '5') or starts-with(., '6') or starts-with(., '7') or starts-with(., '8') or starts-with(., '9') or starts-with(., '0')])">3-salad</xsl:when>
					<xsl:when test="./preceding-sibling::tr//text()[starts-with(., 'Smažená jídla')]">2-fried</xsl:when>
					<xsl:otherwise>1-normal</xsl:otherwise>
				</xsl:choose></xsl:attribute>
				<xsl:attribute name="order"><xsl:value-of select="position()" /></xsl:attribute>
				<xsl:attribute name="time"><xsl:value-of select="$pos" /></xsl:attribute>
				<xsl:attribute name="ref-time"><xsl:value-of select="$date" /></xsl:attribute>
				<title>
					<xsl:apply-templates select="(td|th)[1]" />
					<xsl:apply-templates select="following-sibling::tr[1]" mode="multiline" />
				</title>
				<price>
					<xsl:value-of select="*[@class='edtbl_price']" />
				</price>
			</meal>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="span[@class='show-today']">
	</xsl:template>

	<xsl:template match="tr" mode="multiline">
		<xsl:if test="not(starts-with(*, 'Smažená jídla') or starts-with(*, 'Hlavní jídla') or starts-with(*, 'Polévky') or starts-with(*, 'Menu')or (*[@class='edtbl_price']/text()))">
			<xsl:text>&#10;</xsl:text>
			<xsl:apply-templates select="(td|th)[1]" />
			<xsl:apply-templates select="following-sibling::tr[1]" mode="multiline" />
		</xsl:if>
	</xsl:template>

	<xsl:template match="tr" mode="multilinemenu">
		<xsl:if test="not(starts-with(normalize-space(*//text()), 'Smažená jídla') or starts-with(normalize-space(*//text()), 'Hlavní jídla') or starts-with(normalize-space(*//text()), 'Polévky') or starts-with(normalize-space(*//text()), 'Menu'))">
			<xsl:text>&#10;</xsl:text>
			<xsl:apply-templates select="(td|th)[1]" />
			<xsl:apply-templates select="following-sibling::tr[1]" mode="multilinemenu" />
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>