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
		<restaurant>
			<id>praha-gastronom</id>
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
				<term day-of-week="Po" from="10:30" to="14:00" />
				<term day-of-week="Út" from="10:30" to="14:00" />
				<term day-of-week="St" from="10:30" to="14:00" />
				<term day-of-week="Čt" from="10:30" to="14:00" />
				<term day-of-week="Pá" from="10:30" to="14:00" />
			</open>

			<!-- <xsl:apply-templates select="//div[@id='levy']/div[@class='poledni_menu']" 
				/> -->
			<xsl:apply-templates select="//*[@id='incenterpage']/table" />
		</restaurant>
	</xsl:template>



	<xsl:template match="*[@id='incenterpage']/table">
		<menu>
			<xsl:apply-templates select="//tr">
			</xsl:apply-templates>
		</menu>
	</xsl:template>

	<xsl:template match="table//tr">
		<xsl:if test="td[1]/text() and td[last()]/text()">
			<meal>
				<xsl:attribute name="dish"><xsl:choose>
			<xsl:when
					test="./preceding-sibling::tr[starts-with(td/.,  'HLAVNÍ JÍDLA')]">dinner</xsl:when>
		<xsl:otherwise>soup</xsl:otherwise>
		</xsl:choose></xsl:attribute>
				<xsl:attribute name="category"><xsl:choose>
			<xsl:when test="./preceding-sibling::tr[starts-with(td/.,  'STEAKY')]">steak</xsl:when>
			<xsl:when test="./preceding-sibling::tr[starts-with(td/.,  'TĚSTOVINY')]">pasta</xsl:when>
			<xsl:when
					test="./preceding-sibling::tr[starts-with(td/.,  'JÍDLA PRO DÁMY')]">ladies</xsl:when>
			<xsl:when test="./preceding-sibling::tr[starts-with(td/.,  'SALÁTY ')]">salad</xsl:when>
		<xsl:otherwise>normal</xsl:otherwise>
		</xsl:choose></xsl:attribute>
				<xsl:attribute name="order"><xsl:value-of select="position()" /></xsl:attribute>
				<title>
					<xsl:value-of select="td[1]" />
				</title>
				<price>
					<xsl:value-of select="td[last()]" />
				</price>
			</meal>
		</xsl:if>

	</xsl:template>

</xsl:stylesheet>