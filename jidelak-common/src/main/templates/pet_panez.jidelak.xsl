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
			<id>praha-pet_penez</id>
			<name>Restaurace pět peněz</name>
			<phone>(+420) 220 808 376</phone>
			<e-mail>info@restauracepetpenez.cz</e-mail>
			<web>http://www.restauracepetpenez.cz/</web>
			<city>Praha 7</city>
			<country>Česká republika</country>
			<address>Dělnická 30</address>
			<zip>170 00</zip>

			<source time="absolute" firstDayOfWeek="Po" encoding="utf8"
				dateFormat="E - d.M.y" locale="cs_CZ"
				url="http://www.restauracepetpenez.cz/homepage/poledni-menicka" />

			<open>
				<term day-of-week="Po" from="11:00" to="24:00" />
				<term day-of-week="Út" from="11:00" to="24:00" />
				<term day-of-week="St" from="11:00" to="24:00" />
				<term day-of-week="Čt" from="11:00" to="24:00" />
				<term day-of-week="Pá" from="11:00" to="24:00" />
				<term day-of-week="So" from="12:00" to="24:00" />
				<term day-of-week="Ne" from="12:00" to="24:00" />
			</open>

			<!-- <xsl:apply-templates select="//div[@id='levy']/div[@class='poledni_menu']" 
				/> -->
			<xsl:apply-templates select="//body/table" />
		</restaurant>
	</xsl:template>

	<xsl:template match="table">
		<menu>
			<xsl:apply-templates select=".//tr[position() &gt; 1 and count(td) = 4]" />
		</menu>
	</xsl:template>

	<xsl:template match="tr">
		<meal>
			<xsl:attribute name="dish"><xsl:choose>
		<xsl:when test="contains( td[1],'.')">dinner</xsl:when>
		<xsl:otherwise>soup</xsl:otherwise>
		</xsl:choose></xsl:attribute>
			<xsl:attribute name="order"><xsl:value-of select="position()" /></xsl:attribute>
			<xsl:attribute name="time"><xsl:value-of select="(preceding-sibling::tr[count(td[strong]) = 1])[last()]/td/strong/." /></xsl:attribute>
			<title>
				<xsl:value-of select="td[3]" />
			</title>
			<price>
				<xsl:value-of select="td[4]" />
			</price>
		</meal>

	</xsl:template>

</xsl:stylesheet>
