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
		<restaurant version="1.6">
			<id>praha-u-majeru</id>
			<name>Restaurace U Majerů</name>
			<phone>(+420) 251 612 775</phone>
			<web>http://www.restauraceumajeru.cz/</web>
			<city>Praha 5</city>
			<country>Česká republika</country>
			<address>U Jinonického rybníčka 3a/602</address>
			<zip>158 00</zip>

			<source time="relative" firstDayOfWeek="Po" encoding="utf8"
				timeOffset="-1" base="day" dateFormat="d.M. y" locale="cs_CZ"
				url="http://www.lunchtime.cz/umajeru/a/denni-menu/" />

			<open>
				<term from="10:30" to="14:00" description="denní menu" />
				<term day-of-week="Po" from="10:30" to="21:00" />
				<term day-of-week="Út" from="10:30" to="21:00" />
				<term day-of-week="St" from="10:30" to="21:00" />
				<term day-of-week="Čt" from="10:30" to="21:00" />
				<term day-of-week="Pá" from="10:30" to="21:00" />
				<term day-of-week="So" from="17:30" to="21:00" />
				<term day-of-week="Ne" from="17:30" to="21:00" />
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
		<xsl:apply-templates
			select="(./following-sibling::div)[1]//table//tr[*[@class='edtbl_price']/text()]">
			<xsl:with-param name="date"
				select="concat(//*[@id='tab_denni_menu']/div[1]/div[2]/h3/span[1], //*[@id='tab_denni_menu']/div[1]/div[2]/h3/text()[3])" />
			<xsl:with-param name="pos" select="position()" />
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="table//tr">
		<xsl:param name="date" />
		<xsl:param name="pos" />
		<meal>
			<xsl:attribute name="dish"><xsl:apply-templates
				select="." mode="dish" /></xsl:attribute>
			<xsl:attribute name="order"><xsl:value-of select="position()" /></xsl:attribute>
			<xsl:attribute name="time"><xsl:value-of select="$pos" /></xsl:attribute>
			<xsl:attribute name="ref-time"><xsl:value-of select="$date" /></xsl:attribute>
			<title>
				<xsl:apply-templates select="preceding-sibling::tr[1]"
					mode="multiline" />
				<xsl:apply-templates select="(td|th)[1]" />
			</title>
			<price>
				<xsl:value-of select="(th|td)[@class='edtbl_price']" />
			</price>
		</meal>

	</xsl:template>

	<xsl:template match="span[@class='show-today']">
	</xsl:template>

	<xsl:template match="tr" mode="dish">
		<xsl:choose>
			<xsl:when
				test="not(preceding-sibling::tr[1]//*[@class='edtbl_price']/text()[normalize-space(.) != '']|preceding-sibling::tr[1]//text()[starts-with(normalize-space(.), 'Smažená jídla') or starts-with(normalize-space(.), 'Hotová jídla') or starts-with(normalize-space(.), 'Polévka')])">
				<xsl:apply-templates select="preceding-sibling::tr[1]"
					mode="dish" />
			</xsl:when>
			<xsl:when test="*//text()[starts-with(normalize-space(.), 'Menu')]">
				menu
			</xsl:when>
			<xsl:when
				test="preceding-sibling::tr//text()[starts-with(normalize-space(.), 'Hotová jídla')]">
				dinner
			</xsl:when>
			<xsl:when
				test="preceding-sibling::tr//text()[starts-with(normalize-space(.), 'Polévka')]">
				soup
			</xsl:when>
			<xsl:otherwise>
				dinner
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="tr" mode="multiline">
		<xsl:if
			test="not((*|*//text())[starts-with(normalize-space(.), 'Smažená jídla')] or (*|*//text())[starts-with(normalize-space(.), 'Hotová jídla')] or (*|*//text())[starts-with(normalize-space(.), 'Polévka')] or *[@class='edtbl_price']/text())">
			<xsl:apply-templates select="preceding-sibling::tr[1]"
				mode="multiline" />
			<xsl:apply-templates select="(td|th)[1]" mode="menu" />
			<xsl:text>&#10;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="text()" mode="menu">
		<xsl:value-of select="substring-after(.,'Menu ')" />
	</xsl:template>
</xsl:stylesheet>